/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.bigtop.manager.stack.core.spi.repo;

import org.apache.bigtop.manager.common.shell.ShellExecutor;
import org.apache.bigtop.manager.common.shell.ShellResult;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mockStatic;

public class YumPackageManagerTest {

    private MockedStatic<ShellExecutor> mockStatic;

    private final YumPackageManager yumPackageManager = new YumPackageManager();

    @BeforeEach
    public void setUp() {
        mockStatic = mockStatic(ShellExecutor.class);
        mockStatic
                .when(() -> ShellExecutor.execCommand(anyList(), anyBoolean()))
                .thenAnswer(invocation -> {
                    List<String> cmd = invocation.getArgument(0);
                    return ShellResult.success(cmd.toString());
                });
    }

    @AfterEach
    public void tearDown() {
        mockStatic.close();
    }

    @Test
    public void testInstallPackage() {
        String packageToInstall = "testPackage";
        ShellResult result = yumPackageManager.installPackage(List.of(packageToInstall));
        mockStatic.verify(() -> ShellExecutor.execCommand(anyList(), anyBoolean()));
        assertEquals("[/usr/bin/yum, install, -y, testPackage]", result.getOutput());
    }

    @Test
    public void testUninstallPackage() {
        String packageToRemove = "testPackage";
        ShellResult result = yumPackageManager.uninstallPackage(List.of(packageToRemove));
        mockStatic.verify(() -> ShellExecutor.execCommand(anyList(), anyBoolean()));
        assertEquals("[/usr/bin/yum, remove, -y, testPackage]", result.getOutput());
    }

    @Test
    public void testListPackages() {
        mockStatic.when(() -> ShellExecutor.execCommand(anyList())).thenAnswer(invocation -> {
            List<String> mockOutput = List.of(
                    "Installed Packages",
                    "package1.x86_64 1.2.3-1",
                    "package2.noarch 4.5.6-1",
                    "package3.i686 7.8.9-1");
            return ShellResult.success(String.join("\n", mockOutput));
        });

        List<String> packagesResult = yumPackageManager.listPackages();
        mockStatic.verify(() -> ShellExecutor.execCommand(anyList()));
        assertEquals(3, packagesResult.size());
        assertEquals("package1", packagesResult.get(0));
        assertEquals("package2", packagesResult.get(1));
        assertEquals("package3", packagesResult.get(2));
    }

    @Test
    public void testGetName() {
        assertEquals("YUM", yumPackageManager.getName());
    }
}
