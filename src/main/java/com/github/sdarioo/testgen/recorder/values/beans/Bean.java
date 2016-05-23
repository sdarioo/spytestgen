/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values.beans;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.commons.Method;

public class Bean
{
    private final boolean _isAccessible;
    
    private final List<Field> _fields;
    private final Constructor _constructor;
    private final Map<Field, Method> _setters;
    private final Map<Field, Method> _getters;
    
    public Bean(boolean isAccessible,
            List<Field> fields,
            Constructor constructor, 
            Map<Field, Method> getters,
            Map<Field, Method> setters)
    {
        _isAccessible = isAccessible;
        _fields = fields;
        _constructor = constructor;
        _getters = getters;
        _setters = setters;
    }
    
    public boolean isAccessible()
    {
        return _isAccessible;
    }
    
    public List<Field> getFields()
    {
        return _fields;
    }
    
    public Constructor getConstructor()
    {
        return _constructor;
    }
    
    public Map<Field, Method> getGetters()
    {
        return _getters;
    }
    
    public Map<Field, Method> getSetters()
    {
        return _setters;
    }

}
