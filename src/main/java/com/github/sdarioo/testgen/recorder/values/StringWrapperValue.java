package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

/**
 * Represents classes that has constructor with single String argument or static factory method:
 * - fromString(String)
 * - valueOf(String)
 */
public class StringWrapperValue
    extends AbstractValue
{
    private final StringValue _stringParam;
    private final String _factoryMethod;

    
    public StringWrapperValue(Object value, String factoryMethod)
    {
        super(value.getClass());
        
        _factoryMethod = factoryMethod;
        _stringParam = new StringValue(value.toString());
    }

    @Override
    public boolean isSupported(Type targetType, Collection<String> errors)
    {
        return _stringParam.isSupported(String.class, errors);
    }

    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        Class<?> clazz = getRecordedType();
        
        String typeName = builder.getTypeName(clazz);
        String str = _stringParam.toSourceCode(String.class, builder);
        
        
        return fmt("{0}.{1}({2})", typeName, _factoryMethod, str); //$NON-NLS-1$
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(getRecordedType(), _stringParam);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StringWrapperValue)) {
            return false;
        }
        StringWrapperValue other = (StringWrapperValue)obj;
        return getRecordedType().equals(other.getRecordedType()) && _stringParam.equals(other._stringParam);
    }
    

    
}
