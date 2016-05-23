/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.util.TypeUtil;

public class MapValue
    extends AbstractValue
    implements IAggregateValue
{
    private final int _originalSize;
    
    private final Map<IValue, IValue> _elements;

    
    public MapValue(Map<?,?> map)
    {
        super(map.getClass());
        
        _originalSize = map.size();
        _elements = new HashMap<IValue, IValue>();
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            return;
        }
        
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            IValue key = ValuesFactory.newValue(entry.getKey());
            IValue value = ValuesFactory.newValue(entry.getValue());
            _elements.put(key, value);
        }
    }
    
    /**
     * @see com.github.sdarioo.testgen.recorder.values.IAggregateValue#getComponents()
     */
    @Override
    public Collection<IValue> getComponents() 
    {
        Set<IValue> components = new HashSet<IValue>();
        components.addAll(_elements.keySet());
        components.addAll(_elements.values());
        return components;
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        if (!isAnyOfAssignable(getGeneratedTypes(), targetType, errors)) {
            return false;
        }
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            errors.add(fmt("Map size exceeds maximum permitted size. Max={0}, current={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
            return false;
        }
        
        boolean isSupported = true;
        for (Map.Entry<IValue, IValue> entry : _elements.entrySet()) {
            isSupported = isSupported & 
                    (entry.getKey().isSupported(getKeyType(targetType), errors) && 
                     entry.getValue().isSupported(getValueType(targetType), errors));
        }
        return isSupported;
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        if (TypeUtil.containsTypeVariables(targetType)) {
            targetType = TypeUtil.getRawType(targetType);
            if (targetType == null) {
                targetType = Object.class;
            }
        }
        
        MethodTemplate asMapTemplate = getAsMapTemplate(targetType, builder);
        TestMethod asMap = builder.addHelperMethod(asMapTemplate, "asMap"); //$NON-NLS-1$
        TestMethod asPair = builder.addHelperMethod(AS_PAIR_TEMPLATE, "asPair"); //$NON-NLS-1$

        String elements = getElementsSourceCode(getKeyType(targetType), getValueType(targetType), asPair, builder);
        
        return fmt("{0}({1})", asMap.getName(), elements);
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
        MapValue other = (MapValue)obj;
        if (_originalSize != other._originalSize) {
            return false;
        }
        return _elements.equals(other._elements);
    }
    
    @Override
    public int hashCode() 
    {
        return _elements.hashCode();
    }
    
    protected Class<?>[] getGeneratedTypes() 
    {
        return new Class<?>[]{HashMap.class, TreeMap.class};
    }
    
    protected String getElementsSourceCode(Type keyType, Type valType, TestMethod asPair, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IValue, IValue> entry : _elements.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(asPair.getName());
            sb.append('(');
            sb.append(entry.getKey().toSouceCode(keyType, builder));
            sb.append(", "); //$NON-NLS-1$
            sb.append(entry.getValue().toSouceCode(valType, builder));
            sb.append(')');
        }
        return sb.toString();
    }
    
    private static Type getKeyType(Type targetType)
    {
        Type[] argTypes = TypeUtil.getActualTypeArguments(targetType);
        return argTypes.length == 2 ? argTypes[0] : null;
    }
    
    private static Type getValueType(Type targetType)
    {
        Type[] argTypes = TypeUtil.getActualTypeArguments(targetType);
        return argTypes.length == 2 ? argTypes[1] : null;
    }
    
    @SuppressWarnings("nls")
    private MethodTemplate getAsMapTemplate(Type targetType, TestSuiteBuilder builder)
    {
        Type mapType = getAssignable(getGeneratedTypes(), targetType);
        
        Type keyType = getKeyType(targetType);
        Type valType = getValueType(targetType);
        
        String keyCast = "";
        String valCast = "";
        
        if ((keyType != null) && (valType != null)) {
            mapType = TypeUtil.parameterize((Class<?>)mapType, keyType, valType);
            keyCast = '(' + TypeUtil.getName(keyType, builder) + ')';
            valCast = '(' + TypeUtil.getName(valType, builder) + ')';
        }
    
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.modifier(Modifier.PRIVATE | Modifier.STATIC);
        methodBuilder.name(MethodTemplate.NAME_VARIABLE);
        methodBuilder.returnType(targetType);
        methodBuilder.varg(Object[].class, "pairs");
    
        
        methodBuilder.statement(fmt("{0} map = new {0}()", TypeUtil.getName(mapType, builder)));
        
        String forLoop = 
            "for (Object[] pair : pairs) {\n" +
            fmt("map.put({0}pair[0], {1}pair[1]);\n", keyCast, valCast) +
            "}";
        
        methodBuilder.statement(forLoop, false);
        methodBuilder.statement("return map");
        
        String template = methodBuilder.build();
        return new MethodTemplate(template);
    }
    
    @SuppressWarnings("nls")
    private static final MethodTemplate AS_PAIR_TEMPLATE = new MethodTemplate(new String[] {
        "private static Object[] ${name}(Object key, Object value) {",
        "    return new Object[] { key, value };",
        "}"});

}
