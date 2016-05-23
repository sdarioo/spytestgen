/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values.beans;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.instrument.InstrumentUtil;
import com.github.sdarioo.testgen.logging.Logger;

public final class BeanFactory 
{
    private static BeanFactory INSTANCE = new BeanFactory();
    
    private final Map<Class<?>, Bean> _cache = new HashMap<Class<?>, Bean>();
    
    private BeanFactory() {}
    
    public static BeanFactory getInstance()
    {
        return INSTANCE;
    }
    
    public Bean getBean(Class<?> clazz)
    {
        Bean bean = _cache.get(clazz);
        if (bean == null) {
            if (isBeanHierarchyAllowed(clazz)) {
                bean = introspect(clazz);
            }
            
            if (bean == null) {
                bean = NOT_BEAN;
            }
            _cache.put(clazz, bean);
        }
        
        return (bean != NOT_BEAN) ? bean : null; 
    }
    
    private static Bean introspect(Class<?> clazz)
    {
        Bean bean = null;
        InputStream is = null;
        try {
            is = InstrumentUtil.readClass(clazz);
            if (is != null) {
                ClassReader reader = new ClassReader(is);
                BeanIntrospector introspector = new BeanIntrospector(clazz);
                reader.accept(introspector, 0);
                bean = introspector.getBean();
            } else {
                Logger.warn("Cannot load class bytes: " + clazz); //$NON-NLS-1$
            }
        } catch (IOException e) {
            Logger.warn(e.toString(), e);
        } finally {
            if (is != null) { try { is.close(); } catch (IOException e) {} }
        }
        return bean;
    }
    
    // Default access for junit tests
    static boolean isBeanHierarchyAllowed(Class<?> clazz)
    {
        Class<?> cl = clazz.getSuperclass();
        // java.lang.Object, interface, primitive, void
        if (cl == null) {
            return false;
        }
        if (cl.isInterface()) {
            return true;
        }
        return Object.class.equals(cl);
    }

    
    private static final Bean NOT_BEAN = new Bean(true, Collections.<Field>emptyList(),
            Constructor.DEFAULT,
            Collections.<Field, Method>emptyMap(),
            Collections.<Field, Method>emptyMap());
}
