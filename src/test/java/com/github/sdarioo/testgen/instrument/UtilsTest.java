package com.github.sdarioo.testgen.instrument;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class UtilsTest 
{
    @Test
    public void testGetMethod()
    {
        Method m = RecorderAPI.getMethod(UtilsTest.class, "m1", "()V");
        assertNotNull(m);
        assertEquals("m1", m.getName());
        
        m = RecorderAPI.getMethod(UtilsTest.class, "m2", "()V");
        assertNotNull(m);
        assertEquals("m2", m.getName());

        m = RecorderAPI.getMethod(UtilsTest.class, "m3", "()Ljava/lang/String;");
        assertNotNull(m);
        assertEquals("m3", m.getName());
        
        m = RecorderAPI.getMethod(TestRunnable.class, "run", "()V");
        assertNotNull(m);
        assertEquals("run", m.getName());
        
        m = RecorderAPI.getMethod(A.class, "a", "()V");
        assertNotNull(m);
        
        m = RecorderAPI.getMethod(B.class, "a", "()V");
        assertNull(m);
    }
    
    public static void m1() {}
    
    @SuppressWarnings("unused")
    private static void m2() {}
    
    @SuppressWarnings("unused")
    private String m3() { return null; }
    
    private static class TestRunnable implements Runnable  {
        @Override
        public void run() {
        }
    }
    
    private static class A  {
        @SuppressWarnings("unused")
        public void a() {};
    }
    
    private static class B extends A  {
        
    }
}
