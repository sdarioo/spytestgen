package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class EnumValueTest 
{
    @Test
    public void testEnum()
    {
        EnumValue p = new EnumValue(E.A);
        assertEquals("EnumValueTest.E.A", p.toSourceCode(E.class, new TestSuiteBuilder()));
        
        assertEquals(p, new EnumValue(E.A));
        assertNotEquals(p, new EnumValue(E.C));
    }
    
    public enum E
    {
        A,B,C
    }
}
