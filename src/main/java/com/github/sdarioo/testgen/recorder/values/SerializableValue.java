package com.github.sdarioo.testgen.recorder.values;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.util.TypeUtil;

public class SerializableValue
    extends AbstractValue
{
    private final byte[] _bytes;
    
    public SerializableValue(Serializable value)
    {
        super(value.getClass());
        _bytes = SerializationUtils.serialize(value);
    }

    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        builder.addImport("java.io.*");
        MethodTemplate template = getFactoryMethodTemplate(targetType, builder);
        
        String resName = ClassUtils.getShortCanonicalName(getRecordedType());
        
        ResourceFile resFile = builder.addResource(_bytes, resName);
        TestMethod deserialize = builder.addHelperMethod(template, "deserialize");
        
        return deserialize.getName() + "(\"" + resFile.getFileName() + "\")";
    }
    
    private MethodTemplate getFactoryMethodTemplate(Type targetType, TestSuiteBuilder builder)
    {
        String returnType = TypeUtil.getName(targetType, builder);
        
        MethodTemplate template = builder.getTemplatesCache().get(returnType);
        if (template == null) {
            template = DESERIALIZE_TEMPLATE.with(MethodTemplate.TYPE_VARIABLE, returnType);
            builder.getTemplatesCache().put(returnType, template);
        }
        return template;
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
        SerializableValue other = (SerializableValue)obj;
        return getRecordedType().equals(other.getRecordedType()) && Arrays.equals(_bytes, other._bytes);
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode() + 31 * Arrays.hashCode(_bytes);
    }
    
    @SuppressWarnings("nls")
    private static final MethodTemplate DESERIALIZE_TEMPLATE = new MethodTemplate(new String[] {
    "private static ${type} ${name}(String res) throws Exception {",
    "    InputStream in = ${test_suite_class}.class.getResourceAsStream(res);",
    "    try {",
    "        ObjectInputStream stream = new ObjectInputStream(in);",
    "        return (${type})stream.readObject();",
    "    } finally {",
    "        in.close();",
    "    }",
    "}" });
}
