/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.github.sdarioo.testgen.util.FileUtil;

public class FileUtilTest
{
    @SuppressWarnings("nls")
    @Test
    public void testNameWithExt()
    {
        Pair<String, String> pair = FileUtil.getNameWithExtension("test.java");
        assertEquals("test", pair.getLeft());
        assertEquals("java", pair.getRight());
        
        pair = FileUtil.getNameWithExtension("test.");
        assertEquals("test", pair.getLeft());
        assertEquals("", pair.getRight());
        
        pair = FileUtil.getNameWithExtension("test");
        assertEquals("test", pair.getLeft());
        assertNull(pair.getRight());
    }
}
