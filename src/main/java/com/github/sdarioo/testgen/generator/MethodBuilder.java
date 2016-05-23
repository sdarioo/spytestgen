/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import com.github.sdarioo.testgen.util.TypeUtil;

public class MethodBuilder 
{
    private int _modifier;
    private String _sName;
    private String _sReturnType = "void"; //$NON-NLS-1$
    private String _sTypeParameters = ""; //$NON-NLS-1$
    private String _sJavaDoc = null;
    
    private final List<String> _args = new ArrayList<String>();
    private final List<String> _annotations = new ArrayList<String>();
    private final List<String> _exceptions = new ArrayList<String>();
    
    private final List<String> _lines = new ArrayList<String>();
    
    
    private final TestSuiteBuilder _testSuiteBuilder;
    
    public MethodBuilder(TestSuiteBuilder builder)
    {
        _testSuiteBuilder = builder;
    }
    
    
    public String build()
    {
        StringBuilder sb = new StringBuilder();
        
        // comments
        if ((_sJavaDoc != null) && (_sJavaDoc.length() > 0)) {
            sb.append(_sJavaDoc);
            if (!_sJavaDoc.endsWith(String.valueOf(LS))) {
                sb.append(LS);
            }
        }
        
        // annotations
        for (String annotation : _annotations) {
            sb.append(annotation).append(LS);
        }
        
        // method signature
        String modifier = getModifier();
        if (modifier.length() > 0) {
            sb.append(modifier).append(' ');
        }
        
        if (_sTypeParameters.length() > 0) {
            sb.append(_sTypeParameters).append(' ');
        }
        sb.append(_sReturnType).append(' ');
        sb.append(_sName);
        sb.append('(');
        sb.append(join(_args));
        sb.append(')');
        
        // exceptions
        if (!_exceptions.isEmpty()) {
            sb.append(" throws "); //$NON-NLS-1$
            sb.append(join(_exceptions));
        }
        sb.append(' ').append('{').append(LS);
        
        // body
        for (String line : _lines) {
            sb.append(line).append(LS);
        }
        
        sb.append('}');
        return sb.toString();
    }
    
    public MethodBuilder modifier(int flag)
    {
        _modifier |= flag;
        return this;
    }
    
    public MethodBuilder name(String name)
    {
        _sName = name;
        return this;
    }
    
    public MethodBuilder returnType(Type type)
    {
        if ((type != null) && !Void.TYPE.equals(type)) {
            _sReturnType = TypeUtil.getName(type, _testSuiteBuilder);
        }
        return this;
    }
    
    public MethodBuilder arg(Type type, String name)
    {
        String typeName = TypeUtil.getName(type, _testSuiteBuilder);
        _args.add(typeName + ' ' + name);
        return this;
    }
    
    public MethodBuilder varg(Type type, String name)
    {
        String typeName = TypeUtil.getName(type, _testSuiteBuilder);
        _args.add(typeName + "... " + name); //$NON-NLS-1$
        return this;
    }
    
    public MethodBuilder args(Type[] types, String[] names)
    {
        for (int i = 0; i < types.length; i++) {
            arg(types[i], names[i]);
        }
        return this;
    }
    
    public MethodBuilder annotation(String line)
    {
        _annotations.add(line);
        return this;
    }
    
    public MethodBuilder exception(String exc)
    {
        _exceptions.add(_testSuiteBuilder.getTypeName(exc));
        return this;
    }
    
    public MethodBuilder statement(String statement)
    {
        return statement(statement, true);
    }
    
    @SuppressWarnings("nls")
    public MethodBuilder statement(String statement, boolean bTerminate)
    {
        // Support multiline statement
        String[] lines = statement.split("\\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = ((i == 0) || (i == (lines.length - 1))) ? 
                    indent(lines[i], 1) : 
                    indent(lines[i], 2);
            if (bTerminate && (i == lines.length - 1)) {
                line += ';';
            }
            _lines.add(line);
        }
        return this;
    }
    
    public MethodBuilder statements(List<String> lines)
    {
        for (String line : lines) {
            statement(line);
        }
        return this;
    }
    
    @SuppressWarnings("nls")
    public MethodBuilder ifThen(String condition, List<String> then)
    {
        statement(String.format("if (%s) {", condition), false);
        for (String line : then) {
            statement(indent(line, 1));
        }
        statement("}", false);
        
        return this;
    }
    
    @SuppressWarnings("nls")
    public MethodBuilder tryCatch(List<String> body, String exc, String... catchLines)
    {
        statement("try {", false);
        for (String line : body) {
            statement(indent(line, 1));
        }
        statement(String.format("} catch (%s) {", exc), false);
        for (String line : catchLines) {
            statement(indent(line, 1));
        }
        statement("}", false);
        
        return this;
    }
    
    public MethodBuilder comment(String comment)
    {
        _sJavaDoc = comment;
        return this;
    }
    
    private String getModifier()
    {
        return Modifier.toString(_modifier);
    }

    public MethodBuilder typeParams(TypeVariable<?>[] types)
    {
        StringBuilder sb = new StringBuilder();
        for (TypeVariable<?> var : types) {
            if (sb.length() == 0) {
                sb.append('<');
            } else {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(TypeUtil.getNameWithBounds(var, _testSuiteBuilder));
        }
        if (sb.length() > 0) {
            sb.append('>');
        }
        _sTypeParameters = sb.toString();
        return this;
    }
    
    private static String join(List<String> args)
    {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(arg);
        }
        return sb.toString();
    }
    
    private static String indent(String line, int level)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append(INDENT);
        }
        sb.append(line);
        return sb.toString();
    }
    
    private static final char LS = '\n';
    private static final String INDENT = "    "; //$NON-NLS-1$
}
