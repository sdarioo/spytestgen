/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.io.IOException;
import java.util.List;

public class MockApp 
{
    public static void main(String[] args) 
    {
        IFile file = new IFile() {
            public String read() {
                return "text";
            }
            public List<? extends List> getLines() {
                return null;
            }
        };
        IContext context = new IContext() {
            public int getLength(IFile file) {
                return 666;
            }
        };
        foo(file, context);
        System.out.println("OK");
    }
    
    public static int foo(IFile file, IContext context)
    {
        try {file.read();}catch(Throwable t) {}
        file.getLines();
        return context.getLength(file);
    }
    
    public static interface IFile
    {
        String read() throws IOException;
        
        List<? extends List> getLines();
    }
    
    public static interface IContext
    {
        int getLength(IFile file);
    }
    
    
}
