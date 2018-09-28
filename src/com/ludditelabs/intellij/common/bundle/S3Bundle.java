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

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;


/**
 * This class represents remote platform bundle located in the
 * Amazon S3 bucket.
 */
public class S3Bundle extends RemoteBundle {
    private static final String S3_URL = "https://s3.amazonaws.com/";

    /**
     * Construct S3 remote bundle.
     *
     * It constructs bundle URL:
     * {@code https://s3.amazonaws.com/<bucket>[/<folder>]/<name>}
     *
     * @param bucket Name of the S3 bucket.
     * @param folder Folder in the S3 bucket.
     * @param displayName Bundle display name.
     */
    public S3Bundle(String bucket, String folder, @NotNull String displayName) {
        super(buildUrl(bucket, folder), displayName);
    }

    private static String buildUrl(String bucket, String folder) {
        String url = System.getProperty(
            "ludditelabs.bundle.s3url", S3_URL).trim();

        // Force default URL for empty values.
        if (url.isEmpty())
            url = S3_URL;

        StringBuilder builder = new StringBuilder();
        builder.append(clean(url) + "/");

        append(builder, System.getProperty(
            "ludditelabs.bundle.bucket", bucket));

        append(builder, System.getProperty(
            "ludditelabs.bundle.folder", folder));

        return builder.toString();
    }

    private static String clean(String part) {
        String str = StringUtils.stripStart(part.trim(), "/");
        return StringUtils.stripEnd(str, "/");
    }

    private static void append(StringBuilder builder, String part) {
        part = clean(part);
        if (!part.isEmpty()) {
            builder.append(part);
            builder.append('/');
        }
    }
}
