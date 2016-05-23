package com.github.sdarioo.testgen.recorder.values.mock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProxiesCache 
{
    // Instance -> Proxy Instance
    private ConcurrentMap<Object, Object> _cache = new ConcurrentHashMap<Object, Object>();
    
    
    public Object get(Object target)
    {
        return _cache.get(target);
    }
    
    public Object putIfAbsent(Object target, Object proxy)
    {
        Object otherProxy = _cache.putIfAbsent(target, proxy);
        return (otherProxy != null) ? otherProxy : proxy;
    }
}
