/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest 
{
    @Test
    public void splitLines()
    {
        assertArrayEquals(new String[]{"x", "y", "z"}, StringUtil.splitLines("x\ny\r\nz\r").toArray(new String[0]));
    }
}
