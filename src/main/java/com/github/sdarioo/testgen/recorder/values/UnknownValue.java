/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class UnknownValue
    extends AbstractValue
{
    
    public UnknownValue(Class<?> clazz)
    {
        super(clazz);
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        errors.add("Unsupported type: " + getRecordedType().getName()); //$NON-NLS-1$
        return false;
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        return IValue.NULL.toSouceCode(targetType, builder);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof UnknownValue)) {
            return false;
        }
        UnknownValue other = (UnknownValue)obj;
        return getRecordedType().equals(other.getRecordedType());
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode();
    }
    
}
