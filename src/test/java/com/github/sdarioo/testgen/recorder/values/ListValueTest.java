/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ListValueTest 
{
    
    @Test
    public void testSupportedList()
    {
        ListValue p = new ListValue(new ArrayList());
        assertTrue(p.isSupported(List.class, new HashSet<String>()));
        
        p = new ListValue(new ArrayList());
        assertFalse(p.isSupported(LinkedList.class, new HashSet<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEmptyList() throws Exception
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        List<String> list = new ArrayList<String>();
        ListValue p = new ListValue(list);
        assertEquals("Arrays.asList()", p.toSourceCode(List.class, builder));
        
        Method m = getClass().getMethod("foo1", List.class);
        p = new ListValue(list);
        assertEquals("Arrays.<String>asList()", p.toSourceCode(m.getGenericParameterTypes()[0], builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenericList() throws Exception
    {
        Method m = ListValueTest.class.getMethod("foo2", List.class);
        
        List<List<String>> list = new ArrayList<List<String>>();
        list.add(Collections.<String>emptyList());
        
        ListValue p = new ListValue(list);
        
        foo2(Arrays.<List<String>>asList(new ArrayList<String>()));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<List<String>>asList(Arrays.<String>asList())", 
                p.toSourceCode(m.getGenericParameterTypes()[0], builder));
        
        foo2(Arrays.<List<String>>asList(new ArrayList<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testWildcardList() throws Exception
    {
        Method m = ListValueTest.class.getMethod("foo3", List.class);
        
        List<String> list = new ArrayList<String>();
        
        ListValue p = new ListValue(list);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.asList()", p.toSourceCode(m.getGenericParameterTypes()[0], builder));
        
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testListOfArrays() throws Exception
    {
        Method m = ListValueTest.class.getMethod("foo4", List.class);
        
        ListValue p = new ListValue(Collections.singletonList(new String[]{"x"}));
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<String[]>asList(new String[]{\"x\"})", 
                p.toSourceCode(m.getGenericParameterTypes()[0], builder));
    }

 // DONT REMOVE - USED IN TEST
    public void foo1(List<String> list)        { }
    public void foo2(List<List<String>> list)  { }
    public static <T> void foo3(List<T> list)  { }
    public void foo4(List<String[]> list)      { }
}
