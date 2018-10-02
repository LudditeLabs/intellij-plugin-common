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

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BundleInfoDialog extends DialogWrapper {
    @NotNull private final BundleMetadata m_localMetadata;
    @NotNull private final BundleMetadata m_remoteMetadata;
    public BundleInfoDialog(@NotNull String title,
                            @NotNull BundleMetadata localMetadata,
                            @NotNull BundleMetadata remoteMetadata) {
        super(null);

        m_localMetadata = localMetadata;
        m_remoteMetadata = remoteMetadata;

        init();
        setTitle(title);
        setOKButtonText("Update");
        setCancelButtonText("Remind Me Later");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        BundleInfoPanel panel = new BundleInfoPanel(
            m_localMetadata, m_remoteMetadata);
        return panel.getComponent();
    }
}
