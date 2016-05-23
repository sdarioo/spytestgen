/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

import java.util.List;

import com.github.sdarioo.testgen.util.StringUtil;

/**
 * Represents class field source code
 */
public class FieldSrc 
{
    private final String _name;
    private final String _source;
    
    public FieldSrc(String name, String source)
    {
        _name = name;
        _source = source;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String toSourceCode()
    {
        return _source;
    }
    
    public List<String> toSourceCodeLines()
    {
        return StringUtil.splitLines(_source);
    }
}
