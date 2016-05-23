/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil 
{
    private StringUtil() {}
    
    public static String join(String[] array, String sep)
    {
        return join(Arrays.asList(array), sep);
    }
    
    public static String join(List<String> array, String sep)
    {
        StringBuilder sb = new StringBuilder();
        for (String arg : array) {
            if (sb.length() > 0) {
                sb.append(sep);
            }
            sb.append(arg);
        }
        return sb.toString();
    }
    
    public static List<String> splitLines(String message)
    {
        String[] lines = StringUtils.split(message, "\r\n"); //$NON-NLS-1$
        return Arrays.asList(lines);
    }
}
