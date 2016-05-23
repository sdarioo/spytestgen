package com.github.sdarioo.testgen.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.math.NumberUtils;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.values.ValuesFactory;
import com.github.sdarioo.testgen.util.Defaults;

public final class GeneratorUtil 
{
    
    private GeneratorUtil() {}
    
    /**
     * Search for accessible constructor with minimal number of parameters
     * @param clazz
     * @return constructor with minimal number of parameters or null
     */
    public static Constructor<?> findConstructor(Class<?> clazz)
    {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Arrays.sort(constructors, ConstructorComparator);
        
        for (Constructor<?> constructor : constructors) {
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                return constructor;
            }
        }
        return null;
    }
    
    /**
     * Returns array representing source code of default constructor parameters
     * @param constructor
     * @param builder
     * @return
     */
    public static String[] getDefaultArgSourceCode(Constructor<?> constructor, TestSuiteBuilder builder)
    {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        String[] result = new String[paramTypes.length];
        for (int i = 0; i < result.length; i++) {
            Object defaultValue = Defaults.getDefaultValue(paramTypes[i]);
            result[i] = ValuesFactory.newValue(defaultValue).toSouceCode(paramTypes[i], builder);
        }
        return result;
    }
    
    private static final Comparator<Constructor<?>> ConstructorComparator = 
            new Comparator<Constructor<?>>() 
    {
        @Override
        public int compare(Constructor<?> o1, Constructor<?> o2) {
            return NumberUtils.compare(o1.getParameterTypes().length, o2.getParameterTypes().length);
        }
    };

}
