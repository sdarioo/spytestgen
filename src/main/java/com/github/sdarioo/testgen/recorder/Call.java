/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.math.NumberUtils;

import com.github.sdarioo.testgen.recorder.values.IValue;
import com.github.sdarioo.testgen.recorder.values.ValuesFactory;

/**
 * Represents recorded method call with argument values and result or exception info.
 */
public class Call implements Comparable<Call> 
{
    private final Method _method;
    private final Class<?> _targetClass;
    
    private final long _callId;
    private final List<IValue> _args;
    
    private IValue _result;
    private ExceptionInfo _exception;
    
    private static final AtomicLong _callIdGenerator = new AtomicLong(0);
    
    public static Call newCall(Method method, Object... args)
    {
        return newCall(method, null, args);
    }
    
    public static Call newCall(Method method, Object target, Object[] args)
    {
        List<IValue> params = new ArrayList<IValue>(args.length);
        
        for (int i = 0; i < args.length; i++) {
            params.add(ValuesFactory.newValue(args[i]));
        }
        
        return new Call(method, target, params);
    }
    
    public static Call newCall(MethodRef ref, Object... args)
    {
        return newCall(ref, null, args);
    }
    
    public static Call newCall(MethodRef ref, Object target, Object[] args)
    {
        Method method = ref.getClass().getEnclosingMethod();
        if (method == null) {
            throw new IllegalArgumentException("MethodRef must be anonymous class within a tested method"); //$NON-NLS-1$
        }
        return newCall(method, target, args);
    }
    
    private Call(Method method, Object target, List<IValue> args)
    {
        _method = method;
        _targetClass = (target != null) ? target.getClass() : null;
        _args = args;
        _callId = _callIdGenerator.incrementAndGet();
    }
    
    public void end()
    {
        _result = IValue.VOID;
    }
    
    public void endWithResult(Object result)
    {
        _result = ValuesFactory.newValue(result);
    }
    
    public void endWithException(Throwable thr)
    {
        _exception = new ExceptionInfo(thr);
    }
        
    public Method getMethod()
    {
        return _method;
    }
    
    public boolean isFinished()
    {
        return (_result != null) || (_exception != null);
    }
    
    public boolean isStatic()
    {
        return Modifier.isStatic(_method.getModifiers());
    }
    
    public boolean isSupported(Collection<String> errors)
    {
        boolean bResult = true;
        Type[] paramTypes = _method.getGenericParameterTypes();
        for (int i = 0; i < _args.size(); i++) {
            bResult &= _args.get(i).isSupported(paramTypes[i], errors);
        }
        if (_result != null) {
            bResult &= _result.isSupported(_method.getGenericReturnType(), errors);
        }
        return bResult;
    }
    
    public Class<?> getTargetClass()
    {
        return _targetClass;
    }
    
    public List<IValue> args()
    {
        return Collections.unmodifiableList(_args);
    }

    public IValue getResult()
    {
        return _result;
    }
    
    public ExceptionInfo getExceptionInfo()
    {
        return _exception;
    }
    
    @Override
    public int compareTo(Call other) 
    {
        return NumberUtils.compare(_callId, other._callId);
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(_method, _args, _result, _exception);
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Call other = (Call) obj;
        if (!_method.equals(other._method)) {
            return false;
        }
        if (!_args.equals(other._args)) {
            return false;
        }
        if (!Objects.equals(_targetClass, other._targetClass)) {
            return false;
        }
        if (!Objects.equals(_exception, other._exception)) {
            return false;
        }
        if (!Objects.equals(_result, other._result)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() 
    {
        return _method.toString();
    }
    
    public static abstract class MethodRef {}
    
}
