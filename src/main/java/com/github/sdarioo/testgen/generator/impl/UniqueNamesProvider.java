/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

public class UniqueNamesProvider 
{
    private final Map<String, MutableInt> _names = new HashMap<String, MutableInt>();
    
    public UniqueNamesProvider()
    {
    }
    
    public UniqueNamesProvider(Set<String> usedNames)
    {
        this(usedNames.toArray(new String[0]));
    }
    
    public UniqueNamesProvider(String... usedNames)
    {
        for (String name : usedNames) {
            generateUniqueName(name);
        }
    }
    
    public String generateUniqueName(String name)
    {
        MutableInt count = _names.get(name);
        if (count == null) {
            count = new MutableInt(1);
            _names.put(name, count);
            return name;
        }
        count.increment();
        return name + count.intValue();
    }
    
}
