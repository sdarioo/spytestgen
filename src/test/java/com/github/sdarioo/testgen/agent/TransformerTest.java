/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.Properties;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.github.sdarioo.testgen.agent.Transformer;

@SuppressWarnings("nls")
public class TransformerTest
{
    @Test
    public void shouldSkipPrivateInnerClass() throws IOException
    {
        byte[] b1 = getBytes(PrivateClass.class);
        byte[] b2 = Transformer.transform(b1, "PrivateClass", "publicMethod");
        assertEquals(b1.length, b2.length);
    }
    
    @Test
    public void shouldSkipInterface() throws IOException
    {
        byte[] b1 = getBytes(Interface.class);
        byte[] b2 = Transformer.transform(b1, "Interface", "publicMethod");
        assertEquals(b1.length, b2.length);
    }

    @Test
    public void shouldSkipPrivateMethod() throws IOException
    {
        byte[] b1 = getBytes(PublicClass.class);
        byte[] b2 = Transformer.transform(b1, "PublicClass", "privateMethod");
        assertEquals(b1.length, b2.length);
    }
    
    @Test
    public void testTransform1() throws IOException
    {
        byte[] b1 = getBytes(Target.class);
        byte[] b2 = Transformer.transform(b1, "Target", "m1");
        assertNotEquals(b1.length, b2.length);
        
        verify(b2);
    }
    
   @Test
    public void testTransform2() throws IOException
    {
        byte[] b1 = getBytes(Target.class);
        byte[] b2 = Transformer.transform(b1, "Target", "m2");
        assertNotEquals(b1.length, b2.length);
        
        verify(b2);
    }
    
    @Test
    public void testConcat() throws IOException
    {
        byte[] b1 = getBytes(Target.class);
        byte[] b2 = Transformer.transform(b1, "Target", "concat");
        
        assertNotEquals(b1.length, b2.length);
        verify(b2);
    }
    
    @Test
    public void testLong() throws IOException
    {
        byte[] b1 = getBytes(Target.class);
        byte[] b2 = Transformer.transform(b1, "Target", "l");
        
        assertNotEquals(b1.length, b2.length);
        verify(b2);
    }
    

    private static byte[] getBytes(Class<?> clazz)
        throws IOException
    {
        String classAsPath = clazz.getName().replace('.', '/') + ".class"; //$NON-NLS-1$
        InputStream is = clazz.getClassLoader().getResourceAsStream(classAsPath);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int c = is.read();
        while (c != -1) {
            bytes.write(c);
            c = is.read();
        }
        is.close();
        return bytes.toByteArray();
    }
    
    private static void verify(byte[] bytes)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        CheckClassAdapter.verify(new ClassReader(bytes), false, pw);
        assertTrue(sw.toString(), sw.toString().length() == 0);
    }
    
    private static void trace(byte[] bytes, String[] verifyInst)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        TraceClassVisitor tracer = new TraceClassVisitor(pw);
        ClassReader reader = new ClassReader(bytes);
        reader.accept(tracer, 0);
        
        int count = 0;
        pw.flush();
        String[] asLines = sw.getBuffer().toString().split("\n");
        for (String sLine : asLines) {
            for (String inst : verifyInst) {
                if (sLine.trim().equals(inst)) {
                    count++;
                    break;
                }
            }
        }
        assertEquals(count, verifyInst.length);
    }
    
    private static class PrivateClass {
        public static void publicMethod(int a) {}
    }
    
    public static class PublicClass {
        private static void privateMethod(int a) {}
    }
    
    public static interface Interface {
        void publicMethod(int a);
    }
    
    public static class Target {
        public int m1(int a) { return 0;}
        public int m2(Object a) { return 0;}
        public long l(long v) { return v; }
        
        public static String concat(Properties props)
        {
            StringBuilder sb = new StringBuilder();
            for (Object key : props.keySet()) {
                if (sb.length() > 0) {
                    sb.append(';');
                }
                String sKey = (String)key;
                String sValue = props.getProperty(sKey);
                sb.append(sKey).append('=').append(sValue);
            }
            String ret = sb.toString();
            return ret;
        }
    }
}

