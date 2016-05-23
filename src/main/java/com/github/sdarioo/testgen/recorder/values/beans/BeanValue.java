/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values.beans;

import java.lang.reflect.*;
import java.util.*;

import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.values.*;
import com.github.sdarioo.testgen.util.TypeUtil;


public class BeanValue
    extends AbstractValue
    implements IAggregateValue
{
    private final Bean _bean;
    
    private final List<Field> _fields;
    private final List<IValue> _values;
    
    
    public BeanValue(Object obj, Bean bean)
    {
        super(obj.getClass());
        _bean = bean;
    
        _fields = new ArrayList<Field>();
        _values = new ArrayList<IValue>();
        
        for (Field field : bean.getFields()) {
            _fields.add(field);
            _values.add(getFieldValue(obj, field));
        }
    }
    
    @Override
    public Collection<IValue> getComponents() 
    {
        return Collections.unmodifiableCollection(_values);
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        if (!_bean.isAccessible()) {
            errors.add(fmt("Bean {0} is not accessible.", getRecordedType().getName())); //$NON-NLS-1$
            return false;
        }
        
        boolean bResult = true;
        for (int i = 0; i < _fields.size(); i++) {
            Type fieldType = getFieldType(_fields.get(i), targetType);
            if (!_values.get(i).isSupported(fieldType, errors)) {
                bResult = false;
            }
        }
        return bResult;
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _fields.size(); i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            Type fieldType = getFieldType(_fields.get(i), targetType);
            sb.append(_values.get(i).toSouceCode(fieldType, builder));
        }
        
        String factoryMethodName = getFactoryMethodName(builder);
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate(builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        return fmt("{0}({1})", factoryMethod.getName(), sb.toString());
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
        BeanValue other = (BeanValue)obj;
        return getRecordedType().equals(other.getRecordedType()) &&
                _values.equals(other._values);
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode() + (31 * _values.hashCode());
    }
    
    private IValue getFieldValue(Object obj, Field field)
    {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(obj);
            field.setAccessible(accessible);
            return ValuesFactory.newValue(value);
        } catch (Throwable e) {
            Logger.error(e.toString());
        }
        return IValue.NULL;
    }
    
    private Type getFieldType(Field field, Type targetType)
    {
        TypeVariable<?>[] typeParams = getRecordedType().getTypeParameters();
        Type[] actualTypeParams = TypeUtil.getActualTypeArguments(targetType);
        
        Type fieldType = field.getGenericType();
        if (TypeUtil.containsTypeVariables(fieldType)) {
            int index = Arrays.asList(typeParams).indexOf(fieldType);
            if (index >= 0 && (typeParams.length == actualTypeParams.length)) {
                fieldType = actualTypeParams[index];
            } else {
                fieldType = field.getType();
            }
        }
        return fieldType;
    }
    
    private String getFactoryMethodName(TestSuiteBuilder builder)
    {
        String objectClass = builder.getTypeName(getRecordedType());
        int index = objectClass.lastIndexOf('.');
        if (index > 0) {
            objectClass = objectClass.substring(index + 1);
        }
        return "new" + objectClass; //$NON-NLS-1$
    }
    
    @SuppressWarnings("nls")
    private MethodTemplate getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        Class<?> clazz = getRecordedType();
        Type type = TypeUtil.parameterize(clazz);
        String typeName = TypeUtil.getName(type, builder);
        
        MethodTemplate template = builder.getTemplatesCache().get(typeName);
        if (template != null) {
            return template;
        }
        
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.name(MethodTemplate.NAME_VARIABLE).
            modifier(Modifier.PRIVATE | Modifier.STATIC).
            typeParams(clazz.getTypeParameters()).
            returnType(type);
        
        for (Field field : _fields) {
            methodBuilder.arg(field.getGenericType(), paramName(field));
        }
        
        Set<Field> fieldsToSet = new HashSet<Field>(_fields);
        
        // Constructor 
        StringBuilder constructorArgs = new StringBuilder();
        for (Field field : _bean.getConstructor().getFields()) {
            if (constructorArgs.length() > 0) {
                constructorArgs.append(", ");
            }
            constructorArgs.append(paramName(field));
            fieldsToSet.remove(field);
        }
        methodBuilder.statement(fmt("{0} result = new {0}({1})", typeName, constructorArgs.toString()));
        
        // Setters + direct field set
        for (Field field : fieldsToSet) {
            Method method = _bean.getSetters().get(field);
            if (method != null) {
                methodBuilder.statement(fmt("result.{0}({1})", method.getName(), paramName(field)));
            } else {
                methodBuilder.statement(fmt("result.{0} = {1}", field.getName(), paramName(field)));
            }
        }
        methodBuilder.statement("return result");
        template = new MethodTemplate(methodBuilder.build());
        builder.getTemplatesCache().put(typeName, template);
        return template;
    }
    
    private static String paramName(Field field)
    {
        String result = field.getName();
        if (result.startsWith("_")) { //$NON-NLS-1$
            result = result.substring(1);
        }
        return result;
    }
    
}
