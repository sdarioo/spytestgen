/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.text.StrTokenizer;
import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.RecordedClass;
import com.github.sdarioo.testgen.recorder.Recorder;

public class JUnitParamsGeneratorTest
{
    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestMethod()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        TestMethod source = gen.generateTestCase(this.getClass(), method, new TestSuiteBuilder());
        assertNotNull(source.toSourceCode());
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestMethodWithArray()
    {
        Method method = getMethod("getArray");
        assertNotNull(method);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        TestMethod source = gen.generateTestCase(this.getClass(), method, new TestSuiteBuilder());
        assertTrue(source.toSourceCode().contains("assertArrayEquals"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenerateParmasProviderMethod()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        List<Call> calls = new ArrayList<Call>();
        calls.add(Call.newCall(method, "name1", 1));
        calls.add(Call.newCall(method, null, 2));
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();

        String text = gen.generateParamsProvider(method, calls, "testSayHello", new TestSuiteBuilder()).toSourceCode();
        assertNotNull(text);
        
        String[] lines = new StrTokenizer(text, '\n').getTokenArray();
        assertEquals(7, lines.length);
        assertEquals("private static Object[] testSayHello_Parameters() throws Exception {", lines[1].trim());
        assertEquals("return new Object[] {", lines[2].trim());
        assertEquals("new Object[]{ \"name1\", 1, null },", lines[3].trim());
        assertEquals("new Object[]{ null, 2, null }", lines[4].trim());
        assertEquals("};", lines[5].trim());
        assertEquals("}", lines[6].trim());
    }

    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestSuite()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        List<Call> calls = new ArrayList<Call>();
        Call c1 = Call.newCall(method, "name1", 1);
        Call c2 = Call.newCall(method, null, 1);
        c1.endWithResult("ret1");
        c2.endWithResult(null);
        calls.add(c1);
        calls.add(c2);
        
        Recorder r = Recorder.newRecorder("test");
        r.record(c1);
        r.record(c2);
        RecordedClass recordedClass = r.getRecordedClass(method, null, false);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(recordedClass).toSourceCode();
        List<String> lines = toLines(src);
        
        assertEquals(30, lines.size());
        
        assertEquals("public void testSayHello(String arg0, int arg1, String expected) throws Exception {", lines.get(16));
        assertEquals("String result = JUnitParamsGeneratorTest.sayHello(arg0, arg1);", lines.get(17));
        assertEquals("Assert.assertEquals(expected, result);", lines.get(18));
                
        assertEquals("private static Object[] testSayHello_Parameters() throws Exception {", lines.get(22));
        assertEquals("new Object[]{ \"name1\", 1, \"ret1\" },", lines.get(24));
        assertEquals("new Object[]{ null, 1, null }", lines.get(25));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestSuiteWithProperties()
    {
        Method method = getMethod("methodWithProperties");
        assertNotNull(method);
        
        Properties p1 = new Properties();
        Properties p2 = new Properties();
        p1.setProperty("key1", "value1");
        p2.setProperty("key2", "value2");
        
        List<Call> calls = new ArrayList<Call>();
        calls.add(Call.newCall(method, this, new Object[]{p1}));
        calls.add(Call.newCall(method, this, new Object[]{p2}));
        
        calls.get(0).endWithResult(null);
        calls.get(1).endWithResult(null);
        
        Recorder r = Recorder.newRecorder("test");
        r.record(calls.get(0));
        r.record(calls.get(1));
        RecordedClass recordedClass = r.getRecordedClass(method, this, false);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(recordedClass).toSourceCode();
        List<String> set = toLines(src);
        
        assertTrue(set.contains("@Parameters(method = \"testMethodWithProperties_Parameters\")"));
        assertTrue(set.contains("private static Object[] testMethodWithProperties_Parameters() throws Exception {"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testStaticCallException()
    {
        Method method = getMethod("staticMethodWithProperties");
        Call call = Call.newCall(method, (Object)null);
        call.endWithException(new IllegalArgumentException());
        
        Recorder r = Recorder.newRecorder("test");
        r.record(call);
        RecordedClass recordedClass = r.getRecordedClass(call, false);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(recordedClass).toSourceCode();
        List<String> set = toLines(src);
        
        assertTrue(set.contains("@Test(expected=IllegalArgumentException.class)"));
        assertTrue(set.contains("JUnitParamsGeneratorTest.staticMethodWithProperties(null);"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testCallException()
    {
        Method method = getMethod("methodWithProperties");
        Call call = Call.newCall(method, this, new Object[]{(Object)null});
        call.endWithException(new IllegalArgumentException());
        
        Recorder r = Recorder.newRecorder("test");
        r.record(call);
        RecordedClass recordedClass = r.getRecordedClass(call, false);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(recordedClass).toSourceCode();
        List<String> set = toLines(src);
        
        assertTrue(set.contains("@Test(expected=IllegalArgumentException.class)"));
        assertTrue(set.contains("JUnitParamsGeneratorTest obj = new JUnitParamsGeneratorTest();"));
        assertTrue(set.contains("obj.methodWithProperties(null);"));
    }
    
    @Test
    public void testNonDefaultConstructor() throws Exception
    {
        Method method = Inner.class.getMethod("foo", Integer.TYPE);
        Call call = Call.newCall(method, new Inner("", true, 100L, E.value), new Object[]{Integer.valueOf(1)});
        call.endWithResult(Integer.valueOf(1));
        
        Recorder r = Recorder.newRecorder("test");
        r.record(call);
        RecordedClass recordedClass = r.getRecordedClass(call, false);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(recordedClass).toSourceCode();
        List<String> set = toLines(src);
        
        assertTrue(set.contains("// WARNING - constructing JUnitParamsGeneratorTest.Inner with default parameters;"));
        assertTrue(set.contains("JUnitParamsGeneratorTest.Inner obj = new JUnitParamsGeneratorTest.Inner(null, false, 0L, JUnitParamsGeneratorTest.E.value);"));
        assertTrue(set.contains("int result = obj.foo(arg0);"));
    }
    
    public static String sayHello(String name, int index)
    {
        return null;
    }
    
    public static String[] getArray() { return null; }
    
    public int methodWithProperties(Properties p)
    {
        return 0;
    }
    
    public static int staticMethodWithProperties(Properties p)
    {
        return 0;
    }
    
    private Method getMethod(String name)
    {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    private static List<String> toLines(String src)
    {
        String[] lines = src.split("\\n");
        List<String> set = new ArrayList<String>();
        for (String line : lines) {
            set.add(line.trim());
        }
        return set;
    }
    
    public static class Inner
    {
        public Inner(String s, boolean b, long l, E e) {}
        public int foo(int a) { return 0; }
    }
    
    public static enum E { value; };
}
