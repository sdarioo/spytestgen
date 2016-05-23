package com.github.sdarioo.testgen.recorder.values.mock;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.Call;

/**
 * Proxy invocation handler. 
 * Each proxy instance has separate instance of invocation handler.
 */
public class RecordingInvocationHandler
    implements InvocationHandler
{
    private final Class<?> _proxyType;
    private final Object _target;
    private final ProxiesCache _proxiesCache;
    
    private int _referencesCount = 0;
    
    private final Set<Call> _calls = new LinkedHashSet<Call>();
    private final Set<String> _errors = new HashSet<String>();
    
    private final Map<String, String> _attrs = new HashMap<String, String>();
    
    public RecordingInvocationHandler(Class<?> type, Object value, ProxiesCache proxiesCache)
    {
        _proxyType = type;
        _target = value;
        _proxiesCache = proxiesCache;
    }
    
    public Class<?> getType() 
    {
        return _proxyType;
    }
    
    public void incReferencesCount()
    {
        _referencesCount++;
    }
    
    public int getReferencesCount()
    {
        return _referencesCount;
    }
    
    public String getAttr(String key)
    {
        return _attrs.get(key);
    }
    
    public void setAttr(String key, String value)
    {
        _attrs.put(key, value);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
        throws Throwable 
    {
        if (args == null) {
            args = new Object[0];
        }
        
        Object result = null;
        try {
            result = method.invoke(_target, args);
        } catch (Throwable exception) {
            if (exception instanceof InvocationTargetException) {
                exception = ((InvocationTargetException)exception).getCause();
            }
            String warning = MessageFormat.format(
                "Exception thrown while invoking proxy method: {0} : {1}",  //$NON-NLS-1$ 
                    method.getName(), 
                    exception.toString());
            
            Logger.warn(warning);
            _errors.add(warning);
            throw exception;
        }
        return recordCall(method, args, result);
    }
    
    public boolean isSupported(Collection<String> errors)
    {
        errors.addAll(_errors);
        return _errors.isEmpty();
    }
    
    public boolean isMultipleCallsToSameMethod()
    {
        return getCalls().size() > getMethods().size();
    }

    public Set<Method> getMethods()
    {
        Set<Method> methods = new HashSet<Method>();
        for (Call call : getCalls()) {
            methods.add(call.getMethod());
        }
        return methods;
    }
    
    public Collection<Call> getCalls()
    {
        if (!_errors.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(_calls);
    }
    
    @Override
    public int hashCode() 
    {
        return _target.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof RecordingInvocationHandler)) {
            return false;
        }
        RecordingInvocationHandler other = (RecordingInvocationHandler)obj;
        return _target.equals(other._target);
    }
    
    private Object recordCall(Method method, Object[] args, Object result)
    {
        if (!_errors.isEmpty()) {
            return result;
        }
        Type returnType = method.getGenericReturnType();
        if (returnType == Void.TYPE) {
            Logger.warn("Ignoring VOID method call on proxy: " + method.toGenericString()); //$NON-NLS-1$
            return result;
        }
        
        Object callResult = result;
        Call call = Call.newCall(method, _target, args);
        
        if (ProxyFactory.canProxy(returnType, result)) {
            callResult = ProxyFactory.newProxy(returnType, result, _proxiesCache);
        }
        call.endWithResult(callResult);
        
        if (call.isSupported(_errors)) {
            // TODO - logger
            if (_calls.size() <= Configuration.getDefault().getMaxMockCalls()) {
                _calls.add(call);
                result = callResult;
            } else {
                _errors.add("Mock calls limit has been reached: " + _calls.size()); //$NON-NLS-1$
            }
        }
        return result;
    }
    
}
