/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import com.github.sdarioo.testgen.recorder.Call.MethodRef;

public class CallTest 
{
    @Test
    public void testMethodRef()
    {
        Call call = Call.newCall(new MethodRef() {});
        assertNotNull(call);
    }
    
    @Test
    public void testEqualsMethod()
    {
        Call call1 = Call.newCall(new MethodRef() {});
        Call call2 = Call.newCall(new MethodRef() {});
        assertEquals(call1.getMethod(), call2.getMethod());
        
        Class<?> retType = call1.getMethod().getReturnType();
        assertEquals(Void.TYPE, retType);
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEquals() throws Exception
    {
        Call call1 = Call.newCall(getClass().getMethod("m1"));
        Call call2 = Call.newCall(getClass().getMethod("m1"));
        
        // Empty
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        // With args
        call1 = Call.newCall(getClass().getMethod("m3", Integer.TYPE), 1);
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2 = Call.newCall(getClass().getMethod("m3", Integer.TYPE), 1);
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        call1 = Call.newCall(getClass().getMethod("m4", Integer.TYPE, String.class), 1, "test");
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2 = Call.newCall(getClass().getMethod("m4", Integer.TYPE, String.class), 1, "test");
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        // With Result
        call1.endWithResult(1);
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithResult(2);
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithResult(1);
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        
        // With Exception
        call1.endWithException(new NullPointerException("null"));
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithException(new NullPointerException("other null"));
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithException(new IllegalArgumentException("null"));
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithException(new NullPointerException("null"));
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
    }
    
    @Test
    public void methodNotEquals() throws Exception
    {
        Method m1 = getClass().getMethod("m1");
        Method m2 = getClass().getMethod("m2");
        
        Call c1 = Call.newCall(m1, this, new Object[0]);
        Call c2 = Call.newCall(m2, new Object[0]);
        
        assertNotEquals(c1, c2);
    }
    
    @Test
    public void testIsStatic() throws Exception
    {
        Method m1 = getClass().getMethod("m1");
        Call c1 = Call.newCall(m1, this, new Object[0]);
        assertFalse(c1.isStatic());
        
        Method m2 = getClass().getMethod("m2");
        Call c2 = Call.newCall(m2, new Object[0]);
        assertTrue(c2.isStatic());
    }
    
    @Test
    public void testInheritance() throws Exception
    {
        Method m = Base.class.getMethod("base");
        Call c1 = Call.newCall(m, new A(), new Object[0]);
        Call c2 = Call.newCall(m, new B(), new Object[0]);
        
        assertNotEquals(c1, c2);
    }
    
    public void m1() {}
    public static void m2() {}
    public static void m3(int x) {}
    public static void m4(int x, String s) {}
    
    public static class Base
    {
        public void base() {}
    }
    public static class A extends Base
    {
    }
    public static class B extends Base
    {
    }
}
