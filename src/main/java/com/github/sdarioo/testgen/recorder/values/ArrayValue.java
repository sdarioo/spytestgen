package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.util.TypeUtil;

public class ArrayValue
    extends CollectionValue
{
    private final Class<?> _arrayType;
    
    
    ArrayValue(Object array)
    {
        super(asList(array), new ArrayList<IValue>());
        
        _arrayType = array.getClass();
    }
    
    @Override
    public Class<?> getRecordedType() 
    {
        return _arrayType;
    }

    @Override
    protected Class<?> getGeneratedSourceCodeType() 
    {
        return _arrayType;
    }
    
    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        Class<?> componentType = getComponentType(targetType);
        return fmt(TEMPLATE, 
                builder.getTypeName(componentType),
                getElementsSourceCode(componentType, builder));
    }
    
    private Class<?> getComponentType(Type genericArrayType)
    {
        Class<?> componentType = null;
        if (genericArrayType instanceof GenericArrayType) {
            Type genericComponentType = ((GenericArrayType)genericArrayType).getGenericComponentType();
            componentType = TypeUtil.getRawType(genericComponentType);
        } else if (genericArrayType instanceof Class<?>) {
            componentType = ((Class<?>)genericArrayType).getComponentType();
        }
        if (componentType == null) {
            componentType = _arrayType.getComponentType();
        }
        return componentType;
    }

    private static List<?> asList(Object array)
    {
        List<Object> list = new ArrayList<Object>();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            list.add(Array.get(array, i));
        }
        return list;
    }


    private static final String TEMPLATE = "new {0}[]'{'{1}'}'"; //$NON-NLS-1$
}
