/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.io.IOException;
import java.io.InputStream;

import com.github.sdarioo.testgen.logging.Logger;

public final class IOUtil 
{
    private IOUtil() {}
    
    public static void close(InputStream is)
    {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                Logger.warn(e.toString());
            }
        }
    }
}
