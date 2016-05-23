/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.*;

/**
 * Base ClassVisitor that should visit non-private classes and non-private methods.
 */
public class AccessibleClassVisitor
    extends ClassVisitor
{
    protected Type _type;
    protected boolean _isClassAccessible; // non-private
    protected boolean _isClassVisitable;  // non-synthetic etc.. 
    
    protected AccessibleClassVisitor() 
    {
        super(Opcodes.ASM5);
    }
    
    protected AccessibleClassVisitor(ClassVisitor cw) 
    {
        super(Opcodes.ASM5, cw);
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) 
    {
        super.visit(version, access, name, signature, superName, interfaces);
        
        _type = Type.getObjectType(name);
        _isClassAccessible = isClassAccessible(access);
        _isClassVisitable = isClassVisitable(access);
    }
    
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) 
    {
        super.visitInnerClass(name, outerName, innerName, access);
        
        Type type = Type.getObjectType(name);
        if (type.equals(_type)) {
            _isClassAccessible = isClassAccessible(access);
            _isClassVisitable = isClassVisitable(access);
        }
    }
    
    protected boolean isClassAccessible(int access)
    {
        int[] excludeFlags = { Opcodes.ACC_PRIVATE };
        if (InstrumentUtil.isFlagSet(access, excludeFlags)) {
            return false;
        }
        return true;
    }
    
    protected boolean isMethodAccessible(String methodName, int access)
    {
        if (!_isClassAccessible) {
            return false;
        }
        int[] excludeFlags = { Opcodes.ACC_PRIVATE };
        if (InstrumentUtil.isFlagSet(access, excludeFlags)) {
            return false;
        }
        return true;
    }

    protected boolean isClassVisitable(int access)
    {
        int[] excludeFlags = {
                Opcodes.ACC_INTERFACE,
                Opcodes.ACC_SYNTHETIC,
                Opcodes.ACC_ANNOTATION,
                Opcodes.ACC_ENUM
            };
        if (InstrumentUtil.isFlagSet(access, excludeFlags)) {
            return false;
        }
        return true;
    }
    
    protected boolean isMethodVisitable(String methodName, int access)
    {
        if (!_isClassVisitable) {
            return false;
        }
        int[] excludeFlags = {
            Opcodes.ACC_BRIDGE,
            Opcodes.ACC_NATIVE,
            Opcodes.ACC_ABSTRACT,
            Opcodes.ACC_SYNTHETIC
        };
        if (InstrumentUtil.isFlagSet(access, excludeFlags)) {
            return false;
        }
        return true;
    }

}
