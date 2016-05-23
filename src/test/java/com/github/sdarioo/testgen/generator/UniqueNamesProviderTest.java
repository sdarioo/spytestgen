/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class UniqueNamesProviderTest 
{
    @SuppressWarnings("nls")
    @Test
    public void testUniqueMethods()
    {
        IUniqueNamesProvider provider = new TestSuiteBuilder();
        assertEquals("method", provider.newUniqueMethodName("method"));
        assertEquals("method2", provider.newUniqueMethodName("method"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testUniqueFiles()
    {
        IUniqueNamesProvider provider = new TestSuiteBuilder();
        
        assertEquals("test.txt", provider.newUniqueFileName("test.txt"));
        assertEquals("test2.txt", provider.newUniqueFileName("test.txt"));
        assertEquals("test3.txt", provider.newUniqueFileName("test.txt"));
        
        assertEquals("readme", provider.newUniqueFileName("readme"));
        assertEquals("readme2", provider.newUniqueFileName("readme"));
    }
}
