/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.io.File;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.recorder.ArgNamesCache;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.RecordedClass;

public abstract class AbstractTestSuiteGenerator 
    implements ITestSuiteGenerator
{
    private File _locationDir;
    
    protected abstract void initTestSuite(Class<?> targetClass, 
            TestSuiteBuilder builder);
    
    protected abstract void addTestCases(Class<?> targetClass, 
            Method method, 
            List<Call> callsWithResult, 
            TestSuiteBuilder builder);
    
    protected abstract void addTestCasesForExceptions(Class<?> targetClass,
            Method method, 
            List<Call> callsWithException, 
            TestSuiteBuilder builder);
    
    
    /**
     * @see com.github.sdarioo.testgen.generator.ITestSuiteGenerator#setLocationDir(java.io.File)
     */
    public void setLocationDir(File locationDir)
    {
        _locationDir = locationDir;
    }
    
    /**
     * @see com.github.sdarioo.testgen.generator.ITestSuiteGenerator#generate(com.github.sdarioo.testgen.recorder.RecordedClass)
     */
    @Override
    public TestClass generate(RecordedClass recordedClass)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder(false, _locationDir);
        Class<?> clazz = recordedClass.getRecordedClass();
        initTestSuite(clazz, builder);
        
        for (Method method : recordedClass.getMethods()) {
            List<Call> methodCalls = recordedClass.getCalls(method);
            List<Call> callsWithResult = getCallsWithResult(methodCalls);
            
            if (!callsWithResult.isEmpty()) {
                addTestCases(clazz, method, callsWithResult, builder);
            }
            List<Call> callsWithExc = getCallsWithException(methodCalls);
            if (!callsWithExc.isEmpty()) {
                addTestCasesForExceptions(clazz, method, callsWithExc, builder);
            }
        }
        
        return builder.buildTestClass();
    }
    
    protected String getTestCaseName(Method method, IUniqueNamesProvider namesProvider)
    {
        String name = method.getName();
        String defaultName = "test" + name.substring(0, 1).toUpperCase() + name.substring(1); //$NON-NLS-1$
        return namesProvider.newUniqueMethodName(defaultName);
    }
    
    protected String getTestClassName(Class<?> clazz, IUniqueNamesProvider namesProvider)
    {
        String name = ClassUtils.getShortCanonicalName(clazz);
        String defaultName = name + "Test"; //$NON-NLS-1$
        return namesProvider.newUniqueFileName(defaultName);
    }
    
    protected String[] getParameterNames(Method method)
    {
        return ArgNamesCache.getArgNames(method);
    }
    
    protected static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
    
    private static List<Call> getCallsWithException(List<Call> calls)
    {
        List<Call> result = new ArrayList<Call>();
        for (Call call : calls) {
            if (call.getExceptionInfo() != null) {
                result.add(call);
            }
        }
        return result;
    }
    
    private static List<Call> getCallsWithResult(List<Call> calls)
    {
        List<Call> result = new ArrayList<Call>();
        for (Call call : calls) {
            if (call.getResult() != null) {
                result.add(call);
            }
        }
        return result;
    }
}
