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

public class PrimitiveValue 
    extends AbstractValue
{
    private final String _sourceCode;
    
    PrimitiveValue(String sourceCode, Class<?> type)
    {
        super(type);
        _sourceCode = sourceCode;
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }
        
    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        return _sourceCode;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof PrimitiveValue)) {
            return false;
        }
        PrimitiveValue other = (PrimitiveValue)obj;
        return _sourceCode.equals(other._sourceCode);
    }
    
    @Override
    public int hashCode() 
    {
        return _sourceCode.hashCode();
    }
}
