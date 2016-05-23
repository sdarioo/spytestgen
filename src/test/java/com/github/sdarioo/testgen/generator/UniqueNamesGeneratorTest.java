/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.impl.UniqueNamesProvider;

public class UniqueNamesGeneratorTest
{
    @SuppressWarnings("nls")
    @Test
    public void testGetUniqueName()
    {
        UniqueNamesProvider gen = new UniqueNamesProvider();
        assertEquals("name", gen.generateUniqueName("name"));
        assertEquals("name2", gen.generateUniqueName("name"));
        assertEquals("name3", gen.generateUniqueName("name"));
        
        assertEquals("other", gen.generateUniqueName("other"));
        assertEquals("other2", gen.generateUniqueName("other"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testExistingUniqueName()
    {
        UniqueNamesProvider gen = new UniqueNamesProvider("name", "other");
        
        assertEquals("name2", gen.generateUniqueName("name"));
        assertEquals("name3", gen.generateUniqueName("name"));
        
        assertEquals("other2", gen.generateUniqueName("other"));
        
        assertEquals("new", gen.generateUniqueName("new"));
    }
}
