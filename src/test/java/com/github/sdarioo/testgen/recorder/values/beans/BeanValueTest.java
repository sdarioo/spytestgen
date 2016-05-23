package com.github.sdarioo.testgen.recorder.values.beans;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.values.beans.BeanFactory;
import com.github.sdarioo.testgen.recorder.values.beans.BeanValue;

public class BeanValueTest 
{
    @Test
    public void testEquals()
    {
        BeanValue p1 = new BeanValue(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanValue p2 = new BeanValue(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanValue p3 = new BeanValue(new Bean2(new Bean1()), BeanFactory.getInstance().getBean(Bean2.class));
        BeanValue p4 = new BeanValue(new Bean2(null), BeanFactory.getInstance().getBean(Bean2.class));
        
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p4, p3);
    }
    
    @Test
    public void toSourceCode()
    {
        BeanValue p1 = new BeanValue(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanValue p2 = new BeanValue(new Bean2(new Bean1()), BeanFactory.getInstance().getBean(Bean2.class));
        BeanValue p3 = new BeanValue(new Bean3(), BeanFactory.getInstance().getBean(Bean3.class));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("newBean1(0, 0)", p1.toSouceCode(Bean1.class, builder));
        assertEquals("newBean2(newBean1(0, 0))", p2.toSouceCode(Bean2.class, builder));
        assertEquals("newBean3(0)", p3.toSouceCode(Bean3.class, builder));
    }
    
    @Test
    public void testSimpleBeans()
    {
        BeanValue p1 = new BeanValue(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanValue p2 = new BeanValue(new Bean2(null), BeanFactory.getInstance().getBean(Bean2.class));
        BeanValue p3 = new BeanValue(new Bean3(), BeanFactory.getInstance().getBean(Bean3.class));
        
        testBeanParam(p1, Bean1.class, "newBean1(0, 0)", "private static BeanValueTest.Bean1 newBean1(int x, int y) {");
        testBeanParam(p2, Bean2.class, "newBean2(null)", "private static BeanValueTest.Bean2 newBean2(BeanValueTest.Bean1 x) {");
        testBeanParam(p3, Bean3.class, "newBean3(0)", "private static BeanValueTest.Bean3 newBean3(int x) {");
    }
    
    
    @Test
    public void testRawBean() throws Exception
    {
        BeanValue p = new BeanValue(new Pair<Integer>(1,2), BeanFactory.getInstance().getBean(Pair.class));
        testBeanParam(p, Pair.class, "newPair(1, 2)", "private static <T> BeanValueTest.Pair<T> newPair(T x, T y) {");
    }
    
    @Test
    public void testGenericBean() throws Exception
    {
        Method m = getClass().getMethod("foo1", Pair.class);
        
        BeanValue p = new BeanValue(new Pair<Integer>(1,2), 
                BeanFactory.getInstance().getBean(Pair.class));
        
        testBeanParam(p, m.getGenericParameterTypes()[0], "newPair(1, 2)", "private static <T> BeanValueTest.Pair<T> newPair(T x, T y) {");
    }
    
    @Test
    public void testGenericBeanInGenericMethod() throws Exception
    {
        Method m = getClass().getMethod("foo3", List.class);
        ParameterizedType type = (ParameterizedType)m.getGenericParameterTypes()[0];
        Type beanType = type.getActualTypeArguments()[0];
        
        BeanValue p = new BeanValue(new Pair<Integer>(1,2), 
                BeanFactory.getInstance().getBean(Pair.class));
        
        testBeanParam(p, beanType, "newPair(1, 2)", "private static <T> BeanValueTest.Pair<T> newPair(T x, T y) {");
    }
    
    @Test
    public void testHelperMethods() throws Exception
    {
        Method m1 = getClass().getMethod("foo1", Pair.class);
        Method m2 = getClass().getMethod("foo2", Pair.class);
        
        BeanValue p1 = new BeanValue(new Pair<Integer>(1,2), 
                BeanFactory.getInstance().getBean(Pair.class));
        
        BeanValue p2 = new BeanValue(new Pair<String>("x", "y"), 
                BeanFactory.getInstance().getBean(Pair.class));
        
        assertFalse(p1.equals(p2));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("newPair(1, 2)", p1.toSouceCode(m1.getGenericParameterTypes()[0], builder));
        assertEquals("newPair(\"x\", \"y\")", p2.toSouceCode(m2.getGenericParameterTypes()[0], builder));
        
        assertEquals(1, builder.getHelperMethods().size());
    }
    
    @Test
    public void testNotAccessibleBean()
    {
        BeanValue p = new BeanValue(new PrivateBean(), 
                BeanFactory.getInstance().getBean(PrivateBean.class));
        
        assertFalse(p.isSupported(PrivateBean.class, new HashSet<String>()));
    }
    
    
    private void testBeanParam(BeanValue p, Type targetType, String sourceCode, String... expectedLines)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals(sourceCode, p.toSouceCode(targetType, builder));
        
        List<TestMethod> helperMethods = builder.getHelperMethods();
        assertEquals(1, helperMethods.size());
        
        String[] lines = helperMethods.get(0).toSourceCode().split("\\n");
        
        for (int i = 0; i < Math.min(expectedLines.length, lines.length); i++) {
            assertEquals(expectedLines[i], lines[0]);
        }
    }
    
    
 // DONT REMOVE - USED IN TEST
    public void foo1(Pair<Integer> pair) {}
    public void foo2(Pair<String> pair)  {}
    public <T> void foo3(List<Pair<T>> list) {}
    
    public static class Bean1
    {
        int x, y;
    }
    
    public static class Bean2
    {
        private Bean1 x;
        Bean2(Bean1 x) { this.x = x; }
    }
    
    public static class Bean3
    {
        private int x;
        void setX(int x) { this.x = x;}
    }
    
    public static class Pair<T>
    {
        T x;
        T y;
        Pair(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }
    
    private static class PrivateBean {}
}
