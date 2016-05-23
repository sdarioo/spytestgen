package com.github.sdarioo.testgen.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.sdarioo.testgen.instrument.ArgNamesIntrospector;
import com.github.sdarioo.testgen.instrument.TestGenClassAdapter;
import com.github.sdarioo.testgen.logging.Logger;

public class Transformer
    implements ClassFileTransformer
{
    private final String _className;
    private final String _methodName;
    
    public Transformer(String className, String methodName)
    {
        _className = className.replace('.', '/');
        _methodName = methodName;
    }
    
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException
    {
        if (!shouldTransform(className)) {
            return classfileBuffer;
        }
        Logger.info("Transforming class: " + className); //$NON-NLS-1$
        return transform(classfileBuffer, className, _methodName);
    }

    static byte[] transform(byte[] classfileBuffer, String className, String methodName)
    {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        
        ArgNamesIntrospector argsIntrospector = new ArgNamesIntrospector(writer);
        TestGenClassAdapter adapter = new TestGenClassAdapter(argsIntrospector, methodName);
        
        try {
            // ClassReader.accept() calls that receive any subclass of LocalVariableSorter need EXPAND_FRAMES flag
            reader.accept(adapter, ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        } catch (Throwable thr) {
            Logger.error("Error transforming class: " + className + '\n' + thr.toString()); //$NON-NLS-1$
        }
        return classfileBuffer;
    }
    
    private boolean shouldTransform(String className)
    {
        if (className.equals(_className)) {
            return true;
        }

        if (className.endsWith('/' + _className)) {
            return true;
        }
        return false;
    }
}
