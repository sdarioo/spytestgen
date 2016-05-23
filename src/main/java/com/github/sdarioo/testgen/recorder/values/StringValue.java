/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringEscapeUtils;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestMethod;

public class StringValue
    extends AbstractValue
{
    private final String _value;
    
    /**
     * @param value string value, never null
     * @pre value != null
     */
    public StringValue(String value)
    {
        super(String.class);
        _value = value;
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        int maxLength = Configuration.getDefault().getMaxStringLength();
        if (_value.length() <= maxLength) {
            return '\"' + StringEscapeUtils.escapeJava(_value) + '\"';
        }
        // Long strings will be placed in external resource files
        builder.addImport("java.io.*");
        ResourceFile resFile = builder.addResource(_value, "str");
        TestMethod resMethod = builder.addHelperMethod(RES_TEMPLATE, "res");
        return resMethod.getName() + "(\"" + resFile.getFileName() + "\")";
    }
    
    @Override
    public String toString() 
    {
        return _value;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof StringValue)) {
            return false;
        }
        StringValue other = (StringValue)obj;
        return Objects.equals(_value, other._value);
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(_value);
    }
    
    static int getLinesCount(String str)
    {
        int count = 1;
        
        int lidx = 0;
        int ridx = str.indexOf('\n');
        while (ridx >= lidx) {
            count++;
            lidx = ridx + 1;
            ridx = str.indexOf('\n', lidx);
        }
        return count;
    }
    
    @SuppressWarnings("nls")
    private static final MethodTemplate RES_TEMPLATE = new MethodTemplate(new String[] {
            "private static String ${name}(String res) {",
            "    StringBuilder sb = new StringBuilder();",
            "    try {", 
            "        InputStream is = ${test_suite_class}.class.getResourceAsStream(res);",
            "        BufferedReader reader = new BufferedReader(new InputStreamReader(is, \"UTF-8\"));",
            "        char[] cbuf = new char[1024];",
            "        int c = 0;",
            "        while ((c = reader.read(cbuf)) > 0) {",
            "            sb.append(cbuf, 0, c);",
            "        }",
            "        is.close();",
            "    } catch (IOException e) { Assert.fail(e.getMessage()); }",
            "    return sb.toString();",
            "}" });
    
}
