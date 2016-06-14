/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Properties;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ValuesFactoryTest
{
    @SuppressWarnings("nls")
    @Test
    public void testPrimitiveTypes()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        IValue p = ValuesFactory.newValue(true);
        assertEquals("true", p.toSourceCode(Boolean.TYPE, builder));
        
        p = ValuesFactory.newValue(Character.valueOf('x').charValue());
        assertEquals("'x'", p.toSourceCode(Character.TYPE, builder));
        
        p = ValuesFactory.newValue(Character.valueOf((char)1).charValue());
        assertEquals("(char)1", p.toSourceCode(Character.TYPE, builder));
        
        byte b = 127;
        p = ValuesFactory.newValue(Byte.valueOf(b).byteValue());
        assertEquals("(byte)127", p.toSourceCode(Byte.TYPE, builder));
        b = -127;
        p = ValuesFactory.newValue(Byte.valueOf(b).byteValue());
        assertEquals("(byte)-127", p.toSourceCode(Byte.TYPE, builder));
        
        short s = 1000;
        p = ValuesFactory.newValue(Short.valueOf(s).shortValue());
        assertEquals("(short)1000", p.toSourceCode(Short.TYPE, builder));
        s = -1000;
        p = ValuesFactory.newValue(Short.valueOf(s).shortValue());
        assertEquals("(short)-1000", p.toSourceCode(Short.TYPE, builder));
        
        int i = 100000;
        p = ValuesFactory.newValue(Integer.valueOf(i).intValue());
        assertEquals("100000", p.toSourceCode(Integer.TYPE, builder));
        i = -100000;
        p = ValuesFactory.newValue(Integer.valueOf(i).intValue());
        assertEquals("-100000", p.toSourceCode(Integer.TYPE, builder));
        
        long l = Long.MAX_VALUE;
        p = ValuesFactory.newValue(Long.valueOf(l).longValue());
        assertEquals("9223372036854775807L", p.toSourceCode(Long.TYPE, builder));
        l = Long.MIN_VALUE;
        p = ValuesFactory.newValue(Long.valueOf(l).longValue());
        assertEquals("-9223372036854775808L", p.toSourceCode(Long.TYPE, builder));
        
        p = ValuesFactory.newValue(Float.valueOf(0.0f).floatValue());
        assertEquals("0.0f", p.toSourceCode(Float.TYPE, builder));
        
        p = ValuesFactory.newValue(Double.valueOf(0.1d).doubleValue());
        assertEquals("0.1d", p.toSourceCode(Float.TYPE, builder));
    }
    
    
    @SuppressWarnings("nls")
    @Test
    public void testPrimitiveWrappers()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        IValue p = ValuesFactory.newValue(Boolean.TRUE);
        assertEquals("true", p.toSourceCode(Boolean.class, builder));
        
        p = ValuesFactory.newValue(Character.valueOf('x'));
        assertEquals("'x'", p.toSourceCode(Character.class, builder));
        
        byte b = 127;
        p = ValuesFactory.newValue(Byte.valueOf(b));
        assertEquals("(byte)127", p.toSourceCode(Byte.class, builder));
        
        short s = 1000;
        p = ValuesFactory.newValue(Short.valueOf(s));
        assertEquals("(short)1000", p.toSourceCode(Short.class, builder));
        
        int i = 100000;
        p = ValuesFactory.newValue(Integer.valueOf(i));
        assertEquals("100000", p.toSourceCode(Integer.class, builder));
        
        long l = Long.MAX_VALUE;
        p = ValuesFactory.newValue(Long.valueOf(l));
        assertEquals("9223372036854775807L", p.toSourceCode(Long.class, builder));
        
        p = ValuesFactory.newValue(Float.valueOf(0.0f));
        assertEquals("0.0f", p.toSourceCode(Float.class, builder));
        
        p = ValuesFactory.newValue(Double.valueOf(0.1d));
        assertEquals("0.1d", p.toSourceCode(Double.class, builder));
    }
    
    @Test
    public void propsShouldBeBeforeMap()
    {
        IValue p = ValuesFactory.newValue(new Properties());
        assertEquals(PropertiesValue.class, p.getClass());
        
        p = ValuesFactory.newValue(new HashMap<String, String>());
        assertEquals(MapValue.class, p.getClass());
    }
    
    
}
