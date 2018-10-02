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

import com.intellij.util.text.VersionComparatorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Platform bundle metadata.
 *
 * This class provides platform bundle info.
 */
public class BundleMetadata {
    public String dist = null;
    public String timestamp = null;
    public String version = null;
    public String pluginVersion = null;
    public String message = null;
    public ArrayList<String> changes = null;
    public long lastModified = 0;

    public String getVersion() {
        return version == null ? "N/A" : version;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public boolean hasChanges() {
        return changes != null && !changes.isEmpty();
    }

    public boolean isValid() {
        return dist != null && version != null;
    }
    /**
     * Return true if this metadata version is newer than
     * version of the given metadata.
     *
     * @param other Metadata to compare with.
     * @return boolean
     */
    public boolean isNewerThan(@Nullable BundleMetadata other) {
        return other == null || isNewerThan(other.version);
    }

    /**
     * Return true if this metadata version is newer than given version.
     *
     * @param version Version string to compare with.
     * @return boolean
     */
    public boolean isNewerThan(@NotNull String version) {
        return VersionComparatorUtil.compare(this.version, version) > 0;
    }

    /**
     * Return true if this metadata version is older than
     * version of the given metadata.
     *
     * @param other Metadata to compare with.
     * @return boolean
     */
    public boolean isOlderThan(@Nullable BundleMetadata other) {
        return other != null && isOlderThan(other.version);
    }

    /**
     * Return true if this metadata version is older than given version.
     *
     * @param version Version string to compare with.
     * @return boolean
     */
    public boolean isOlderThan(@NotNull String version) {
        return VersionComparatorUtil.compare(this.version, version) < 0;
    }
}
