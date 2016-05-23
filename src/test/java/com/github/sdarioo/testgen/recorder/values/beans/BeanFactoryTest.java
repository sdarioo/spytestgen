/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.sdarioo.testgen.recorder.values.beans.Bean;
import com.github.sdarioo.testgen.recorder.values.beans.BeanFactory;

public class BeanFactoryTest 
{
    @Test
    public void testGetField() throws Exception
    {
        Simple s = new Simple();
        s.setAge(1);
        s.setName("name");
        
        java.lang.reflect.Field f = s.getClass().getDeclaredField("age");
        f.setAccessible(true);
        assertEquals(1, f.get(s));
        
    }
    
    @Test
    public void testEmptyBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Empty.class);
        assertNotNull(bean);
        assertTrue(bean.getConstructor().fields.isEmpty());
        assertTrue(bean.getGetters().isEmpty());
        assertTrue(bean.getGetters().isEmpty());
    }
    
    @Test
    public void testSimpleBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Simple.class);
        assertNotNull(bean);
        assertEquals(0, bean.getConstructor().fields.size());
        assertEquals(2, bean.getSetters().size());
        assertEquals(2, bean.getGetters().size());
    }
    
    @Test
    public void testNoSettersBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(NoSetters.class);
        assertNotNull(bean);
        assertEquals(2, bean.getConstructor().fields.size());
        assertEquals(0, bean.getSetters().size());
        assertEquals(0, bean.getGetters().size());
    }
    
    @Test
    public void testMixedBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Mixed.class);
        assertNotNull(bean);
        assertEquals(1, bean.getConstructor().fields.size());
        assertEquals(1, bean.getSetters().size());
        assertEquals(2, bean.getGetters().size());
    }
    
    @Test
    public void testBeanHierarchy()
    {
        assertTrue(BeanFactory.isBeanHierarchyAllowed(BeanSInterface.class));
        assertFalse(BeanFactory.isBeanHierarchyAllowed(BeanSClass.class));
    }
    
    @Test
    public void testNoSetterBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(NoSetter.class);
        assertNotNull(bean);
        assertEquals(0, bean.getConstructor().fields.size());
        assertEquals(0, bean.getSetters().size());
        assertEquals(0, bean.getGetters().size());
    }
    
    @Test
    public void testGenericBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Pair.class);
        assertNotNull(bean);
        assertTrue(bean.isAccessible());
    }
    
    @Test
    public void testPrivateBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(PrivateBean.class);
        assertNotNull(bean);
        assertFalse(bean.isAccessible());
    }
    
    public static class Empty
    {
    }
    
    public static class Simple
    {
        private int age;
        private String name;
        
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class NoSetters
    {
        private int age;
        private String name;
        
        public NoSetters(int age, String name)
        {
            this.age = age;
            this.name = name;
        }
    }
    
    public static class Mixed
    {
        private int age;
        private String name;
        
        public Mixed(int age)        
        {
            this.age = age;
        }
        
        public int getAge() {
            return age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class NoSetter
    {
        int x;
        int y;
    }
    
    public static class BeanSInterface 
        implements Comparable<BeanSInterface>
    {
        @Override
        public int compareTo(BeanSInterface o) {
            return 0;
        }
    }
    
    public static class BeanSClass extends Empty {}
    
    public static class Pair<T>
    {
        final T _x, _y;
        
        Pair(T x, T y) {
            _x = x;
            _y = y;
        }
        
        T getX() { return _x; }
        T getY() { return _y; }
    }
    
    private static class PrivateBean {}
}
