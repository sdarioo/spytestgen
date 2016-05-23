/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

public interface IUniqueNamesProvider 
{
    String newUniqueMethodName(String methodName);
    
    String newUniqueFieldName(String fieldName);
    
    String newUniqueFileName(String fileName);
}
