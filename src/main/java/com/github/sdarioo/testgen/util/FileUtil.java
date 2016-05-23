/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import org.apache.commons.lang3.tuple.Pair;

public final class FileUtil 
{
    private FileUtil() {}
    
    public static String stripExtension(String name)
    {
        return getNameWithExtension(name).getLeft();
    }
    
    public static Pair<String, String> getNameWithExtension(String fileName)
    {
        String base = fileName;
        String ext = null;
        int idx = fileName.lastIndexOf('.');
        if (idx > 0) {
            base = fileName.substring(0, idx);
            ext = fileName.substring(idx + 1);
        }
        return Pair.of(base, ext);
    }
    
    
}
