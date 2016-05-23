/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.lang3.text.StrTokenizer;
import org.junit.Test;

public class MethodBuilderTest 
{
    @Test
    public void buildSignature() throws Exception
    {
        Method m = getClass().getDeclaredMethod("sort", List.class);
        
        MethodBuilder builder = new MethodBuilder(new TestSuiteBuilder());
        builder.name("sort").
            modifier(Modifier.STATIC).
            modifier(Modifier.PRIVATE).returnType(m.getGenericReturnType()).
            arg(m.getGenericParameterTypes()[0], "list").
            typeParams(m.getTypeParameters()).
            exception("java.lang.Exception");
        
        String text = builder.build();
        String[] lines = new StrTokenizer(text, '\n').getTokenArray();
        
        assertEquals(2, lines.length);
        assertEquals("private static <T extends Comparable<T>> void sort(List<T> list) throws Exception {", lines[0]);
        assertEquals("}", lines[1]);
    }
    
    
    private static <T extends Comparable<T>> void sort(List<T> list) {}
}
