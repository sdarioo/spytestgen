/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.values.PropertiesValue;

public class PropertiesValueTest 
{
    @SuppressWarnings("nls")
    @Test
    public void testToSourceCode()
    {
        Properties p = new Properties();
        p.setProperty("key1", "value1");
        
        PropertiesValue param = new PropertiesValue(p);
        
        String text = param.toSouceCode(Properties.class, new TestSuiteBuilder());
        
        assertEquals("asProps(asPair(\"key1\", \"value1\"))", text);
    }
}
