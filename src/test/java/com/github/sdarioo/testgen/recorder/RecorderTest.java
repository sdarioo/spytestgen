/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.instrument.RecorderAPI;
import com.github.sdarioo.testgen.recorder.Call.MethodRef;

public class RecorderTest 
{
 
    @SuppressWarnings("nls")
    @Test
    public void testMethodHolderPerformance()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Call call = Call.newCall(new MethodRef() {});
            assertNotNull(call);
        }
        long time = System.currentTimeMillis() - start;
        assertTrue("Actual time: " + time, time < 200);
    }
    
    @Test
    public void testGetValidCallsFirst()
    {
        Method m = RecorderAPI.getMethod(RecorderTest.class, "testCall", "(Ljava/lang/Object;)I");
        assertNotNull(m);
        
        int max = Configuration.getDefault().getMaxCalls();
        Recorder r = Recorder.newRecorder("test");
        for (int i = 0; i < max; i++) {
            Call c = Call.newCall(m, this, new Object[]{"param"+i});
            c.end();
            r.record(c);
        }
        // This call will not be returned by getCalls because there is already max valid calls
        Call c = Call.newCall(m, this, new Object[]{new Object()});
        c.end();
        r.record(c);
        
        RecordedClass recordedClass = r.getRecordedClass(m, this, false);
        assertNotNull(recordedClass);
        
        List<Call> calls = recordedClass.getCalls(m);
        assertEquals(max, calls.size());
        assertFalse(calls.contains(c));
    }
    
    @Test
    public void testGetUnsupportedCalls()
    {
        Method m = RecorderAPI.getMethod(RecorderTest.class, "testCall", "(Ljava/lang/Object;)I");
        assertNotNull(m);
        
        Recorder r = Recorder.newRecorder("test");
        
        Call c = Call.newCall(m, this, new Object[]{"param"});
        c.end();
        r.record(c);
        
        // This call will be returned because size of supported calls is less than max
        c = Call.newCall(m, this, new Object[]{new Object()});
        c.end();
        r.record(c);
        
        RecordedClass recordedClass = r.getRecordedClass(m, this, false);
        assertNotNull(recordedClass);
        
        List<Call> calls = recordedClass.getCalls(m);
        assertEquals(2, calls.size());
        assertTrue(calls.contains(c));
    }
    
    
    public int testCall(Object o)
    {
        return 0;
    }
    
}
