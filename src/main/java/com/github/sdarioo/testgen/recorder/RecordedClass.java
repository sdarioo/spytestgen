/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.values.IAggregateValue;
import com.github.sdarioo.testgen.recorder.values.IValue;
import com.github.sdarioo.testgen.recorder.values.mock.MockValue;
import com.github.sdarioo.testgen.recorder.values.mock.ProxiesCache;

/**
 * Stores recorded calls for single tested class. 
 */
public final class RecordedClass 
{
    private final Class<?> _clazz;
    private AtomicLong _timestamp = new AtomicLong(0L);
    
    private final Map<Method, Set<Call>> _calls = new LinkedHashMap<Method, Set<Call>>();
    private final Map<Method, Set<Call>> _unsupportedCalls = new LinkedHashMap<Method, Set<Call>>();

    // Proxies should be shared withing single test class but not across multiple
    // test classes (shared mock will have class field scope)
    private final ProxiesCache _proxiesCache = new ProxiesCache();
    
    RecordedClass(Class<?> clazz)
    {
        _clazz = clazz;
    }
    
    public Class<?> getRecordedClass()
    {
        return _clazz;
    }
    
    /**
     * @return last record timestamp, 0L if nothing has been recorded yet.
     */
    public long getTimestamp()
    {
        return _timestamp.get();
    }
    
    /**
     * @return collection of recorded methods
     */
    public Collection<Method> getMethods()
    {
        Set<Method> result = new LinkedHashSet<Method>();
        synchronized (_calls) {
            result.addAll(_calls.keySet());
        }
        synchronized (_unsupportedCalls) {
            result.addAll(_unsupportedCalls.keySet());
        }
        return result;
    }
    
    /**
     * @param method recorded methods
     * @return list of recorded calls for this method, empty list if no calls has been recorded
     */
    public List<Call> getCalls(Method method)
    {
        int maxCalls = Configuration.getDefault().getMaxCalls();
        
        // First add valid calls. If number of valid calls is less than max number of calls
        // then return unsupported calls (info about it can be added to generated test suite).
        List<Call> result = new ArrayList<Call>();
        
        collectCalls(_calls, method, result);
        if (result.size() < maxCalls) {
            collectCalls(_unsupportedCalls, method, result);
        }
        
        Collections.sort(result);
        return result;
    }
    
    public ProxiesCache getProxiesCache()
    {
        return _proxiesCache;
    }
    
    boolean record(Call call)
    {
        if (!call.isFinished()) {
            Logger.error("Cannot record call without return value or thrown exception: " + call.getMethod().toString()); //$NON-NLS-1$
            return false;
        }
        Method method = call.getMethod();
        if (method == null) {
            Logger.error("Cannot record call without java.lang.reflect.Method object."); //$NON-NLS-1$
            return false;
        }
        if (!call.isStatic() && (call.getTargetClass() == null)) {
            Logger.error("Missing target class for non-static method."); //$NON-NLS-1$
            return false;
        }
        if (call.args().size() != call.getMethod().getParameterTypes().length) {
            Logger.error(MessageFormat.format("Recorded call args count {0} is different that method parameters count {1}",  //$NON-NLS-1$
                    call.args().size(), call.getMethod().getParameterTypes().length));
            return false;
        }
        
        int maxCalls = Configuration.getDefault().getMaxCalls();
        if (count(method, _calls) >= maxCalls) {
            return false;
        }
        
        if (call.isSupported(new HashSet<String>())) {
            boolean bRecorded = recordCall(_calls, call);
            if (bRecorded) {
                incMockReferencesCount(call);
            }
            return bRecorded;
        } else {
            return recordCall(_unsupportedCalls, call);
        }
    }

    private boolean recordCall(Map<Method, Set<Call>> calls, Call call)
    {
        Method method = call.getMethod();
        int maxCalls = Configuration.getDefault().getMaxCalls();
        
        synchronized (calls) {
            Set<Call> methodCalls = calls.get(method);
            if (methodCalls == null) {
                methodCalls = new HashSet<Call>();
                calls.put(method, methodCalls);
            }
            if (methodCalls.size() >= maxCalls) {
                return false;
            }
            if (methodCalls.add(call)) {
                _timestamp.set(System.currentTimeMillis());
                return true;
            }
            return false;
        }
    }
    
    private static void collectCalls(Map<Method, Set<Call>> calls, Method method, List<Call> result)
    {
        synchronized (calls) {
            for (Map.Entry<Method, Set<Call>> entry : calls.entrySet()) {
                if (entry.getKey().equals(method)) {
                    result.addAll(entry.getValue());
                }
            }
        }
    }
    
    private static int count(Method method, Map<Method, Set<Call>> calls)
    {
        synchronized (calls) {
            Set<Call> methodCalls = calls.get(method);
            return (methodCalls != null) ? methodCalls.size() : 0;
        }
    }
    
    /**
     * Mark mocks references count because same mock used more than once will have
     * different source code generated.
     */
    private static void incMockReferencesCount(Call call)
    {
        Set<Integer> visited = new HashSet<Integer>();
        incMockReferencesCount(call.getResult(), visited);
        for (IValue arg : call.args()) {
            incMockReferencesCount(arg, visited);
        }
    }
    
    private static void incMockReferencesCount(IValue value, Set<Integer> visited)
    {
        if (value == null) {
            return;
        }
        if (value instanceof MockValue) {
            ((MockValue)value).getHandler().incReferencesCount();
        }
        
        int hash = System.identityHashCode(value);
        if ((value instanceof IAggregateValue) && !visited.contains(hash)) {
            visited.add(hash);
            for (IValue child : ((IAggregateValue)value).getComponents()) {
                incMockReferencesCount(child, visited);
            }
        }
    }
    
    @Override
    public int hashCode() 
    {
        return _clazz.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof RecordedClass)) {
            return false;
        }
        RecordedClass other = (RecordedClass)obj;
        return _clazz.equals(other._clazz);
    }
}
