/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class ArgNamesCacheTest 
{
    @Test
    public void introspectArgNames() throws Throwable
    {
        ArgNamesCache.clear();
        
        Method method = getClass().getDeclaredMethod("foo", String.class);
        String[] names = ArgNamesCache.getArgNames(method, true);
        assertNotNull(names);
        assertEquals("xyz", names[0]);
    }
    
    @Test
    public void testDefaultNames() throws Exception
    {
        ArgNamesCache.clear();
        
        Method method = getClass().getDeclaredMethod("foo1", int.class, int.class);
        String[] names = ArgNamesCache.getArgNames(method, false);
        assertNotNull(names);
        assertEquals(2, names.length);
        assertEquals("arg0", names[0]);
        assertEquals("arg1", names[1]);
    }
    
    @Test
    public void cannotIntrospectInterfaceArgNames() throws Throwable
    {
        ArgNamesCache.clear();
        
        Method method = IProvider.class.getDeclaredMethod("foo", String[].class);
        String[] names = ArgNamesCache.getArgNames(method, true);
        assertNotNull(names);
        assertEquals("arg0", names[0]);
    }
    
    
    private static interface IProvider
    {
        void foo(String... args);
    }
    
    private static void foo(String xyz) {}
    private static void foo1(int x, int y) {}
}
