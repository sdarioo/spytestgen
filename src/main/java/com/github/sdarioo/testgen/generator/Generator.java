/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.sdarioo.testgen.generator.impl.JUnitParamsGenerator;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.RecordedClass;
import com.github.sdarioo.testgen.recorder.Recorder;
import com.github.sdarioo.testgen.util.TestLocationUtil;

// ThreadSafe
public final class Generator
{
    private final Recorder _recorder;
    private final ConcurrentMap<Class<?>, Long> _lastWriteTime = new ConcurrentHashMap<>();
    
    private static final Generator DEFAULT = new Generator(Recorder.getDefault());

    private Generator(Recorder recorder) 
    {
        _recorder = recorder;
    }
    
    public static synchronized Generator getDefault()
    {
        return DEFAULT;
    }

    public void generateTests()
    {
        try {
            internalGenerateTests();
        } catch (Throwable e) {
            Logger.error("Error while generating tests.", e); //$NON-NLS-1$
        }
    }
    
    @SuppressWarnings("nls")
    private void internalGenerateTests()
        throws IOException
    {
        Collection<RecordedClass> classes = _recorder.getRecordedClasses();
        for (RecordedClass recordedClass : classes) {
            
            Class<?> clazz = recordedClass.getRecordedClass();
            File locationDir = TestLocationUtil.getTestLocation(clazz);
            if (locationDir == null) {
                Logger.error("Null test location for class: " + clazz.getName());
                continue;
            }
            long timestamp = recordedClass.getTimestamp();
            if ((timestamp == 0) || (timestamp == getLastWriteTime(clazz))) {
                continue;
            }
            
            ITestSuiteGenerator generator = getTestSuiteGenerator(clazz);
            generator.setLocationDir(locationDir);

            TestClass testSuite = generator.generate(recordedClass);
            if (write(testSuite, locationDir)) {
                setLastWriteTime(clazz, timestamp);
                Logger.info("Generated test: " + locationDir.getAbsolutePath() + File.separator + testSuite.getFileName());
            }
        }
    }
    
    private long getLastWriteTime(Class<?> clazz)
    {
        Long time = _lastWriteTime.get(clazz);
        if (time == null) {
            return 0L;
        }
        return time.longValue();
    }
    
    private void setLastWriteTime(Class<?> clazz, long timestamp)
    {
        _lastWriteTime.put(clazz, timestamp);
    }
    
    private static ITestSuiteGenerator getTestSuiteGenerator(Class<?> testedClass)
    {
        return new JUnitParamsGenerator();
    }
    
    private synchronized static boolean write(TestClass testSuite, File destDir)
        throws IOException
    {
        if (!destDir.isDirectory() && !destDir.mkdirs()) {
            Logger.error("Cannot create test destination director: " + destDir.getAbsolutePath()); //$NON-NLS-1$
            return false;
        }
        
        String content = testSuite.toSourceCode();
        Path testPath = Paths.get(destDir.getAbsolutePath(), testSuite.getFileName());
        if (testPath.toFile().isFile()) {
            Logger.warn("Removing existing test file: " + testPath); //$NON-NLS-1$
            testPath.toFile().delete();
        }
        
        Files.write(testPath, content.getBytes(ENCODING), StandardOpenOption.CREATE);
        
        for (ResourceFile res : testSuite.getResources()) {
            
            Path path = Paths.get(destDir.getAbsolutePath(), res.getFileName());
            path.getParent().toFile().mkdirs();
        
            byte[] bytes = res.isBinary() ? 
                    res.getBinaryContent() : 
                    res.getContent().getBytes(ENCODING);
            
            if (path.toFile().isFile()) {
                Logger.warn("Removing existing resource file: " + testPath); //$NON-NLS-1$
                path.toFile().delete();
            }
                    
            Files.write(path, bytes, StandardOpenOption.CREATE);
        }
        return true;
    }
  
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$
    
}
