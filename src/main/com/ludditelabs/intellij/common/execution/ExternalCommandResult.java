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

package com.ludditelabs.intellij.common.execution;

import com.intellij.execution.process.ProcessOutput;
import org.jetbrains.annotations.NotNull;


/**
 * This class provides external command result.
 */
public class ExternalCommandResult {
    @NotNull private final ProcessOutput m_output;

    /**
     * Construct result.
     *
     * @param output command line process output.
     */
    public ExternalCommandResult(@NotNull ProcessOutput output) {
        m_output = output;
    }

    /** External command process output. */
    @NotNull
    public ProcessOutput processOutput() {
        return m_output;
    }

    /** External command exit code. */
    public int exitCode() {
        return m_output.getExitCode();
    }

    /** Content of the standard output. */
    @NotNull
    public String stdout() {
        return m_output.getStdout();
    }

    /** Content of the error output. */
    @NotNull
    public String stderr() {
        return m_output.getStderr();
    }

    /** Combined content of the standard and error output. */
    @NotNull
    public String allContent() {
        return stdout() + "\n" + stderr();
    }

    /** Return true if external command is canceled. */
    public boolean isCanceled() {
        return m_output.isCancelled();
    }

    /** Return true if at leas one stream has content. */
    public boolean hasContent() {
        return !m_output.getStdout().isEmpty()
            || !m_output.getStderr().isEmpty();
    }

    /** Return true is external command finished successfully.*/
    public boolean isSuccess() {
        return exitCode() == 0;
    }
}
