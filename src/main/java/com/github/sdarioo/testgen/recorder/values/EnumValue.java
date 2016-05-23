package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class EnumValue
    extends AbstractValue
{
    private final String _name;
    
    EnumValue(Enum<?> e)
    {
        super(e.getClass());
        _name = e.toString();
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        String enumClass = builder.getTypeName(getRecordedType());
        return enumClass + '.' + _name;
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode() + 31 * _name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof EnumValue)) {
            return false;
        }
        EnumValue other = (EnumValue)obj;
        return getRecordedType().equals(other.getRecordedType()) && _name.equals(other._name);
    }
}
