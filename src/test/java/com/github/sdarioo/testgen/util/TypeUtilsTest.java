/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class TypeUtilsTest 
{
    @Test
    public void typeVariableName() throws Exception 
    {
        Method m = getClass().getMethod("foo1", List.class);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("List<T>", TypeUtil.getName(m.getGenericParameterTypes()[0], builder));
    }
    
    @Test
    public void wildcardName1() throws Exception 
    {
        Method m = getClass().getMethod("foo2", List.class);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("List<? super Integer>", TypeUtil.getName(m.getGenericParameterTypes()[0], builder));
    }
    
    @Test
    public void wildcardName2() throws Exception 
    {
        Method m = getClass().getMethod("foo3", List.class);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("List<? extends Integer>", TypeUtil.getName(m.getGenericParameterTypes()[0], builder));
    }
    
    @Test
    public void paramArray() throws Exception 
    {
        Method m = getClass().getMethod("foo4", Comparable[].class);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("T[]", TypeUtil.getName(m.getGenericParameterTypes()[0], builder));
        
        assertEquals("T extends Comparable<T>", TypeUtil.getNameWithBounds(m.getTypeParameters()[0], builder));
        
    }
    
    private List rawList;
    private List<List<Map<String, List<String>>>> map;
    
    @Test
    public void paramType() throws Exception
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        Field field = getClass().getDeclaredField("rawList");
        Type type = field.getGenericType();
        assertEquals("List", TypeUtil.getName(type, builder));
        
        field = getClass().getDeclaredField("map");
        type = field.getGenericType();
        assertEquals("List<List<Map<String, List<String>>>>", TypeUtil.getName(type, builder));
    }
    
    public static <T extends Comparable<T>> void foo1(List<T> list)
    {
    }
    public static void foo2(List<? super Integer> list)
    {
    }
    public static void foo3(List<? extends Integer> list)
    {
    }
    public static <T extends Comparable<T>> void foo4(T[] array)
    {
    }
    public static <T> void foo5(List<? extends T> list)
    {
    }
    
}
