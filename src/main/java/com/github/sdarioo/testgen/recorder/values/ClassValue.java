/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ClassValue
    extends AbstractValue
{
    private final Class<?> _clazz;
    
    public ClassValue(Class<?> clazz)
    {
        super(clazz);
        _clazz = clazz;
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }

    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        String simpeName = builder.getTypeName(_clazz);
        return fmt(fmt("{0}.class", simpeName)); //$NON-NLS-1$
    }

    @Override
    public String toString() 
    {
        return String.format("Class<%s>", _clazz.getName()); //$NON-NLS-1$
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof ClassValue)) {
            return false;
        }
        ClassValue other = (ClassValue)obj;
        return Objects.equals(_clazz, other._clazz);
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(_clazz);
    }
    
}
