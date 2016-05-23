/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class Formatter 
{
    private Formatter() {}
    
    public static String indentLines(String text)
    {
        String delim = "\n"; //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        
        StringTokenizer tokenizer = new StringTokenizer(text, delim, true);
        while (tokenizer.hasMoreTokens()) {
            String sLine = tokenizer.nextToken();
            
            if (delim.equals(sLine)) {
                sb.append(sLine);
            } else if (sLine.trim().length() == 0) {
                sb.append(sLine);
            } else {
                sb.append(getIndent() + sLine);
            }
        }
        return sb.toString();
    }
    
    public static List<String> indentLines(List<String> lines)
    {
        List<String> result = new ArrayList<>(lines.size());
        
        for (String line : lines) {
            if (line.trim().length() == 0) {
                result.add(line);
            } else {
                result.add(getIndent() + line);
            }
        }
        return result;
    }
    
    public static String getIndent()
    {
        return "    "; //$NON-NLS-1$
    }
}
