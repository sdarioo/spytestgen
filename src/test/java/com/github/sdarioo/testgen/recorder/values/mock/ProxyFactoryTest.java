/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values.mock;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.recorder.values.mock.ProxyFactory;

public class ProxyFactoryTest
{
    @Test
    public void testCanProxy()
    {
        assertFalse(ProxyFactory.canProxy(Object.class, new Object()));
        assertFalse(ProxyFactory.canProxy(IBase.class, null));
        assertFalse(ProxyFactory.canProxy(IPrivate.class, new IPrivate(){}));
        assertFalse(ProxyFactory.canProxy(java.util.List.class, new java.util.ArrayList()));
        
        assertTrue(ProxyFactory.canProxy(IBase.class, new IBase(){}));
        assertTrue(ProxyFactory.canProxy(IDerived.class, new IBase(){}));
    }
    
    @Test
    public void testNewProxy()
    {
        Object obj = new IDerived() {};
        Object proxy = ProxyFactory.newProxy(IBase.class, obj);
        assertNotNull(proxy);
        assertTrue(ProxyFactory.isProxy(proxy));
        
        assertTrue(proxy instanceof IBase);
        assertTrue(proxy instanceof IDerived);
    }
    
    private static interface IPrivate {}
    public static interface IBase {}
    public static interface IDerived extends IBase {}
}
