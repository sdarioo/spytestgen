/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;

// ThreadSafe
public final class Recorder
{
    private final String _name;
    private final ConcurrentMap<Class<?>, RecordedClass> _recordedClasses = new ConcurrentHashMap<>();
    
    private static final Recorder DEFAULT = new Recorder("Default"); //$NON-NLS-1$

    
    public static Recorder getDefault()
    {
        return DEFAULT;
    }
    public static Recorder newRecorder(String name)
    {
        return new Recorder(name);
    }
    
    private Recorder(String name)
    {
        _name = name;
    }
    
    public boolean record(Call call)
    {
        RecordedClass recordedClass = getRecordedClass(call, true);
        if (recordedClass.record(call)) {
            logCall(call);
            return true;
        }
        return false;
    }

    public Collection<RecordedClass> getRecordedClasses()
    {
        return _recordedClasses.values();
    }
    
    public RecordedClass getRecordedClass(Method method, Object target, boolean bCreate)
    {
        Class<?> clazz = method.getDeclaringClass();
        if ((target != null) && !Configuration.getDefault().isAlwaysGenerateForDeclaringClass()) {
            clazz = target.getClass();
        }
        return getRecordedClass(clazz, bCreate);
    }

    public RecordedClass getRecordedClass(Call call, boolean bCreate)
    {
        Class<?> clazz = call.getMethod().getDeclaringClass();
        if (!call.isStatic() && !Configuration.getDefault().isAlwaysGenerateForDeclaringClass()) {
            clazz = call.getTargetClass();
        }
        return getRecordedClass(clazz, bCreate);
    }
    
    private RecordedClass getRecordedClass(Class<?> clazz, boolean bCreate)
    {
        RecordedClass recordedClass = _recordedClasses.get(clazz);
        if (bCreate && (recordedClass == null)) {
            recordedClass = new RecordedClass(clazz);
            RecordedClass other = _recordedClasses.putIfAbsent(clazz, recordedClass);
            if (other != null) {
                recordedClass = other;
            }
        }
        return recordedClass;
    }
    
    @SuppressWarnings("nls")
    private void logCall(Call call)
    {
        Set<String> errors = new HashSet<String>();
        boolean isSupported = call.isSupported(errors);
        
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("[{0}] Recording {1} call: {2}", _name, (isSupported ? "supported" : "unsupported"), call));
        if (call.getExceptionInfo() != null) {
            sb.append("\n   Expected exception=").append(call.getExceptionInfo().getClassName());
        }
        
        for (String msg : errors) {
            sb.append("\n   " + msg); //$NON-NLS-1$
        }
        if (isSupported) {
            Logger.info(sb.toString());
        } else {
            Logger.warn(sb.toString());
        }
    }
   
}
