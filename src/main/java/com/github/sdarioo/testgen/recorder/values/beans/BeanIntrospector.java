package com.github.sdarioo.testgen.recorder.values.beans;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.instrument.AccessibleClassVisitor;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;

public class BeanIntrospector
    extends AccessibleClassVisitor
{
    private final BeanBuilder _builder;
    
    public BeanIntrospector(Class<?> clazz) 
    {
        _builder = new BeanBuilder(clazz);
    }
    
    public Bean getBean()
    {
        _builder.setAccessible(_isClassAccessible);
        return _isClassVisitable ? _builder.build() : null;
    }
    
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) 
    {
        if (!InstrumentUtil.isFlagSet(access, Opcodes.ACC_STATIC, Opcodes.ACC_SYNTHETIC)) {
            _builder.addField(name);
        }
        return super.visitField(access, name, desc, signature, value);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        
        if (!isMethodVisitable(name, access) || !isMethodAccessible(name, access)) {
            return mv;
        }
        
        Method method = new Method(name, desc);
        if (isConstructor(method) || isGetter(method) || isSetter(method)) {
            mv = new BeanMethodVisitor(_builder, method);
        }
        return mv;
    }
    
    
    private static boolean isConstructor(Method method)
    {
        return "<init>".equals(method.getName()); //$NON-NLS-1$
    }
    
    private static boolean isGetter(Method method)
    {
        if (!method.getName().startsWith("get")) { //$NON-NLS-1$
            return false;
        }
        if (method.getArgumentTypes().length != 0) {
            return false;
        }
        if (Type.VOID_TYPE.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }
    
    private static boolean isSetter(Method method)
    {
        if (!method.getName().startsWith("set")) { //$NON-NLS-1$
            return false;
        }
        if (method.getArgumentTypes().length != 1) {
            return false;
        }
        if (!Type.VOID_TYPE.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }
    
    private static boolean in(int code, int... codes)
    {
        for (int c : codes) {
            if (code == c) {
                return true;
            }
        }
        return false;
    }
    
    private static class BeanMethodVisitor 
        extends MethodVisitor
    {
        private final BeanBuilder _builder;
        private final Method _method;
        
        private final List<GetField> _getInsts;
        private final List<SetField> _setInsts;
        
        private GetField _get;
        private SetField _set;
        
        BeanMethodVisitor(BeanBuilder bean, Method method)
        {
            super(Opcodes.ASM5);
            
            _builder = bean;
            _method = method;
            
            _getInsts = new ArrayList<GetField>();
            _setInsts = new ArrayList<SetField>();
        }
        
        @Override
        public void visitLocalVariable(String name, String desc, String signature,
                Label start, Label end, int index) 
        {
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }
        
        @Override
        public void visitVarInsn(int opcode, int var)
        {
            super.visitVarInsn(opcode, var);
            
            // Typical setter instruction sequence
            // ALOAD 0
            // ILOAD 1
            // PUTFIELD <class>.<field> : I
            
            if ((var == 0) && (Opcodes.ALOAD == opcode)) {
                _set = new SetField();
                _get = new GetField();
                
            } else if ((var > 0) && in(opcode, Opcodes.ILOAD, Opcodes.FLOAD, Opcodes.FLOAD, Opcodes.ALOAD)) {
                if (_set != null) {
                    _set.arg = var;
                }
            }
            
            // Typical getter instruction sequence
            // ALOAD 0: this
            // GETFIELD App$Bean.name : String
            // ARETURN
        }
    
        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc)
        {
            super.visitFieldInsn(opcode, owner, name, desc);
            
            if (Opcodes.PUTFIELD == opcode) {
                if ((_set != null) && (_set.arg > 0)) {
                    _set.name = name;
                    _setInsts.add(_set);
                }
                _set = null;
            } else if (Opcodes.GETFIELD == opcode) {
                if (_get != null) {
                    _get.name = name;
                }
            }
        }
        
        @Override
        public void visitInsn(int opcode) 
        {
            super.visitInsn(opcode);
            if (in(opcode, Opcodes.IRETURN, Opcodes.LRETURN, Opcodes.FRETURN, 
                    Opcodes.DRETURN, Opcodes.ARETURN, Opcodes.RETURN)) 
            {
                if ((_get != null) && (_get.name != null)) {
                    _getInsts.add(_get);
                }
            }
            _get = null;
            _set = null;
        }
        
        @Override
        public void visitIincInsn(int var, int increment) 
        {
            super.visitIincInsn(var, increment);
            _get = null;
            _set = null;
        }
        @Override
        public void visitIntInsn(int opcode, int operand) 
        {
            super.visitIntInsn(opcode, operand);
            _get = null;
            _set = null;
        }
        @Override
        public void visitJumpInsn(int opcode, Label label) 
        {
            super.visitJumpInsn(opcode, label);
            _get = null;
            _set = null;
        }
        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) 
        {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            _get = null;
            _set = null;
        }
        @Override
        public void visitLabel(Label label) 
        {
            super.visitLabel(label);
            _get = null;
            _set = null;
        }
        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) 
        {
            super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
            _get = null;
            _set = null;
        }
        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) 
        {
            super.visitLookupSwitchInsn(dflt, keys, labels);
            _get = null;
            _set = null;
        }
        @Override
        public void visitTypeInsn(int opcode, String type) 
        {
            super.visitTypeInsn(opcode, type);
            _get = null;
            _set = null;
        }
        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) 
        {
            super.visitTableSwitchInsn(min, max, dflt, labels);
            _get = null;
            _set = null;
        }
        
        
        @Override
        public void visitEnd() 
        {
            super.visitEnd();
            
            if (isConstructor(_method) && _getInsts.isEmpty() && 
                    (_setInsts.size() == _method.getArgumentTypes().length)) 
            {
                Collections.sort(_setInsts);
                List<Field> fields = verifyAndGetFields(_builder, _setInsts);
                if (fields != null) {
                    _builder.addConstructor(_method, fields);
                }
            }
            
            if (isGetter(_method) && (_getInsts.size() == 1) && _setInsts.isEmpty()) {
                GetField get = _getInsts.get(0);
                Field field = _builder.getField(get.name);
                if (field != null) {
                    _builder.addGetter(field, _method);
                }
            }
            
            if (isSetter(_method) && (_setInsts.size() == 1) && _getInsts.isEmpty()) {
                SetField set = _setInsts.get(0);
                Field field = _builder.getField(set.name);
                if (field != null) {
                    _builder.addSetter(field, _method);
                }
            }
        }
        
        private static List<Field> verifyAndGetFields(BeanBuilder builder, List<SetField> setInsts)
        {
            List<Field> result = new ArrayList<Field>();
            
            int index = 1;
            for (SetField set : setInsts) {
                Field field = builder.getField(set.name);
                if (field == null) {
                    return null;
                }
                if (set.arg != index) {
                    return null;
                }
                result.add(field);
                index++;
            }
            return result;
        }
        
        private static class GetField
        {
            String name;
        }
        
        private static class SetField implements Comparable<SetField>
        {
            int arg; // 1-indexed
            String name;
            
            @Override
            public int compareTo(SetField set) 
            {
                return NumberUtils.compare(arg, set.arg);
            }
        }
    }

}
