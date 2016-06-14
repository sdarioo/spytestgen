/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.util.TypeUtil;

public abstract class CollectionValue
    extends AbstractValue
    implements IAggregateValue
{
    private final int _originalSize;
    protected final Collection<IValue> _elements;
    
    protected CollectionValue(Collection<?> collection, 
            Collection<IValue> elements)
    {
        super(collection.getClass());
    
        _originalSize = collection.size();
        _elements = elements;
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            return;
        }
        for (Object obj : collection) {
            _elements.add(ValuesFactory.newValue(obj));
        }
    }
    
    protected abstract Class<?> getGeneratedSourceCodeType();
    
    /**
     * @see com.github.sdarioo.testgen.recorder.values.IAggregateValue#getComponents()
     */
    @Override
    public Collection<IValue> getComponents() 
    {
        return Collections.unmodifiableCollection(_elements);
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        if (!isAssignable(getGeneratedSourceCodeType(), targetType, errors)) {
            return false;
        }
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            errors.add(fmt("Collection size exceeds maximum permitted size. Max={0}, size={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
            return false;
        }
        boolean bResult = true;
        Type elementType = getElementType(targetType);
        for (IValue element : _elements) {
            bResult &= element.isSupported(elementType, errors); 
        }
        return bResult;
    }
    
    /**
     * @param builder
     * @return string representing comma separated list of all collection elements source code 
     */
    protected String getElementsSourceCode(Type elementTargetType, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (IValue param : _elements) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(param.toSourceCode(elementTargetType, builder));
        }
        return sb.toString();
    }

    /**
     * @return generic collection element type or null if collection is not parameterized type
     */
    protected static Type getElementType(Type paramType)
    {
        Type[] argTypes = TypeUtil.getActualTypeArguments(paramType);
        if (argTypes.length == 1) {
            return argTypes[0];
        }
        // ArrayParam also extends CollectionParam
        if (paramType instanceof Class<?>) {
            return ((Class<?>)paramType).getComponentType();
        }
        return null;
    }
    
    /**
     * @param builder
     * @return string representing collection element specification e.g <String> 
     * or empty string in no generic element information available
     */
    protected static String getElementTypeSpec(Type paramType, TestSuiteBuilder builder)
    {
        Type elementType = getElementType(paramType);
        if ((elementType == null) || 
             TypeUtil.containsTypeVariables(elementType) ||
             TypeUtil.containsWildcards(elementType) )
        {
            return ""; //$NON-NLS-1$
        }
        return '<' + TypeUtil.getName(elementType, builder) + '>';
    }
    
    @Override
    public int hashCode() 
    {
        return _elements.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CollectionValue other = (CollectionValue)obj;
        return (_originalSize == other._originalSize) && _elements.equals(other._elements);
    }
    
     
}
