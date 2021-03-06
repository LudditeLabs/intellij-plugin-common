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

package com.ludditelabs.intellij.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Locale;

// Inspired by com.intellij.platform.templates.github.DownloadUtil
public class DownloadUtils {
    private static final Logger LOG = Logger.getInstance("ludditelabs.common.DownloadUtils");

    private static String sizeToString(int size) {
        if (size < 0) {
            return "N/A";
        }
        final int kilo = 1024;
        if (size < kilo) {
            return String.format(Locale.US, "%d bytes", size);
        }
        if (size < kilo * kilo) {
            return String.format(Locale.US, "%.1f kB", size / (1.0 * kilo));
        }
        return String.format(Locale.US, "%.1f MB", size / (1.0 * kilo * kilo));
    }

    // size in bytes.
    private static void setProgress(@Nullable final ProgressIndicator indicator,
                                    @Nullable String text, int size) {
        if (indicator != null && text != null) {
            String txt = text + " (" + sizeToString(size) + ")";
            indicator.setText(txt);
        }
    }

    public static void download(@NotNull String url,
                                @NotNull final OutputStream output,
                                @Nullable final ProgressIndicator indicator,
                                final boolean addProgress,
                                final boolean showDownloadingFile,
                                @Nullable final String errorMessage,
                                @Nullable final HttpRequests.RequestProcessor<Void> extraProcessor) throws IOException {

        final String progress_text = indicator != null ? indicator.getText() : null;

        if (showDownloadingFile && indicator != null) {
            try {
                String[] parts = URI.create(url).getPath().split("/");
                if (parts.length > 0)
                    indicator.setText2("Downloading " + parts[parts.length - 1]);
            }
            catch (IllegalArgumentException e) {
                // Don't show extra text if something is wrong.
            }
        }

        HttpRequests.request(url).productNameAsUserAgent()
            .connect(new HttpRequests.RequestProcessor<Void>() {
                @Override
                public Void process(@NotNull HttpRequests.Request request) throws IOException {
                    try {
                        int sz = request.getConnection().getContentLength();
                        if (addProgress)
                            setProgress(indicator, progress_text, sz);
                        NetUtils.copyStreamContent(indicator, request.getInputStream(), output, sz);
                        if (extraProcessor != null)
                            extraProcessor.process(request);
                    }
                    catch (IOException e) {
                        LOG.debug(e);
                        HttpURLConnection conn = (HttpURLConnection)request.getConnection();

                        String msg = (errorMessage == null || errorMessage.isEmpty() ? "" : errorMessage + ": ") +
                            conn.getResponseCode() + " " +
                            conn.getResponseMessage();
                        throw new IOException(msg, e);
                    }
                    return null;
                }
            });
    }

    public static void download(@NotNull String url,
                                @NotNull final OutputStream output,
                                @Nullable final ProgressIndicator indicator,
                                final boolean addProgress,
                                @Nullable final String errorMessage,
                                @Nullable final HttpRequests.RequestProcessor<Void> extraProcessor) throws IOException {
        download(url, output, indicator, addProgress, false, errorMessage, extraProcessor);
    }

    public static String downloadToString(@NotNull String url,
                                          @Nullable final ProgressIndicator indicator,
                                          final boolean addProgress,
                                          @Nullable final String errorMessage,
                                          @Nullable final HttpRequests.RequestProcessor<Void> extraProcessor) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        download(url, out, indicator, addProgress, errorMessage, extraProcessor);
        return out.toString();
    }

    public static void downloadToFile(@NotNull String url,
                                      @NotNull File outFile,
                                      @Nullable final ProgressIndicator indicator,
                                      final boolean addProgress,
                                      @Nullable final String errorMessage,
                                      @Nullable final HttpRequests.RequestProcessor<Void> extraProcessor) throws IOException {
        final FileOutputStream out = new FileOutputStream(outFile);
        download(url, out, indicator, addProgress, errorMessage, extraProcessor);
    }
}
