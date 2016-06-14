/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ListValue
    extends CollectionValue
{
    
    public ListValue(List<?> list)
    {
        super(list, new ArrayList<IValue>());
    }
    
    @Override
    protected Class<?> getGeneratedSourceCodeType() 
    {
        return List.class;
    }

    @SuppressWarnings("nls")
    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        Type elementType = getElementType(targetType);
        String elements = getElementsSourceCode(elementType, builder);
        builder.addImport(Arrays.class.getName());
        return fmt("Arrays.{0}asList({1})", getElementTypeSpec(targetType, builder), elements);
    }

}
