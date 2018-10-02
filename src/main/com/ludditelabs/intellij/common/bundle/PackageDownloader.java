/*
 * Copyright 2018 Luddite Labs Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ludditelabs.intellij.common.bundle;

import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.ludditelabs.intellij.common.DownloadUtils;
import com.ludditelabs.intellij.common.Utils;
import com.ludditelabs.intellij.common.ZipUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class PackageDownloader {
    protected static final Logger LOG = Logger.getInstance("ludditelabs.bundle.PackageDownloader");

    @NotNull private final Updater m_updater;
    @NotNull private final BundleMetadata m_metadata;
    @Nullable private final ProgressIndicator m_indicator;

    public PackageDownloader(@NotNull Updater updater,
                             @NotNull BundleMetadata metadata,
                             @Nullable ProgressIndicator indicator) {
        m_updater = updater;
        m_metadata = metadata;
        m_indicator = indicator;
    }

    // TODO: use FileUtil.createTempFile() instead of File.createTempFile
    private File getTempFilename() throws IOException {
        String[] parts = m_metadata.dist.split(".");
        String ext = parts.length > 1 ? parts[1] : "";
        return File.createTempFile("bundle", ext);
    }

    private String doDownload() throws IOException {
        String url;

        // Seems this is an URL so use as is.
        if (m_metadata.dist.contains(":/")) {
            url = m_metadata.dist;
        }
        // otherwise construct URL:
        // <base>/<platform>/<arch>/<plugin version>/<dist filename>
        // For more info see s3bundle project docs.
        // NOTE: We don't put '/' between <base> and <platform> because
        // <base> already has it.
        else {
            RemoteBundle bundle = m_updater.getRemoteBundle();
            url = String.format("%s%s/%s/%s/%s",
                bundle.getBaseUrl(),
                Utils.getPlatform(),
                bundle.getArch(),
                m_metadata.pluginVersion,
                m_metadata.dist);
        }

        if (m_indicator != null)
            m_indicator.setText("Downloading platform bundle");

        final File file = getTempFilename();
        final String filename = file.getAbsolutePath();

        LOG.debug("Downloading ", url, " -> ",filename);

        DownloadUtils.downloadToFile(
            url, file, m_indicator,
            true,
            "Can't download file",
            null);

        return filename;
    }

    private void doUnpack(String fileName, String outPath) throws IOException {
        File zip_file = new File(fileName);
        File out_dir = new File(outPath);

        LOG.debug("Unpacking ", fileName, " -> ", outPath);

        if (m_indicator != null)
            m_indicator.setText("Unpacking platform bundle");

        ZipUtils.unzipAtomic(zip_file, out_dir, m_indicator, false);

        File meta_file = Paths.get(outPath, "metadata.json").toFile();
        LOG.debug("Saving ", meta_file.getAbsolutePath());

        try (FileWriter writer = new FileWriter(meta_file)) {
            Gson gson = new Gson();
            gson.toJson(m_metadata, writer);
        }
    }

    public String download() throws IOException {
        try {
            m_updater.setBusy(true);
            return doDownload();
        }
        finally {
            m_updater.setBusy(false);
        }
    }

    public void unpack(String fileName, String outPath) throws IOException {
        try {
            m_updater.setBusy(true);
            doUnpack(fileName, outPath);
        }
        finally {
            m_updater.setBusy(false);
        }
    }

    public void downloadAndUnpack(String outPath) throws IOException {
        try {
            m_updater.setBusy(true);
            doUnpack(doDownload(), outPath);
        } finally {
            m_updater.setBusy(false);
        }
    }
}
