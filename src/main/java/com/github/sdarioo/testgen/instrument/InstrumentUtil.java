package com.github.sdarioo.testgen.instrument;

import java.io.InputStream;

import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.logging.Logger;

public final class InstrumentUtil 
{
    private InstrumentUtil() {}

    public static boolean isFlagSet(int access, int... flags)
    {
        for (int flag : flags) {
            if ((access & flag) == flag) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPrimitive(Type t)
    {
        return t.getSort() <= 8;
    }
    
    public static InputStream readClass(Class<?> clazz)
    {
        String name = clazz.getName();
        if (name == null) {
            Logger.warn("Cannot read annonymous class: " + clazz); //$NON-NLS-1$
            return null;
        }
        ClassLoader classLoader = clazz.getClassLoader();
        String resource = name.replace('.', '/') + ".class"; //$NON-NLS-1$
        if (classLoader != null) {
            return classLoader.getResourceAsStream(resource);
        }
        return ClassLoader.getSystemResourceAsStream(resource);
    }
    
}
