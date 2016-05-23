package com.github.sdarioo.testgen.instrument;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.RecordedClass;
import com.github.sdarioo.testgen.recorder.Recorder;
import com.github.sdarioo.testgen.recorder.values.mock.ProxyFactory;

public final class RecorderAPI
{
	private static ThreadLocal<ThreadLocalRecorder> threadLocalRecorders =
			new ThreadLocal<ThreadLocalRecorder>() {
		
		protected ThreadLocalRecorder initialValue() {
			String thread = Thread.currentThread().getName();
			Logger.info("New ThreadLocalRecorder [" + thread + ']'); //$NON-NLS-1$
			return new ThreadLocalRecorder();
		};
	};
	
	
	private RecorderAPI() {}
	
	public static void methodBegin(Method method, Object target, Object[] args)
	{
	    try {
	        threadLocalRecorders.get().methodBegin(method, target, args);
	    } catch (Throwable t) {
	        Logger.error(t.getMessage(), t);
	    }
	}
	
	public static void methodEnd()
	{
	    try {
	        threadLocalRecorders.get().methodEnd(true, null, null);
	    } catch (Throwable t) {
	        Logger.error(t.getMessage(), t);
	    }
	}
	
	public static void methodEndWithResult(Object object)
	{
	    try {
	        threadLocalRecorders.get().methodEnd(false, object, null);
	    } catch (Throwable t) {
	        Logger.error(t.getMessage(), t);
	    }
	}
	
	public static void methodEndWithException(Throwable throwable)
	{
	    try {
	        threadLocalRecorders.get().methodEnd(false, null, throwable);
	    } catch (Throwable t) {
	        Logger.error(t.getMessage(), t);
	    }
	}
	
    @SuppressWarnings("nls")
    public static java.lang.reflect.Method getMethod(Class<?> owner, String name, String descriptor)
    {
        try {
            Method[] declaredMethods = owner.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (name.equals(method.getName())) {
                    String desc = org.objectweb.asm.commons.Method.getMethod(method).getDescriptor();
                    if (descriptor.equals(desc)) {
                        return method;
                    }
                }
            }
        } catch (Throwable t) {
            Logger.error(t.getMessage(), t);
        }
        Logger.error("Null method for: " + owner.getName() + " name: " + name + " desc: " + descriptor);
        return null;
    }
	
    public static Object proxyArg(Method method, Object target, int argIndex, Object argValue)
    {
        java.lang.reflect.Type argType = method.getGenericParameterTypes()[argIndex];
        if (!ProxyFactory.canProxy(argType, argValue)) {
            return argValue;
        }
        RecordedClass recordedClass = Recorder.getDefault().getRecordedClass(method, target, true);
        
        // Don't proxy argument if recorded calls limit has been reached
        int maxCalls = Configuration.getDefault().getMaxCalls();
        if (recordedClass.getCalls(method).size() >= maxCalls) {
            return argValue;
        }
        
        Object argProxy = ProxyFactory.newProxy(argType, argValue, recordedClass.getProxiesCache());
        return (argProxy != null) ? argProxy : argValue;
    }
    
	private static class ThreadLocalRecorder
	{
		private final LinkedList<Call> _callQueue = new LinkedList<Call>();
		
		void methodBegin(Method method, Object target, Object[] args)
		{
			Call call = Call.newCall(method, target, args);
			_callQueue.addFirst(call);
		}
		
		void methodEnd(boolean isVoid, Object result, Throwable thr)
		{
			if (!_callQueue.isEmpty()) {
				Call call = _callQueue.removeFirst();
				if (isVoid) {
					call.end();
				} else if (thr != null) {
					call.endWithException(thr);
				} else {
					call.endWithResult(result);
				}
				Recorder.getDefault().record(call);
			}
		}
	}
	
	static final String TYPE_NAME = Type.getType(RecorderAPI.class).getInternalName();
}
