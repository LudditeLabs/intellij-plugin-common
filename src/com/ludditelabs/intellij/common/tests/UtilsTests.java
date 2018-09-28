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

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.util.Arrays;
import java.util.List;

public class UtilsTests extends LightPlatformCodeInsightFixtureTestCase {
    // Test: how sortVersions() sorts version strings.
    public void testSortVersions() throws Throwable {
        List<String> versions = Arrays.asList("1.0.1", "0.0.2", "0.3.3", "0.2.0");
        Utils.sortVersions(versions);
        assertOrderedEquals(versions, "0.0.2", "0.2.0", "0.3.3", "1.0.1");
    }

    // Test: find suitable version.
    public void testVersions() throws Throwable {
        List<String> versions = Arrays.asList("0.0.2", "0.0.3", "0.2.0");
        Utils.sortVersions(versions);

        assertEquals("0.0.2", Utils.findClosestVersion(versions, "0.0.2"));
        assertEquals("0.0.3", Utils.findClosestVersion(versions, "0.1"));
        assertEquals("0.2.0", Utils.findClosestVersion(versions, "0.3.1"));
        assertNull(Utils.findClosestVersion(versions, "0.0.1"));
    }
}
