/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.MessageFormat;
import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.util.TypeUtil;

public abstract class AbstractValue
    implements IValue
{
    private final Class<?> _recordedType;
    
    protected AbstractValue(Class<?> recordedType)
    {
        _recordedType = recordedType;
    }
    
    @Override
    public Class<?> getRecordedType() 
    {
        return _recordedType;
    }

    protected static boolean isAssignable(Type type, Type targetType, Collection<String> errors)
    {
        if (targetType instanceof TypeVariable<?>) {
            return true; // TODO - verify this, bounded variables??
        }
        
        if (org.apache.commons.lang3.reflect.TypeUtils.isAssignable(type, targetType)) {
            return true;
        }
        errors.add("Unsupported type: " + TypeUtil.getName(targetType, new TestSuiteBuilder())); //$NON-NLS-1$
        return false;
    }
    
    protected static boolean isAnyOfAssignable(Type[] types, Type targetType, Collection<String> errors)
    {
        if (getAssignable(types, targetType) != null) {
            return true;
        }
        errors.add("Unsupported type: " + TypeUtil.getName(targetType, new TestSuiteBuilder())); //$NON-NLS-1$
        return false;
    }

    protected static Type getAssignable(Type[] types, Type targetType)
    {
        for (Type type : types) {
            if (org.apache.commons.lang3.reflect.TypeUtils.isAssignable(type, targetType)) {
                return type;
            }
        }
        return null;
    }
    
    protected static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
    
}
