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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents platform bundle.
 *
 * Platform bundle is a set of files for a specific platform.
 */
public class Bundle {
    @NotNull private final String m_displayName;
    @Nullable private BundleMetadata m_metadata = null;

    /**
     * Construct bundle.
     * @param displayName Name to display in UI.
     */
    public Bundle(@NotNull String displayName) {
        m_displayName = displayName;
    }

    /** Bundle display name. */
    @NotNull
    public String getDisplayName() {
        return m_displayName;
    }

    /** Bundle metadata. */
    @Nullable
    public BundleMetadata getMetadata() {
        return m_metadata;
    }

    /** Set bundle metadata. */
    public void setMetadata(@Nullable BundleMetadata metadata) {
        m_metadata = metadata;
    }

    /**
     * Return true if this bundle version newer than other bundle version.
     * @param other Bundle to compare with.
     * @return boolean
     */
    public boolean isNewerThan(@Nullable Bundle other) {
        if (m_metadata == null)
            return false;
        else if (other == null) {
            return true;
        }
        return isNewerThan(other.getMetadata());
    }

    public boolean isNewerThan(@Nullable BundleMetadata metadata) {
        return m_metadata != null && m_metadata.isNewerThan(metadata);
    }
}
