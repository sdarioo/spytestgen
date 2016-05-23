/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.TestMethod;

public class PropertiesValue 
    extends MapValue
{
    public PropertiesValue(Properties props)
    {
        super(propsToMap(props));
    }
    
    @Override
    public Class<?> getRecordedType() 
    {
        return Properties.class;
    }
    
    @Override
    protected Class<?>[] getGeneratedTypes() 
    {
        return new Class<?>[]{Properties.class};
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        builder.addImport(Properties.class.getName());
        
        TestMethod asProps = builder.addHelperMethod(AS_PROPS_TEMPLATE, "asProps"); //$NON-NLS-1$
        TestMethod asPair = builder.addHelperMethod(AS_PAIR_TEMPLATE, "asPair"); //$NON-NLS-1$
     
        String elements = getElementsSourceCode(String.class, String.class, asPair, builder);
        return fmt("{0}({1})", asProps.getName(), elements);
    }
    
    private static Map<String, String> propsToMap(Properties props)
    {
        Map<String, String> map = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String sKey = (String)key;
            map.put(sKey, props.getProperty(sKey));
        }
        return map;
    }

    @SuppressWarnings("nls")
    private static final MethodTemplate AS_PAIR_TEMPLATE = new MethodTemplate(new String[] {
            "private static String[] ${name}(String key, String value) {",
            "    return new String[] { key, value };",
            "}" });
    
    @SuppressWarnings("nls")
    private static final MethodTemplate AS_PROPS_TEMPLATE = new MethodTemplate(new String[] {
            "private static Properties ${name}(String[]... pairs) {",
            "    Properties p = new Properties();",
            "    for (String[] pair : pairs) {",
            "        p.setProperty(pair[0], pair[1]);",
            "    }",
            "    return p;",
            "}" });
    
}
