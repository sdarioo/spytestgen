/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

import java.util.*;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.util.Formatter;
import com.github.sdarioo.testgen.util.StringUtil;

public class TestClass
{
    private final String _qName;
    private final String _signature;
    private final List<String> _imports;
    private final List<FieldSrc> _fields;
    private final List<TestMethod> _methods;
    private final List<ResourceFile> _resources;
    
    public TestClass(String name, String signature, 
            Collection<String> imports, 
            Collection<FieldSrc> fields,
            Collection<TestMethod> methods, 
            Collection<ResourceFile> resources)
    {
        _qName = name;
        _signature = signature;
        _imports = new ArrayList<>(imports);
        _fields = new ArrayList<>(fields);
        _methods = new ArrayList<>(methods);
        _resources = new ArrayList<>(resources);
        
        Collections.sort(_imports);
        Collections.sort(_methods);
    }
    
    public String getName()
    {
        return _qName;
    }
    
    public String getPackage()
    {
        return ClassUtils.getPackageCanonicalName(_qName);
    }
    
    public String getFileName()
    {
        String name = ClassUtils.getShortCanonicalName(_qName);
        return name + FILE_EXT;
    }
    
    @SuppressWarnings("nls")
    public String toSourceCode() 
    {
        List<String> lines = new ArrayList<>();
        lines.add(AUTO_GENERATED_SIGNATURE);
        
        String pkg = getPackage();
        if ((pkg != null) && (pkg.length() > 0)) {
            lines.add(String.format("package %s;", pkg));
        }
        if (!_imports.isEmpty()) {
            lines.add("");
            for (String imprt : _imports) {
                lines.add(String.format("import %s;", imprt));
            }
        }
        lines.add("");
        lines.add("");
        lines.add(_signature);
        lines.add("{");
        
        if (!_fields.isEmpty()) {
            for (FieldSrc field : _fields) {
                List<String> fieldLines = field.toSourceCodeLines();
                fieldLines = Formatter.indentLines(fieldLines);
                lines.addAll(fieldLines);
            }
            lines.add("");
            lines.add("");
        }
        for (TestMethod method : _methods) {
            List<String> methodLines = method.toSourceCodeLines();
            methodLines = Formatter.indentLines(methodLines);
            lines.addAll(methodLines);
            lines.add("");
        }

        lines.add("}");
        lines.add("");
        
        return StringUtil.join(lines, "\n");
    }
    
    public List<ResourceFile> getResources() 
    {
        return Collections.unmodifiableList(_resources);
    }

    
    public static final String FILE_EXT = ".java"; //$NON-NLS-1$
    
    // When first line in existing file matches this comment than test will be overwritten.
    // Otherwise new test suite file will be created.
    public static final String AUTO_GENERATED_SIGNATURE = "// AUTO-GENERATED"; //$NON-NLS-1$
    
}
