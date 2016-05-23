package com.github.sdarioo.testgen.recorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.ClassReader;

import com.github.sdarioo.testgen.instrument.ArgNamesIntrospector;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.util.IOUtil;

public final class ArgNamesCache 
{
    private static ConcurrentMap<String, String[]> NAMES = new ConcurrentHashMap<String, String[]>();
    
    private ArgNamesCache() {}
    
    public static String[] getArgNames(java.lang.reflect.Method method)
    {
        return getArgNames(method, false);
    }
    
    public static String[] getArgNames(java.lang.reflect.Method method, boolean bIntrospect)
    {
        Class<?> declaringClass = method.getDeclaringClass();
        String typeDesc = org.objectweb.asm.Type.getDescriptor(declaringClass);
        String methodName = method.getName();
        String methodDesc = org.objectweb.asm.Type.getMethodDescriptor(method);
        String key = getKey(typeDesc, methodName, methodDesc);
        String[] names =  NAMES.get(key);
        if ((names == null) && bIntrospect) {
            introspectArgNames(declaringClass);
        }
        names = NAMES.get(key);
        if (names == null) {
            names = defaultArgNames(method);
        }
        return names;
    }
    
    public static void setArgNames(org.objectweb.asm.Type type, org.objectweb.asm.commons.Method method, String[] names)
    {
        if (!isValidArgNames(names)) {
            return;
        }
        
        String key = getKey(type.getDescriptor(), method.getName(), method.getDescriptor());
        NAMES.put(key, names);
    }
    
    public static void clear()
    {
        NAMES.clear();
    }
    
    private static String getKey(String typeDesc, String methodName, String methodDesc)
    {
        return typeDesc + ':' + methodName + ':' + methodDesc;
    }
    
    private static void introspectArgNames(Class<?> clazz)
    {
        InputStream is = null;
        try {
            is = InstrumentUtil.readClass(clazz);
            if (is != null) {
                ClassReader reader = new ClassReader(is);
                ArgNamesIntrospector introspector = new ArgNamesIntrospector();
                reader.accept(introspector, 0);
            }
        } catch (IOException e) {
            Logger.warn(e.toString(), e);
        } finally {
            IOUtil.close(is);
        }
    }
    
    private static String[] defaultArgNames(java.lang.reflect.Method method)
    {
        int count = method.getParameterTypes().length;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = "arg" + String.valueOf(i); //$NON-NLS-1$
        }
        return result;
    }
 
    private static boolean isValidArgNames(String... names)
    {
        for (String name : names) {
            if (name == null) {
                return false;
            }
        }
        return true;
    }
}
