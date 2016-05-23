/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import com.github.sdarioo.testgen.logging.Logger;

public class TestLocationUtil 
{
    
    @SuppressWarnings("nls")
    public static File getTestLocation(Class<?> testedClass)
    {
        ProtectionDomain protectionDomain = testedClass.getProtectionDomain();
        if (protectionDomain == null) {
            Logger.error("Null ProtectionDomain for class: " + testedClass.getName());
            return null;
        }
        CodeSource codeSource = protectionDomain.getCodeSource();
        if (codeSource != null) {
            URL url = codeSource.getLocation();
            File loc = toFile(url);
            String path = loc.getAbsolutePath().replace('\\', '/');
            if (loc.isDirectory()) {
                
                String pkgPath = "";
                Package pkg = testedClass.getPackage();
                if (pkg != null) {
                    pkgPath = pkg.getName().replace('.', '/');
                }
                
                // Maven
                File mavenRoot = getMavenProjectRoot(loc);
                if (mavenRoot != null) {
                    File testSrcDir = new File(mavenRoot, "src/test/java");
                    if (testSrcDir.isDirectory()) {
                        return new File(testSrcDir, pkgPath);
                    }
                }
                // Eclipse
                File eclipseProject = null;
                if (isEclipseProject(loc)) {
                    eclipseProject = loc;
                } else if (path.endsWith("/bin") && isEclipseProject(loc.getParentFile())) {
                    eclipseProject = loc.getParentFile();
                }
                if (eclipseProject != null) {
                    File testsDir = getEclipseTestProject(eclipseProject);
                    File testSrcDir = new File(testsDir, "src");
                    if (testSrcDir.isDirectory()) {
                        return new File(testSrcDir, pkgPath);
                    }
                }
                
                Logger.warn("Unrecognized project structure for path: " + path);
                
            } else {
                Logger.warn("CodeSource loction not exists: " + url.toString());
            }
        } else {
            Logger.warn("Null ProtectionDomein.CodeSource for: " + testedClass.getName());
        }
        
        return null;
    }
    
    private static File getMavenProjectRoot(File dir)
    {
        File pom = new File(dir, "pom.xml"); //$NON-NLS-1$
        if (pom.isFile()) {
            return dir;
        }
        File parentDir = dir.getParentFile();
        if (parentDir != null) {
            return getMavenProjectRoot(parentDir);
        }
        return null;
    }
    
    private static boolean isEclipseProject(File dir)
    {
        return new File(dir, ".project").isFile(); //$NON-NLS-1$
    }
    
    private static File getEclipseTestProject(File projectDir)
    {
        String name = projectDir.getName();
        File testsDir = new File(projectDir.getParentFile(), name + ".tests"); //$NON-NLS-1$
        if (testsDir.isDirectory()) {
            return testsDir;
        }
        return projectDir;
    }
    
    public static File toFile(URL url)
    {
        try {
            String path = url.toURI().getPath();
            return new File(path);
        } catch (URISyntaxException e) {
            return new File(url.getPath());
        }
    }
}
