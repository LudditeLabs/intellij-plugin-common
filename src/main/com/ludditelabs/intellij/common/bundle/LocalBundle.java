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
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.diagnostic.Logger;
import com.ludditelabs.intellij.common.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Paths;


/**
 * This class represents locally installed platform bundle.
 *
 * Local bundle is a directory with at least two files:
 * <ul>
 *     <li>{@code metadata.json} - bundle metadata.</li>
 *     <li>{@code <name>.exe} or {@code <name>.bin} - bundle executable.</li>
 * </ul>
 */
public class LocalBundle extends Bundle {
    protected static final Logger LOG = Logger.getInstance("ludditelabs.bundle");

    private String m_bundlePath;
    private String m_exePath;

    /**
     * Construct bundle.
     *
     * It construct bundle paths and loads metadata if exists.
     *
     * Bundle paths:
     * <ul>
     *     <li>Bundle directory is {@code <rootPath>/<name>-pkg/}</li>
     *     <li>Bundle executable is {@code <rootPath>/<name>-pkg/<name>(.exe|.bin)}.</li>
     * </ul>
     *
     * @param rootPath Root path where bundle is unpacked/installed.
     * @param name Name of the bundle.
     */
    public LocalBundle(@NotNull String rootPath, @NotNull String name,
                       @NotNull String displayName) {
        super(displayName);
        setup(rootPath, name + "-pkg", name);
    }

    // Construct paths and load metadata.
    private void setup(String rootPath, String bundleDir, String exeName) {
        m_bundlePath = getPath(rootPath, bundleDir);
        m_exePath = Utils.exeFilename(getPath(m_bundlePath, exeName));
        reloadMetadata();
    }

    // Helper method to load metadata from 'metadata.json' file.
    private BundleMetadata loadMetadata() {
        BundleMetadata meta = null;
        String path = getPath(m_bundlePath, "metadata.json");

        try {
            FileInputStream in = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(in);
            Gson gson = new Gson();
            meta = gson.fromJson(reader, BundleMetadata.class);
        }
        catch (JsonSyntaxException e) {
            LOG.error(e);
        }
        catch (FileNotFoundException e) {
            LOG.debug(String.format("Can't find platform bundle metadata %s", path));
        }

        return meta;
    }

    // Helper method to construct paths.
    protected static String getPath(String basePath, String... path) {
        return Paths.get(basePath, path).toAbsolutePath().toString();
    }

    /** Reload bundle metadata if exists. */
    public final void reloadMetadata() {
        setMetadata(null);
        File f = new File(m_exePath);
        if (f.exists() && f.isFile())
            setMetadata(loadMetadata());
    }

    /** Return true if bundle exists. */
    public boolean isExist() {
        return getMetadata() != null;
    }

    /** Bundle root path. */
    public String getBundlePath() {
        return m_bundlePath;
    }

    /** Bundle executable path. */
    public String getExePath() {
        return m_exePath;
    }
}
