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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.util.text.VersionComparatorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Utils {
    private static final Logger LOG = Logger.getInstance("com.ludditelabs.common.Utils");

    @NotNull
    public static String getPluginVersion(String id) {
        IdeaPluginDescriptor desc = PluginManager.getPlugin(PluginId.getId(id));
        return desc == null ? "" : desc.getVersion();
    }

    public static void sortVersions(@NotNull final List<String> versions) {
        Collections.sort(versions, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return VersionComparatorUtil.compare(o1, o2);
            }
        });
    }

    // TODO: there must be more optimal way to do that.
    /**
     * Find version string closest to the given one.
     *
     * This functions searches for version <= given one.
     *
     * @param versions List of <em>sorted</em> version strings.
     * @param version Reference version.
     * @return String or null.
     */
    @Nullable
    public static String findClosestVersion(
        @NotNull final List<String> versions,
        @NotNull final String version) {

        int i;
        int cmp;
        int sz = versions.size();

        for (i = 0; i < sz; i++) {
            cmp = VersionComparatorUtil.compare(version, versions.get(i));
            if (cmp == 0) {
                return versions.get(i);
            }
            else if (cmp < 0) {
                return i == 0 ? null : versions.get(i - 1);
            }
        }

        if (i > 0)
            return versions.get(sz - 1);
        return null;
    }

    /**
     * Get current platform name.
     */
    public static String getPlatform() {
        if (SystemInfo.isMac)
            return "darwin";
        else if (SystemInfo.isLinux)
            return "linux";
        else if (SystemInfo.isWindows)
            return "windows";
        return "unsupported";
    }

    public static ProcessOutput runProcess(String executable, String... parameters) {
        LOG.debug("Run process:", executable);

        try {
            GeneralCommandLine cmd = new GeneralCommandLine();
            cmd.setExePath(executable);
            cmd.addParameters(parameters);
            CapturingProcessHandler handler = new CapturingProcessHandler(
                cmd.withCharset(CharsetToolkit.getDefaultSystemCharset()));
            return handler.runProcess(60 * 1000);
        }
        catch (ExecutionException e) {
            LOG.info(e);
        }

        return null;
    }

    public static ProcessOutput runCheckedProcess(String executable, String... parameters) {
        ProcessOutput out = runProcess(executable, parameters);
        if (out != null) {
            if (out.isCancelled()) {
                LOG.info("Run process: CANCELED.");
                return null;
            }
            else if (!out.checkSuccess(LOG)) {
                return null;
            }
        }
        return out;
    }
}
