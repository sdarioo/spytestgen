/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen;

public final class Configuration 
{
    private int _maxCollectionSize = 20;
    private int _maxStringLength = 200;
    
    private boolean _backgroundGeneration = true;
    private boolean _alwaysGenerateForDeclaringClass = true;
    
    private int _maxCalls = 10;
    
    private static Configuration INSTANCE = new Configuration();
    
    public static Configuration getDefault()
    {
        return INSTANCE;
    }
    
    public int getMaxCalls()
    {
        return _maxCalls;
    }
    
    public int getMaxMockCalls()
    {
        return _maxCalls;
    }
    
    public int getMaxCollectionSize()
    {
        return _maxCollectionSize;
    }
    
    public int getMaxStringLength()
    {
        return _maxStringLength;
    }
    
    public boolean isBackgroundGenerationEnabled()
    {
        return _backgroundGeneration;
    }
    
    public boolean isAlwaysGenerateForDeclaringClass()
    {
        return _alwaysGenerateForDeclaringClass;
    }
    
    public boolean isMockingEnabled(String interfaceType)
    {
        if (interfaceType == null) {
            return false;
        }
        if (interfaceType.startsWith("java")) { //$NON-NLS-1$
            return false;
        }
        // TODO - config
        return true;
    }
}
