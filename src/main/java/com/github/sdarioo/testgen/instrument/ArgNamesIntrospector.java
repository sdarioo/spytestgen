/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.recorder.ArgNamesCache;


public class ArgNamesIntrospector 
    extends ClassVisitor
{
    private Type _type;
    
    public ArgNamesIntrospector() 
    {
        super(Opcodes.ASM5);
    }
    
    public ArgNamesIntrospector(ClassWriter writer) 
    {
        super(Opcodes.ASM5, writer);
    }

    @Override
    public void visit(int version, int access, String name,
            String signature, String superName, String[] interfaces) 
    {
        super.visit(version, access, name, signature, superName, interfaces);
        _type = Type.getObjectType(name);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) 
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        
        return new ArgNamesMethodVisitor(mv, _type, access, name, desc);
    }
    
    
    private static class ArgNamesMethodVisitor extends MethodVisitor
    {
        private final Type _type;
        private final Method _method;
        
        private final String[] _argNames;
        private final int[] _argIndexes;
        
        public ArgNamesMethodVisitor(MethodVisitor visitor,
                Type type, int access, String name, String desc) 
        {
            super(Opcodes.ASM5, visitor);
            
            _type = type;
            _method = new Method(name, desc);
            
            Type[] argTypes = _method.getArgumentTypes();
            boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
            
			_argNames = new String[argTypes.length];
			_argIndexes = new int[argTypes.length];
			int index = isStatic ? 0 : 1; 
			for (int i = 0; i < argTypes.length; i++) {
				_argIndexes[i] = index;
				index += argTypes[i].getSize(); 
			}
        }
        
        @Override
        public void visitLocalVariable(String name, String desc, String signature,
                Label start, Label end, int index) 
        {
            super.visitLocalVariable(name, desc, signature, start, end, index);

            for (int i = 0; i < _argIndexes.length; i++) {
            	if (_argIndexes[i] == index) {
            		_argNames[i] = name;
            		break;
            	}
            }
        }
        
        @Override
        public void visitEnd() 
        {
            super.visitEnd();
            
            ArgNamesCache.setArgNames(_type, _method, _argNames);
        }
    }
}