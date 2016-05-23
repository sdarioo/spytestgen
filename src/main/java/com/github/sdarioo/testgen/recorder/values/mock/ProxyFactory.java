package com.github.sdarioo.testgen.recorder.values.mock;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.util.TypeUtil;


public class ProxyFactory 
{
    public static boolean isProxy(Object value)
    {
        return (value != null) && 
               Proxy.isProxyClass(value.getClass()) &&
               (Proxy.getInvocationHandler(value) instanceof RecordingInvocationHandler);
    }
    
    public static RecordingInvocationHandler getHandler(Object proxy)
    {
        return (RecordingInvocationHandler)Proxy.getInvocationHandler(proxy);
    }
    
    public static boolean canProxy(Type type, Object value)
    {
        Class<?> rawType = TypeUtil.getRawType(type);
        if (rawType == null) {
            return false;
        }
        if (value == null) {
            return false;
        }
        if (Modifier.isPrivate(rawType.getModifiers())) {
            return false;
        }
        
        // List<Proxy>
        if (List.class.equals(rawType)) {
            Type[] elementType = TypeUtil.getActualTypeArguments(type);
            if (elementType.length == 1) {
               return canProxyList((List<?>)value, elementType[0]);
            }
            return false;
        }
        // Proxy[]
        if (rawType.isArray()) {
            Class<?> elementType = rawType.getComponentType();
            return canProxyArray(value, elementType);
        }
        if (!rawType.isInterface()) {
            return false;
        }
        String typeName = rawType.getName();
        if (!Configuration.getDefault().isMockingEnabled(typeName)) {
            return false;
        }
        return true;
    }
    
    public static Object newProxy(Type type, Object value)
    {
        return newProxy(type, value, new ProxiesCache());
    }

    public static Object newProxy(Type type, Object value, ProxiesCache cache)
    {
        if (isProxy(value)) {
            return value;
        }
        
        if (!canProxy(type, value)) {
            return null;
        }
        Class<?> rawType = TypeUtil.getRawType(type);
        
        // List<Proxy> 
        if (List.class.equals(rawType)) {
            Type elementType = TypeUtil.getActualTypeArguments(type)[0];
            List<?> list = (List<?>)value;
            List<Object> newList = new ArrayList<Object>();
            for (Object element : list) {
                newList.add(newProxy(elementType, element, cache));
            }
            return newList;
        }
        // Proxy[]
        if (rawType.isArray()) {
            int length = Array.getLength(value);
            Class<?> elementType = rawType.getComponentType();
            Object newArray = Array.newInstance(elementType, length);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                Array.set(newArray, i, newProxy(elementType, element, cache));
            }
            return newArray;
        }
        
        Class<?> proxyInterface = findProxyInterface(rawType, value);
        Class<?>[] interfaces = value.getClass().getInterfaces();
        for (Class<?> interfce : interfaces) {
            if (proxyInterface.isAssignableFrom(interfce)) {
                proxyInterface = interfce;
                break;
            }
        }
        Object proxy = cache.get(value);
        if (proxy == null) {
           proxy = Proxy.newProxyInstance(value.getClass().getClassLoader(), 
                    new Class<?>[]{ proxyInterface }, 
                    new RecordingInvocationHandler(proxyInterface, value, cache));
           
           proxy = cache.putIfAbsent(value, proxy);
        }
        return proxy;
    }
    
    private static boolean canProxyList(List<?> list, Type elementType)
    {
        if (list.size() > Configuration.getDefault().getMaxCollectionSize()) {
            return false;
        }
        for (Object object : list) {
            if (!canProxy(elementType, object)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean canProxyArray(Object array, Type elementType)
    {
        if (TypeUtil.getRawType(elementType) == null) {
            return false;
        }
        int length = Array.getLength(array);
        if (length > Configuration.getDefault().getMaxCollectionSize()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            if (!canProxy(elementType, element)) {
                return false;
            }
        }
        return true;
    }
    
    private static Class<?> findProxyInterface(Class<?> argumentType, Object value)
    {
        Class<?>[] interfaces = value.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            if (clazz.isAssignableFrom(argumentType) && canProxy(clazz, value)) {
                return clazz;
            }
        }
        return argumentType;
    }
    
}
