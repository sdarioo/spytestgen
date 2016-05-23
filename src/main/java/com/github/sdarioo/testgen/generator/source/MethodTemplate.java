package com.github.sdarioo.testgen.generator.source;

import com.github.sdarioo.testgen.util.StringUtil;

public class MethodTemplate 
{
    public final String _template;
    
    public MethodTemplate(String template)
    {
        _template = template;
    }
    
    public MethodTemplate(String[] templateLines)
    {
        this(StringUtil.join(templateLines, "\n")); //$NON-NLS-1$
    }
    
    public String toString()
    {
        return _template;
    }
    
    public MethodTemplate withName(String name)
    {
        return with(NAME_VARIABLE, name);
    }
    
    public MethodTemplate with(String variable, String value)
    {
        if (value != null) {
            String newTemplate = _template.replace(variable, value);
            return new MethodTemplate(newTemplate);
        } else {
            return this;
        }
    }
    
    @Override
    public int hashCode() 
    {
        return _template.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof MethodTemplate)) {
            return false;
        }
        MethodTemplate other = (MethodTemplate)obj;
        return _template.equals(other._template);
    }

    public static final String NAME_VARIABLE = "${name}"; //$NON-NLS-1$
    public static final String TYPE_VARIABLE = "${type}"; //$NON-NLS-1$
    public static final String SUITE_CLASS_VARIABLE = "${test_suite_class}"; //$NON-NLS-1$
}
