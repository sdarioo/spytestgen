package com.github.sdarioo.testgen.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultsTest 
{
    @Test
    public void defaultPrimitive()
    {
        assertEquals(Integer.valueOf(0), Defaults.getDefaultValue(Integer.TYPE));
        assertEquals(Boolean.FALSE.booleanValue(), Defaults.getDefaultValue(Boolean.TYPE));
    }
    
    @Test
    public void defaultEnum()
    {
        assertEquals(null, Defaults.getDefaultValue(E1.class));
        assertEquals(E2.value, Defaults.getDefaultValue(E2.class));
    }
    
    @Test
    public void defaultString()
    {
        assertEquals(null, Defaults.getDefaultValue(String.class));
    }
    
    @Test
    public void defaultObject()
    {
        assertEquals(null, Defaults.getDefaultValue(Object.class));
        assertEquals(null, Defaults.getDefaultValue(DefaultsTest.class));
    }
    
    public enum E1
    {
    }
    public enum E2
    {
        value
    }
    
}
