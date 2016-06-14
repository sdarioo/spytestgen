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

/**
 * Base abstraction for recorded values
 */
public interface IValue 
{
    /**
     * @param targetType type to which this param value will be assign
     * @param errors collection for reason why param cannot be represented in source code
     * @return whether given parameter can be represented in source code
     */
    boolean isSupported(Type targetType, Collection<String> errors);
    
    /**
     * @param targetType target type 
     * @return source code text used by source code generator. May be null if
     * parameter is not valid {{@link #isValid(StringBuilder)} 
     */
    String toSourceCode(Type targetType, TestSuiteBuilder builder);
    
    /**
     * @return recorded value type, may be null if recorded null value
     */
    Class<?> getRecordedType();
  
    
    @Override
    boolean equals(Object obj);
    
    @Override
    int hashCode();
    
    
    /** Special value that represents result of void return methods */
    public static final IValue VOID = new IValue() {
        public boolean isSupported(Type targetType, Collection<String> errors) { return true; };
        public String toSourceCode(Type targetType, TestSuiteBuilder builder) { return null; };
        public Class<?> getRecordedType() { return Void.class; };
    };
    
    /** Special value that represents null object */
    public static final IValue NULL = new IValue() { 
        public boolean isSupported(Type targetType, Collection<String> errors) { return true; };
        public String toSourceCode(Type targetType, TestSuiteBuilder builder) { return "null"; }; //$NON-NLS-1$
        public Class<?> getRecordedType() { return null; };
    };

}
