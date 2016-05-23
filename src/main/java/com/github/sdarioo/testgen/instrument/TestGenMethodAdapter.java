/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class TestGenMethodAdapter 
    extends AdviceAdapter
{
    private final Type _owner;
    private final Method _method;
    private final boolean _isStatic;
    
    private final Label startFinallyLabel = new Label();

    public TestGenMethodAdapter(Type owner, MethodVisitor mv, int access, String name, String desc) 
    {
        super(Opcodes.ASM5, mv, access, name, desc);
        
        _isStatic = isStatic(access);
        _owner = owner;
        _method = new Method(name, desc);
    }
    
    @Override
    public void visitMaxs(int maxStack, int maxLocals)
    {
        Label endFinallyLabel = new Label();
        super.visitTryCatchBlock(startFinallyLabel, endFinallyLabel, endFinallyLabel, null);
        super.visitLabel(endFinallyLabel);
        onFinally(ATHROW);
        super.visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }
    
    @SuppressWarnings("nls")
    @Override
    protected void onMethodEnter()
    {
    	super.visitLabel(startFinallyLabel);

    	proxyArguments();
    	
    	int argIndex = generateArgumentsArray();
    	
    	getMethod();
    	
        if (_isStatic) {
        	super.visitInsn(ACONST_NULL);
        } else {
        	super.visitVarInsn(ALOAD, 0);
        }
        super.visitVarInsn(ALOAD, argIndex);    
   
        super.visitMethodInsn(
                INVOKESTATIC, RecorderAPI.TYPE_NAME, "methodBegin",
                "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)V", false);
    }
    
    @Override
    protected void onMethodExit(int opcode)
    {
        if (opcode != ATHROW) {
            onFinally(opcode);
        }
    }
    
    @SuppressWarnings("nls")
    private void onFinally(int opcode)
    {
        if (opcode == ATHROW) {
            super.visitInsn(DUP);
            super.visitMethodInsn(
                    INVOKESTATIC, RecorderAPI.TYPE_NAME, "methodEndWithException", "(Ljava/lang/Throwable;)V", false);
            
        } else if (opcode == RETURN) {
            super.visitMethodInsn(
                    INVOKESTATIC, RecorderAPI.TYPE_NAME, "methodEnd", "()V", false);
            
        } else {
            if (opcode == LRETURN || opcode == DRETURN) {
                super.visitInsn(DUP2);
            } else {
                super.visitInsn(DUP);
            }
            // TODO - valueOf?
            box(Type.getReturnType(methodDesc));

            super.visitMethodInsn(
                    INVOKESTATIC, RecorderAPI.TYPE_NAME, "methodEndWithResult", "(Ljava/lang/Object;)V", false);
        }
    }
    
    // [] -> [java.lang.reflect.Method]
    @SuppressWarnings("nls")
    private void getMethod()
    {
        mv.visitLdcInsn(_owner);
        mv.visitLdcInsn(_method.getName());
        mv.visitLdcInsn(_method.getDescriptor());
        mv.visitMethodInsn(INVOKESTATIC, RecorderAPI.TYPE_NAME, "getMethod", 
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/reflect/Method;", false);
    }
  
    @SuppressWarnings("nls")
    private int generateArgumentsArray()
    {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);

        mv.visitIntInsn(BIPUSH, argumentTypes.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

        int argIndex = _isStatic ? 0 : 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];

            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitVarInsn(argumentType.getOpcode(ILOAD), argIndex);

            boxIfNeeded(argumentType);

            mv.visitInsn(AASTORE);
            argIndex += argumentType.getSize();
        }

        mv.visitVarInsn(ASTORE, argIndex);
        return argIndex;
    }
    
    @SuppressWarnings("nls")
    private void proxyArguments()
    {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int argIndex = _isStatic ? 0 : 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            if (argumentType.getSort() == Type.OBJECT) {
                
                getMethod();
                if (_isStatic) {
                    mv.visitInsn(ACONST_NULL);
                } else {
                    mv.visitVarInsn(ALOAD, 0);
                }
                mv.visitLdcInsn(i);
                mv.visitVarInsn(ALOAD, argIndex);
                
                mv.visitMethodInsn(INVOKESTATIC, 
                        RecorderAPI.TYPE_NAME, "proxyArg", "(Ljava/lang/reflect/Method;Ljava/lang/Object;ILjava/lang/Object;)Ljava/lang/Object;", false);
                
                mv.visitTypeInsn(CHECKCAST, argumentType.getInternalName());
                mv.visitVarInsn(ASTORE, argIndex);
            }
            argIndex += argumentType.getSize();
        }
    }
    
    
    @SuppressWarnings("nls")
    private void boxIfNeeded(Type type)
    {
        switch (type.getSort()) {
            case Type.BYTE:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Byte",
                        "valueOf",
                        "(B)Ljava/lang/Byte;",
                        false
                );
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Boolean",
                        "valueOf",
                        "(Z)Ljava/lang/Boolean;",
                        false
                );
                break;
            case Type.SHORT:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Short",
                        "valueOf",
                        "(S)Ljava/lang/Short;",
                        false
                );
                break;
            case Type.CHAR:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Character",
                        "valueOf",
                        "(C)Ljava/lang/Character;",
                        false
                );
                break;
            case Type.INT:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false
                );
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Float",
                        "valueOf",
                        "(F)Ljava/lang/Float;",
                        false
                );
                break;
            case Type.LONG:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Long",
                        "valueOf",
                        "(J)Ljava/lang/Long;",
                        false
                );
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Double",
                        "valueOf",
                        "(D)Ljava/lang/Double;",
                        false
                );
                break;
        }
    }
    
    private static boolean isStatic(int access)
    {
        return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC; 
    }
}
