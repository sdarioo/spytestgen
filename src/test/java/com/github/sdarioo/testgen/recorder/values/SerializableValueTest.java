package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.values.SerializableValue;

public class SerializableValueTest 
{
    @Test
    public void testEquals() throws Exception
    {
        assertEquals(new SerializableValue(new Inner1("x")), new SerializableValue(new Inner1("x")));
        assertNotEquals(new SerializableValue(new Inner1("x")), new SerializableValue(new Inner1("y")));
        assertNotEquals(new SerializableValue(new Inner1("x")), new SerializableValue(new Inner2("x")));
    }
    
    @Test
    public void toSourceCode()
    {
        SerializableValue p = new SerializableValue(new Inner1("x"));
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("deserialize(\"res/SerializableValueTest.Inner1\")", p.toSouceCode(Inner1.class, builder));
    }
    
    
    public static class Inner1 implements Serializable 
    {
        private String x;
        
        public Inner1(String s) {
            this.x = s;
        }
    }
    public static class Inner2 implements Serializable 
    {
        private String x;
        
        public Inner2(String s) {
            this.x = s;
        }
    }
}
