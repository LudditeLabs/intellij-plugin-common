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

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AppUIUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;

/**
 * Base class for the application service to install and update platform bundle.
 *
 * It extend Updater class with the following features:
 * <ul>
 *     <li>Show notifications if new version is available.</li>
 *     <li>Show 'new version' dialog.</li>
 *     <li>Download and install bundle from notifications.</li>
 * </ul>
 *
 * How to subclass:
 * <ul>
 *     <li>
 *         Subclass from the BundleManager and configure labels and other
 *         bundle specific parameters.
 *     </li>
 *     <li>
 *         Add to plugin.xml:
 *         <pre>
 *         <code>
 *          <applicationService
 *            serviceInterface="your.plugin.BundleManagerImpl"
 *            serviceImplementation="your.plugin.BundleManagerImpl" />
 *         </code>
 *         </pre>
 *     </li>
 *     <li>
 *         Optionally implement helper getInstance() method:
 *         <pre>
 *         <code>
 *          public static BundleManagerImpl getInstance() {
 *              return ServiceManager.getService(BundleManagerImpl.class);
 *          }
 *         </code>
 *         </pre>
 *     </li>
 * </ul>
 */
public class BundleManager extends Updater {
    public static class NotifierAdapter implements Updater.Notifier {
        @Override
        public void canceled() {

        }

        @Override
        public void ioError(IOException e) {

        }

        @Override
        public void stateChanged(boolean busy) {

        }

        @Override
        public void metadataDownloaded(@NotNull BundleMetadata metadata) {

        }

        @Override
        public void unpacked() {

        }
    }

    private NotificationGroup m_releaseGroup;
    private NotificationGroup m_infoGroup;
    private NotificationGroup m_errorGroup;
    private Notification m_notification = null;

    @NotNull private String m_bundleName = "bundle";
    @NotNull private String m_unsupportedPlatformText = "This platform is not supported!";
    @NotNull private String m_firstDownloadText = "You need to download Platform Bundle.";
    @NotNull private String m_newVersionText = "New version is available!";
    @NotNull private String m_afterUpdateText = "Platform Bundle is updated.";
    @NotNull private String m_afterFirstDownloadText = "Platform Bundle is installed.";
    @NotNull private String m_infoDialogTitle = "Platform Bundle Update";
    @NotNull private PlatformChecker m_platformChecker = new PlatformChecker();

    /**
     * Construct manager.
     *
     * Do nothing by default.
     */
    public BundleManager(@NotNull String pluginVersion,
                         @NotNull RemoteBundle remoteBundle,
                         @NotNull LocalBundle localBundle) {
        super(pluginVersion, remoteBundle, localBundle);

        m_bundleName = localBundle.getDisplayName();

        m_releaseGroup = new NotificationGroup(
            m_bundleName + " Bundle Release",
            NotificationDisplayType.STICKY_BALLOON, true);

        m_infoGroup = new NotificationGroup(
            m_bundleName + " Bundle",
            NotificationDisplayType.BALLOON, true);

        m_errorGroup = new NotificationGroup(
            m_bundleName + " Error",
            NotificationDisplayType.STICKY_BALLOON,
            true
        );
    }

    // Updater API

    public void setBusy(boolean state) {
        if (m_notification != null) {
            m_notification.expire();
            m_notification = null;
        }
        super.setBusy(state);
    }

    // Display text

    /**
     * Set text to display in the notification if platform is not supported.
     * @param text Text to display.
     */
    public void setUnsupportedPlatformText(@NotNull String text) {
        m_unsupportedPlatformText = text;
    }

    /**
     * Set text to display in the notification if no bundle is installed yet.
     * @param text Text to display.
     */
    public void setFirstDownloadText(@NotNull String text) {
        m_firstDownloadText = text;
    }

    /**
     * Set text to display in the notification if new version is available.
     * @param text Text to display.
     */
    public void setNewVersionText(@NotNull String text) {
        m_newVersionText = text;
    }

    /**
     * Set text to display in the notification after bundle is updated.
     * @param text Text to display.
     */
    public void setAfterUpdateText(@NotNull String text) {
        m_afterUpdateText = text;
    }

    /**
     * Set text to display in the notification after first bundle installation.
     * @param text Text to display.
     */
    public void setAfterFirstDownloadText(@NotNull String text) {
        m_afterFirstDownloadText = text;
    }

    /**
     * Set title for the update dialog.
     * @param text Title text.
     */
    public void setInfoDialogTitle(@NotNull String text) {
        m_infoDialogTitle = text;
    }

    // Bundle manager API.

    private void afterDownload(boolean isFirst) {
        Notification n = m_infoGroup.createNotification(
            m_bundleName,
            isFirst ? m_afterFirstDownloadText : m_afterUpdateText,
            NotificationType.INFORMATION, null);
        Notifications.Bus.notify(n);
    }

    private void showNewVersionDialog(final BundleMetadata metadata) {
        final BundleInfoDialog dlg = new BundleInfoDialog(
            m_infoDialogTitle,
            getLocalBundle().getMetadata(), metadata);
        dlg.show();

        if (dlg.getExitCode() == BundleInfoDialog.OK_EXIT_CODE) {
            download(metadata, new Runnable() {
                @Override
                public void run() {
                    afterDownload(false);
                }
            });
        }
    }

    private void showNewVersionNotification(@Nullable final Project project,
                                            final BundleMetadata metadata) {
        String html = "<html>" + m_newVersionText + "<br/>" +
            "Click <a href='#'>here</a> for more info.<br/><html>";

        Notification n = m_releaseGroup.createNotification(
            m_bundleName, html, NotificationType.INFORMATION, new NotificationListener.Adapter() {
                @Override
                protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
                    notification.expire();
                    showNewVersionDialog(metadata);
                }
            });
        Notifications.Bus.notify(n, project);
    }

    /**
     * Show download notification if no bundle is installed yet.
     */
    public void showFirstDownloadNotification(@Nullable final Project project) {
        AppUIUtil.invokeOnEdt(new Runnable() {
            @Override
            public void run() {
                String html = "<html>" + m_firstDownloadText
                    + "<br/>Click <a href='#'>here</a> to download.</html>";

                // Download bundle on click and show final message.
                m_notification = m_releaseGroup.createNotification(
                    m_bundleName, html, NotificationType.INFORMATION, new NotificationListener.Adapter() {
                        @Override
                        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
                            download();
                        }
                    });
                Notifications.Bus.notify(m_notification, project);
            }
        });
    }

    /**
     * Show notification if platform is not supported.
     */
    public void showUnsupportedPlatformNotification(final Project project) {
        AppUIUtil.invokeOnEdt(new Runnable() {
            @Override
            public void run() {
                String html = "<html>" + m_unsupportedPlatformText + "</html>";
                showErrorNotification(m_bundleName, html, project, null);
            }
        });
    }

    public Notification createErrorNotification(@NotNull final String title,
                                                @NotNull final String content,
                                                @Nullable NotificationListener listener) {
        return m_errorGroup.createNotification(
            title, content, NotificationType.ERROR, listener);
    }

    public void showErrorNotification(@NotNull final String title,
                                      @NotNull final String content,
                                      @Nullable Project project,
                                      @Nullable NotificationListener listener) {
        Notification n = createErrorNotification(title, content, listener);
        Notifications.Bus.notify(n, project);
    }

    /**
     * Download and install platform bundle.
     *
     * Notification will be shown after installation.
     */
    @Override
    public void download() {
        super.download(new Runnable() {
            @Override
            public void run() {
                afterDownload(true);
            }
        });
    }

    /**
     * Check for platform bundle updates with progress displaying.
     *
     * This method downloads remote bundle metadata and checks if it's newer
     * than local bundle. And shows install dialog if so.
     *
     * @param noUpdatesRunner Runnable to call if no update is found.
     */
    public void checkUpdate(@Nullable final Runnable noUpdatesRunner) {
        downloadMetadata(new Consumer<BundleMetadata>() {
            @Override
            public void consume(final BundleMetadata metadata) {
                if (metadata.isNewerThan(getLocalBundle().getMetadata()))
                    showNewVersionDialog(metadata);
                else if (noUpdatesRunner != null)
                    noUpdatesRunner.run();
            }
        }, false, false);
    }

    /**
     * Silently check for platform bundle updates.
     *
     * This method downloads remote bundle metadata and checks if it's newer
     * than local bundle. And shows install dialog if so.
     */
    public void checkUpdateSilent(@Nullable final Project project) {
        downloadMetadata(new Consumer<BundleMetadata>() {
            @Override
            public void consume(final BundleMetadata metadata) {
                if (metadata.isNewerThan(getLocalBundle().getMetadata()))
                    showNewVersionNotification(project, metadata);
            }
        }, true, true);
    }

    // TODO: improve me if possible.
    /**
     * Return true if platform is supported.
     */
    public boolean isPlatformSupported() {
        return m_platformChecker.isSupported();
    }

    public void setPlatformSupported(boolean state) {
        m_platformChecker.setSupported(state);
    }
}
