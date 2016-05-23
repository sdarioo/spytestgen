/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values.beans;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.commons.Method;

public final class Constructor
{
    Method method;
    List<Field> fields;
    
    public Constructor(Method method, List<Field> fields)
    {
        this.method = method;
        this.fields = new ArrayList<Field>(fields);
    }
    
    public List<Field> getFields()
    {
        return fields;
    }
    
    @Override
    public int hashCode() 
    {
        return method.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof Constructor)) {
            return false;
        }
        Constructor other = (Constructor)obj;
        return method.equals(other.method);
    }
    
    @SuppressWarnings("nls")
    public static final Constructor DEFAULT = new Constructor(new Method("<init>", "()V"), Collections.<Field>emptyList());
}
