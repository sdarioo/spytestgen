/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class TestSuiteBuilderTest 
{
    @SuppressWarnings("nls")
    @Test
    public void shouldAddJavaUtil()
    {
        TestSuiteBuilder b = new TestSuiteBuilder();
        assertEquals("TreeMap", b.getTypeName("java.util.TreeMap"));
        assertEquals(1, b.getImports().size());
        assertTrue(b.getImports().contains("java.util.TreeMap"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGetSimpleName()
    {
        TestSuiteBuilder b = new TestSuiteBuilder();
        assertEquals("TestSuiteBuilderTest", b.getTypeName(TestSuiteBuilderTest.class));
        assertEquals("String[]", b.getTypeName(new String[0].getClass()));
        assertEquals("int[]", b.getTypeName(new int[0].getClass()));
        assertEquals("TestSuiteBuilderTest.Inner", b.getTypeName(Inner.class));
        
        assertEquals(1, b.getImports().size());
        assertTrue(b.getImports().contains("com.github.sdarioo.testgen.generator.TestSuiteBuilderTest"));
        
        
        b = new TestSuiteBuilder();
        assertEquals("TestSuiteBuilderTest", b.getTypeName(TestSuiteBuilderTest.class.getName()));
        assertEquals("String[]", b.getTypeName(new String[0].getClass().getName()));
        assertEquals("int[]", b.getTypeName(new int[0].getClass().getName()));
        assertEquals("TestSuiteBuilderTest.Inner", b.getTypeName(Inner.class.getName()));
        
        assertEquals(1, b.getImports().size());
        assertTrue(b.getImports().contains("com.github.sdarioo.testgen.generator.TestSuiteBuilderTest"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testInternalBeanName()
    {
        String name = "com.parasoft.dtp.shared.UuidCalculator$ViolationInfo";
        TestSuiteBuilder b = new TestSuiteBuilder();
        assertEquals("UuidCalculator.ViolationInfo", b.getTypeName(name));
        
        assertEquals(1, b.getImports().size());
        assertTrue(b.getImports().contains("com.parasoft.dtp.shared.UuidCalculator"));
        
    }
    
    @Test
    public void typeName() throws Exception
    {
    	TestSuiteBuilder b = new TestSuiteBuilder(true, null);
    	Method m = getClass().getMethod("foo", Object.class);
    	assertEquals("java.lang.Object", b.getTypeName(m.getParameterTypes()[0]));
    }
    
    @Test
    public void genericTypeName() throws Exception
    {
    	TestSuiteBuilder b = new TestSuiteBuilder(true, null);
    	Method m = getClass().getMethod("foo2", List.class);
    }
    
    
    public static <T> void foo(T arg) {}
    public static <T> void foo2(List<T> arg) {}
    
    public static class Inner {}
}
