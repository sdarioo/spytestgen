package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ArrayValueTest 
{
    @Test
    public void testEmptyArray()
    {
        ArrayValue p = new ArrayValue(new Properties[0]);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new Properties[]{}", p.toSourceCode(Properties[].class, builder));
    }
    
    @Test
    public void testIntArray()
    {
        ArrayValue p = new ArrayValue(new int[] {1, 2, 3});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new int[]{1, 2, 3}", p.toSourceCode(int[].class, builder));
    }
    
    @Test
    public void testStringArray()
    {
        ArrayValue p = new ArrayValue(new String[] {"ala", "ma", "kota"});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[]{\"ala\", \"ma\", \"kota\"}", p.toSourceCode(String[].class, builder));
    }
    
    @Test
    public void testTwoDimArray()
    {
        ArrayValue p = new ArrayValue(new String[][] { new String[]{"ala"}, new String[]{"ma"}});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[][]{new String[]{\"ala\"}, new String[]{\"ma\"}}", 
                p.toSourceCode(String[][].class, builder));
    }
    
    @Test
    public void testGenericArray() throws Exception
    {
        Method m = ArrayValueTest.class.getMethod("foo", List[].class);
        assertNotNull(m);
        
        ArrayValue p = new ArrayValue(new ArrayList[0]);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new List[]{}", p.toSourceCode(m.getGenericParameterTypes()[0], builder));
    }
    
    @Test
    public void testTypeVarArray() throws Exception
    {
        System.err.println(String[].class.getName());
        
        Method m = ArrayValueTest.class.getMethod("foo", Object[].class);
        assertNotNull(m);
        
        ArrayValue p = new ArrayValue(new String[] {"goo"});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[]{\"goo\"}", p.toSourceCode(m.getGenericParameterTypes()[0], builder));
    }
    
    public static <T> int foo(T[] var)
    {
        return 0;
    }
    public static int foo(List<String>[] var)
    {
        return 0;
    }
    
}
