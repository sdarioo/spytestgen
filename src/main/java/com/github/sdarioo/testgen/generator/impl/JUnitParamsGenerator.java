/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.lang.reflect.*;
import java.util.*;

import com.github.sdarioo.testgen.generator.AbstractTestSuiteGenerator;
import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.values.IValue;
import com.github.sdarioo.testgen.util.GeneratorUtil;
import com.github.sdarioo.testgen.util.StringUtil;
import com.github.sdarioo.testgen.util.TypeUtil;

public class JUnitParamsGenerator
    extends AbstractTestSuiteGenerator
{
    private int _testMethodOrder = 0;
    private int _paramMethodOrder = 1000;
    
    
    @Override
    protected void initTestSuite(Class<?> targetClass, TestSuiteBuilder builder) 
    {
        builder.addImport("org.junit.Assert"); //$NON-NLS-1$
        builder.addImport("org.junit.Test"); //$NON-NLS-1$
        builder.addImport("org.junit.runner.RunWith"); //$NON-NLS-1$
        builder.addImport("junitparams.Parameters"); //$NON-NLS-1$
        builder.addImport("junitparams.JUnitParamsRunner"); //$NON-NLS-1$
        
        String pkg = ""; //$NON-NLS-1$
        if (targetClass.getPackage() != null) {
            pkg = targetClass.getPackage().getName();
        }
        String name = getTestClassName(targetClass, builder);
        
        String signature =  fmt(CLASS_SIGNATURE_TEMPLATE, name);
        builder.setCanonicalName((pkg.length() > 0) ? pkg + '.' + name : name);
        builder.setSignature(signature);
    }
    
    @Override
    protected void addTestCases(Class<?> targetClass, Method method, 
            List<Call> callsWithResult, 
            TestSuiteBuilder builder) 
    {
        TestMethod testCase = generateTestCase(targetClass, method, builder);
        TestMethod paramProvider = generateParamsProvider(method, callsWithResult, testCase.getName(), builder);
        
        builder.addTestCase(paramProvider);
        builder.addTestCase(testCase);
    }

    @Override
    protected void addTestCasesForExceptions(Class<?> targetClass, Method method, 
            List<Call> callsWithException,
            TestSuiteBuilder builder) 
    {
        for (Call call : callsWithException) {
            if (!call.isSupported(new HashSet<String>())) {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            Type[] paramGenericTypes = method.getGenericParameterTypes();
            
            List<IValue> callArgs = call.args();
            List<String> callArgsCode = new ArrayList<String>();
            
            for (int i = 0; i < callArgs.size(); i++) {
                Type type = getTypeWithoutVariables(paramTypes[i], paramGenericTypes[i]);
                callArgsCode.add(callArgs.get(i).toSouceCode(type, builder));
            }
            // Tested method invocation - should throw exception
            List<String> body = getCall(targetClass, method, toArray(callArgsCode), null, builder);
            
            String name = getTestCaseName(method, builder);
            String exception = builder.getTypeName(call.getExceptionInfo().getClassName());
            
            MethodBuilder methodBuilder = new MethodBuilder(builder);
            methodBuilder.modifier(Modifier.PUBLIC).
                annotation(fmt("@Test(expected={0}.class)", exception)). //$NON-NLS-1$
                name(name).
                exception(exception).
                statements(body);
            
            String source = methodBuilder.build();
            TestMethod testCase = new TestMethod(name, source, _testMethodOrder++);
            builder.addTestCase(testCase);
        }
    }
    
    @SuppressWarnings("nls")
    protected TestMethod generateTestCase(Class<?> targetClass, Method method, TestSuiteBuilder builder)
    {
        String name = getTestCaseName(method, builder);
        String paramProviderName = getParamsProviderMethodName(name);
        
        Type[] paramTypes = method.getGenericParameterTypes();
        String[] paramNames = getParameterNames(method);
        
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.modifier(Modifier.PUBLIC).
                      annotation("@Test").
                      annotation(fmt("@Parameters(method = \"{0}\")", paramProviderName)).
                      name(name).
                      args(paramTypes, paramNames).
                      exception(Exception.class.getName()).
                      typeParams(method.getTypeParameters());
        
        if (hasReturn(method)) {
            methodBuilder.arg(method.getGenericReturnType(), EXPECTED);
        }
       
        List<String> body = getCall(targetClass, method, paramNames, RESULT, builder);
        if (hasReturn(method)) {
            if (method.getReturnType().isArray()) {
                body.add(fmt("Assert.assertArrayEquals({0}, {1})", EXPECTED, RESULT)); //$NON-NLS-1$
            } else {
                body.add(fmt("Assert.assertEquals({0}, {1})", EXPECTED, RESULT)); //$NON-NLS-1$
            }
        }
        methodBuilder.statements(body);
        
        String source = methodBuilder.build();
        return new TestMethod(name, source, _testMethodOrder++);
    }
    
    // Protected for junit tests
    protected TestMethod generateParamsProvider(Method method, List<Call> calls, 
            String testCaseName, TestSuiteBuilder builder)
    {
        String name = getParamsProviderMethodName(testCaseName);

        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.modifier(Modifier.PRIVATE | Modifier.STATIC).
            annotation("@SuppressWarnings(\"unused\")"). //$NON-NLS-1$
            name(name).
            returnType(Object[].class).
            exception(Exception.class.getName());
        
        Set<String> errors = new HashSet<String>();
        StringBuilder stmt = new StringBuilder();
        
        for (Call call : calls) {
            if (!call.isSupported(errors)) {
                continue;
            }
            if (stmt.length() > 0) {
                stmt.append(",\n"); //$NON-NLS-1$
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            Type[] paramGenericTypes = method.getGenericParameterTypes();
            
            List<IValue> callArgs = call.args();
            List<String> callArgsCode = new ArrayList<String>();
            for (int i = 0; i < callArgs.size(); i++) {
                Type type = getTypeWithoutVariables(paramTypes[i], paramGenericTypes[i]);
                callArgsCode.add(callArgs.get(i).toSouceCode(type, builder));
            }
            
            if (hasReturn(method)) {
                Type type = getTypeWithoutVariables(method.getReturnType(), method.getGenericReturnType());
                callArgsCode.add(call.getResult() != null ? call.getResult().toSouceCode(type, builder) : "null"); //$NON-NLS-1$
            }
            stmt.append(fmt("new Object[]'{' {0} '}'", join(toArray(callArgsCode)))); //$NON-NLS-1$
        }
        
        methodBuilder.statement(fmt("return new Object[] '{'\n{0}\n'}'", stmt.toString())); //$NON-NLS-1$
        
        String javadoc = getProblemsComment(errors);
        methodBuilder.comment(javadoc);
        
        String source = methodBuilder.build();
        return new TestMethod(name, source, _paramMethodOrder++);
    }
    
    private static boolean hasReturn(Method method)
    {
        return !Void.TYPE.equals(method.getReturnType());
    }
    
    private List<String> getCall(Class<?> targetClass, Method method, String[] args, 
            String variable, TestSuiteBuilder builder)
    {
        List<String> lines = new ArrayList<String>();
        
        String typeName = builder.getTypeName(targetClass);
        String callTarget;
        
        if (Modifier.isStatic(method.getModifiers())) {
            callTarget = typeName;
        } else {
            Constructor<?> constructor = GeneratorUtil.findConstructor(targetClass);
            String[] constructorArgs = new String[0];
            if (constructor == null) {
                lines.add(fmt("// ERROR - class {0} has no accessible constructors", typeName)); //$NON-NLS-1$
            } else if (constructor.getParameterTypes().length > 0) {
                lines.add(fmt("// WARNING - constructing {0} with default parameters", typeName)); //$NON-NLS-1$
                constructorArgs = GeneratorUtil.getDefaultArgSourceCode(constructor, builder);
            }
            lines.add(fmt("{0} obj = new {0}({1})", typeName, join(constructorArgs))); //$NON-NLS-1$
            callTarget = "obj"; //$NON-NLS-1$
        }
        
        if (hasReturn(method) && (variable != null)) {
            lines.add(fmt("{0} {1} = {2}.{3}({4})",  //$NON-NLS-1$
                    TypeUtil.getName(method.getGenericReturnType(), builder), variable, 
                    callTarget, method.getName(), join(args)));
        } else {
            lines.add(fmt("{0}.{1}({2})", callTarget, method.getName(), join(args))); //$NON-NLS-1$
        }
        return lines;
    }
    
    private static String getParamsProviderMethodName(String testCaseName)
    {
        // TestCase name is unique and it guarantees provider name uniqueness
        return testCaseName + "_Parameters"; //$NON-NLS-1$
    }
    
    private static Type getTypeWithoutVariables(Class<?> rawParamType, Type genericParamType)
    {
        if (TypeUtil.containsTypeVariables(genericParamType)) {
            return rawParamType;
        }
        return genericParamType;
    }
    
    private static String join(String[] args)
    {
        return StringUtil.join(args, ", "); //$NON-NLS-1$
    }
    
    private static String[] toArray(List<String> list)
    {
        return list.toArray(new String[list.size()]);
    }
    
    private static String getProblemsComment(Set<String> errors)
    {
        StringBuilder sb = new StringBuilder();
        if (!errors.isEmpty()) {
            int idx = 1;
            sb.append("// WARNING - cannot record parameters:"); //$NON-NLS-1$
            for (String message : errors) {
                List<String> lines = StringUtil.splitLines(message);
                for (int i = 0; i < lines.size(); i++) {
                    if (i == 0) {
                        sb.append(fmt("\n// {0}. {1}", idx++, lines.get(i))); //$NON-NLS-1$
                    } else {
                        sb.append(fmt("\n// {0}", lines.get(i))); //$NON-NLS-1$
                    }
                }
            }
        }
        return sb.toString();
    }
    
    @SuppressWarnings("nls")
    private static final String CLASS_SIGNATURE_TEMPLATE = 
        "@RunWith(JUnitParamsRunner.class)\n" + 
        "public class {0}";



    
    private static final String RESULT = "result"; //$NON-NLS-1$
    private static final String EXPECTED = "expected"; //$NON-NLS-1$
    
}
