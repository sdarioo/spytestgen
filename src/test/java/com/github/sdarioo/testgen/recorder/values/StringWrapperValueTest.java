package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class StringWrapperValueTest 
{
    @Test
    public void isStringWrapper()
    {
        assertFalse(ValuesFactory.newValue(this) instanceof StringWrapperValue);
        assertFalse(ValuesFactory.newValue(new Wrapper1("")) instanceof StringWrapperValue);
        assertFalse(ValuesFactory.newValue(new Wrapper22()) instanceof StringWrapperValue);
        
        assertTrue(ValuesFactory.newValue(new Wrapper2()) instanceof StringWrapperValue);
        assertTrue(ValuesFactory.newValue(new Wrapper3()) instanceof StringWrapperValue);
    }
    
    @Test
    public void testEquals()
    {
        
        assertEquals(new StringWrapperValue(Wrapper2.fromString("a"), "fromString"),
                     new StringWrapperValue(Wrapper2.fromString("a"), "fromString"));
        
        assertNotEquals(new StringWrapperValue(Wrapper2.fromString("a"), "fromString"),
                new StringWrapperValue(Wrapper3.valueOf("a"), "valueOf"));
    }
    
    @Test
    public void toSourceCode()
    {
        StringWrapperValue p1 = new StringWrapperValue(Wrapper3.valueOf("x"), "valueOf");
        StringWrapperValue p2 = new StringWrapperValue(Wrapper2.fromString("x"), "fromString");
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        assertEquals("StringWrapperValueTest.Wrapper3.valueOf(\"x\")", p1.toSourceCode(Wrapper3.class, builder));
        assertEquals("StringWrapperValueTest.Wrapper2.fromString(\"x\")", p2.toSourceCode(Wrapper2.class, builder));
    }
    
    public static class Wrapper1
    {
        String s;
        public Wrapper1(String s) { this.s = s;}
        @Override
        public String toString() {
            return s;
        }
    }
    public static class Wrapper2
    {
        String s;
        public static Wrapper2 fromString(String s) { 
            Wrapper2 w = new Wrapper2();
            w.s = s;
            return w;
        }
        @Override
        public String toString() {
            return s;
        }
    }
    public static class Wrapper22
    {
        public static Wrapper2 fromString(String s) { return null;}
    }
    public static class Wrapper3
    {
        String s;
        public static Wrapper3 valueOf(String s) { 
            Wrapper3 w = new Wrapper3();
            w.s = s;
            return w;
        }
        @Override
        public String toString() {
            return s;
        }
    }
}
