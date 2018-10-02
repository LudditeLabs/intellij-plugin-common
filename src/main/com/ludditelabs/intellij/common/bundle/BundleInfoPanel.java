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

import javax.swing.*;

public class BundleInfoPanel {
    private JPanel content;
    private JLabel infoLabel;
    private JLabel currentVerLabel;
    private JLabel newVerLabel;
    private JLabel titleLabel;

    public BundleInfoPanel(@NotNull BundleMetadata localMetadata,
                           @NotNull BundleMetadata remoteMetadata) {

        titleLabel.setText(String.format(
            "<html>New version <b>%s</b> is available!</html>",
            remoteMetadata.getVersion()));

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");

        String msg = remoteMetadata.getMessage();
        if (!msg.isEmpty()) {
            builder.append("<p>").append(remoteMetadata.message).append("</p>");
        }

        if (remoteMetadata.hasChanges()) {
            builder.append("Highlights:");
            builder.append("<ul>");
            for (String change : remoteMetadata.changes) {
                builder.append("<li>").append(change).append("</li>");
            }
            builder.append("</ul>");
        }
        builder.append("</html>");

        infoLabel.setText(builder.toString());
        currentVerLabel.setText(localMetadata.getVersion());
        newVerLabel.setText(remoteMetadata.getVersion());
    }

    public JComponent getComponent() {
        return content;
    }
}
