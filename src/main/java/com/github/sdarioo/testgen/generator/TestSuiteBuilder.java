/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.generator.impl.UniqueNamesProvider;
import com.github.sdarioo.testgen.generator.source.*;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.util.FileUtil;

public class TestSuiteBuilder
    implements IUniqueNamesProvider
{
    private final UniqueNamesProvider _methodNames;
    private final UniqueNamesProvider _fieldNames;
    private final UniqueNamesProvider _fileNames;
    
    private final boolean _useFullTypeNames;
    
    private String _qName;
    private String _signature;
    
    private final Set<String> _imports = new HashSet<String>();
    
    private final List<FieldSrc> _fields = new ArrayList<>();
    private final List<TestMethod> _testCases = new ArrayList<>();
    private final Map<MethodTemplate, TestMethod> _helperMethods = new LinkedHashMap<>();
    private final List<ResourceFile> _resources = new ArrayList<>();
    
    private final Map<String, MethodTemplate> _templatesCache = new HashMap<>();
    
    private int _helperMethodOrder = 1000000;
    
    public TestSuiteBuilder()
    {
        this(false, null);
    }
    
    public TestSuiteBuilder(boolean useFullTypeNames, File testLocation)
    {
        _useFullTypeNames = useFullTypeNames;
        _methodNames = new UniqueNamesProvider();
        _fieldNames = new UniqueNamesProvider();
        
        if (testLocation != null) {
            Set<String> usedNames = getExistingFileNames(testLocation);
            _fileNames = new UniqueNamesProvider(usedNames);
        } else {
            _fileNames = new UniqueNamesProvider();
        }
    }
    
    public TestClass buildTestClass()
    {
        List<TestMethod> methods = new ArrayList<TestMethod>();
        methods.addAll(_testCases);
        methods.addAll(_helperMethods.values());
        
        return new TestClass(_qName, _signature, _imports, _fields, methods, _resources);
    }
    
    public void addImport(String classOrPkg) 
    {
        if (!classOrPkg.startsWith("java.lang.")) { //$NON-NLS-1$
            _imports.add(classOrPkg);
        }
    }
    
    public void setCanonicalName(String qName)
    {
        _qName = qName;
    }

    public void setSignature(String signature) 
    {
        _signature = signature;
    }
    
    public boolean addField(FieldSrc field)
    {
        for (FieldSrc existing : _fields) {
            if (existing.getName().equals(field.getName())) {
                return false;
            }
        }
        return _fields.add(field);
    }
    
    public void addTestCase(TestMethod testCase)
    {
        _testCases.add(testCase);
    }
    
    public TestMethod addHelperMethod(MethodTemplate template, String name)
    {
        TestMethod method = _helperMethods.get(template);
        if (method == null) {
            String uniqueName = newUniqueMethodName(name);
            String sourceCode = template
                    .withName(uniqueName)
                    .with(MethodTemplate.SUITE_CLASS_VARIABLE, ClassUtils.getShortClassName(_qName))
                    .toString();

            method = new TestMethod(uniqueName, sourceCode, _helperMethodOrder++);
            _helperMethods.put(template, method);    
        }
        return method;
    }
    
    public ResourceFile addResource(byte[] bytes, String nameSuffix)
    {
        // Lookup existing resource with the same binary content
        for (ResourceFile res : _resources) {
            if (Objects.equals(res.getBinaryContent(), bytes)) {
                return res;
            }
        }
        String fileName = toResourceName(nameSuffix);
        String uniqueName = newUniqueFileName(fileName);
        
        ResourceFile resource = new ResourceFile("res/" + uniqueName, bytes); //$NON-NLS-1$
        _resources.add(resource);
        return resource;
    }
    
    public ResourceFile addResource(String content, String nameSuffix)
    {
        // Lookup existing resource with the same text content
        for (ResourceFile res : _resources) {
            if (Objects.equals(res.getContent(), content)) {
                return res;
            }
        }
        String fileName = toResourceName(nameSuffix);
        String uniqueName = newUniqueFileName(fileName);
        
        ResourceFile resource = new ResourceFile("res/" + uniqueName, content); //$NON-NLS-1$
        _resources.add(resource);
        return resource;
    }
    
    /**
     * Returns type name used by code generators. If using short type names than
     * apropriate import will be added to this builder.
     * @param type class
     * @param builder test class builder
     * @return type name 
     */
    public String getTypeName(Class<?> type)
    {
        return getTypeName(type.getName());
    }
    
    public String getTypeName(String className)
    {
        if (_useFullTypeNames) {
            return className;
        }
        // Add required import
        String componentType = className;
        int index = className.lastIndexOf('[');
        if (index >= 0) {
            componentType =  getElementType(className);
        }
        if (componentType.lastIndexOf('.') > 0) {
            String pkg = ClassUtils.getPackageCanonicalName(componentType);
            if (pkg.length() > 0) {
                String outClassName = ClassUtils.getShortCanonicalName(componentType);
                if (outClassName.indexOf('.') > 0) {
                    outClassName = outClassName.substring(0, outClassName.indexOf('.'));
                }
                addImport(pkg + '.' + outClassName);
            }
        }
        // Create short name
        String shortName = ClassUtils.getShortCanonicalName(className);
        return shortName;
    }
    
    /**
     * @see com.github.sdarioo.testgen.generator.IUniqueNamesProvider#newUniqueMethodName(java.lang.String)
     */
    @Override
    public String newUniqueMethodName(String methodName) 
    {
        return _methodNames.generateUniqueName(methodName);
    }
    
    @Override
    public String newUniqueFieldName(String fieldName) 
    {
        return _fieldNames.generateUniqueName(fieldName);
    }
    
    /**
     * @see com.github.sdarioo.testgen.generator.IUniqueNamesProvider#newUniqueFileName(java.lang.String)
     */
    @Override
    public String newUniqueFileName(String fileName) 
    {
        Pair<String, String> pair = FileUtil.getNameWithExtension(fileName);
        String baseName = pair.getLeft();
        String uniqueName = _fileNames.generateUniqueName(baseName);
        if (pair.getRight() != null) {
            return uniqueName + '.' + pair.getRight();
        }
        return uniqueName;
    }

    public Map<String, MethodTemplate> getTemplatesCache()
    {
        return _templatesCache;
    }
    
    public Set<String> getImports()
    {
        return Collections.unmodifiableSet(_imports);
    }
    
    public List<TestMethod> getHelperMethods()
    {
        return new ArrayList<TestMethod>(_helperMethods.values());
    }
 
    private String toResourceName(String suffix)
    {
        if (_qName == null) {
            return suffix;
        }
        int index = _qName.lastIndexOf('.');
        return (index > 0) ? 
                _qName.substring(index + 1) + '_' + suffix : 
                _qName + '_' + suffix;
    }
    
    private static String getElementType(String arrayType)
    {
        Type type = Type.getType(arrayType);
        while (type.getSort() == Type.ARRAY) {
            type = type.getElementType();
        }
        return InstrumentUtil.isPrimitive(type) ? type.getClassName() : type.getInternalName().replace('/', '.');
    }

    private static Set<String> getExistingFileNames(File dir)
    {
        if (!dir.isDirectory()) {
            return Collections.emptySet();    
        }
        Set<String> result = new HashSet<String>();
        File[] children = dir.listFiles();
        for (File file : children) {
            String name = file.getName();
            String nameWithoutExtension = FileUtil.stripExtension(name);
            
            if (!name.endsWith(TestClass.FILE_EXT)) {
                result.add(nameWithoutExtension);
                continue;
            }
            boolean bAutoGenerated = false;
            try {
                List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
                for (String line : lines) {
                    if (TestClass.AUTO_GENERATED_SIGNATURE.equals(line.trim())) {
                        bAutoGenerated = true;
                        break;
                    }
                }
            } catch (IOException e) {
                Logger.warn(e.getMessage(), e);
            }
            if (!bAutoGenerated) {
                result.add(nameWithoutExtension);
            }
        }
        return result;
    }
}
