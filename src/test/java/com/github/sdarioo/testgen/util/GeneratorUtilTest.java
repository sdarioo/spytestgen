package com.github.sdarioo.testgen.util;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import org.junit.Test;

public class GeneratorUtilTest 
{
    @Test
    public void noConstructor()
    {
        assertNull(GeneratorUtil.findConstructor(Private.class));
    }
    
    @Test
    public void shouldUseDefaultConstructor()
    {
        Constructor<?> constructor = GeneratorUtil.findConstructor(Public.class);
        assertNotNull(constructor);
        assertEquals(0, constructor.getParameterTypes().length);
    }
    
    
    private static class Private
    {
        private Private() {}
    }
    private static class Public
    {
        public Public(int x) {}
        public Public(int x, int y) {}
        public Public() {}
    }
}
