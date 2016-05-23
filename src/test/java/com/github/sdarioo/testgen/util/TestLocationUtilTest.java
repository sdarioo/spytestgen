/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class TestLocationUtilTest 
{
    @Test
    public void shouldDetectMavenLocation()
    {
        File loc = TestLocationUtil.getTestLocation(TestLocationUtil.class);
        String path = loc.getAbsolutePath().replace('\\', '/');
        
        assertTrue(path, path.endsWith("testgen/src/test/java/com/github/sdarioo/testgen/util")); //$NON-NLS-1$
    }
}
