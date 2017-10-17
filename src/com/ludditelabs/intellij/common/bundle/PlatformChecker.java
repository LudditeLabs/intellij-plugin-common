package com.ludditelabs.intellij.common.bundle;

import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.util.SystemInfo;
import com.ludditelabs.intellij.common.Utils;

// TODO: improve me if possible.
/**
 * Detect if platform is supported.
 *
 * Currently the following platforms are supported:
 *
 * <ul>
 *     <li>Linux 64bit</li>
 *     <li>MacOS 64bit, >= 10.9</li>
 *     <li>Windows 64bit, >= 7</li>
 * </ul>
 */
public class PlatformChecker {
    private boolean m_needCheck = true;
    private boolean m_isSupported = false;

    public PlatformChecker() {

    }

    private boolean doCheck() {
        if (SystemInfo.isLinux) {
            // Get linux kernel arch
            ProcessOutput p = Utils.runCheckedProcess("uname", "-m");
            if (p != null) {
                String out = p.getStdout();
                if (out.contains("x86_64") || out.contains("x64"))
                    return true;
            }
        }
        else if (SystemInfo.isMac) {
            // Try to run 'uname -m' like for linux;
            // if failed then check OS version.
            ProcessOutput p = Utils.runCheckedProcess("uname", "-m");
            if (p == null) {
                // 10.7 (Lion) was last supported 32bit arch, but we build
                // platform bundles with 10.9, so this is min required OS.
                if (SystemInfo.isOsVersionAtLeast("10.9"))
                    return true;
            }
            else {
                String out = p.getStdout();
                if (out.contains("x86_64") || out.contains("x64"))
                    return true;
            }
        }
        else if (SystemInfo.isWin7OrNewer) {
            String arch = System.getenv("PROCESSOR_ARCHITECTURE");
            String wow_arch = System.getenv("PROCESSOR_ARCHITEW6432");
            String pfx86 = System.getenv("ProgramFiles(x86)");
            return (arch != null && arch.endsWith("64"))
                || (wow_arch != null && wow_arch.endsWith("64"))
                || pfx86 != null;
        }

        return false;
    }

    public boolean isSupported() {
        if (m_needCheck)
            setSupported(doCheck());
        return m_isSupported;
    }

    public void setSupported(boolean state) {
        m_isSupported = state;
        m_needCheck = false;
    }
}
