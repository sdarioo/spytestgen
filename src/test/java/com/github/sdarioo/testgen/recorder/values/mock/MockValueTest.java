package com.github.sdarioo.testgen.recorder.values.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;

public class MockValueTest
{
    
    //@Test
    public void testEquals()
    {
        IServiceProvider provider1 = new ServiceProvider();
        IServiceProvider provider2 = new ServiceProvider();
        
        IServiceProvider proxy1 = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider1);
        IServiceProvider proxy2 = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider2);
        
        assertNotSame(proxy1, proxy2);
        
        MockValue param1 = new MockValue(proxy1);
        MockValue param2 = new MockValue(proxy2);
        
        assertEquals(param1, param2);
        
        proxy1.getService("A"); //$NON-NLS-1$
        assertNotEquals(param1, param2);
        
        proxy2.getService("A"); //$NON-NLS-1$
        assertEquals(param1, param2);
    }
    
    
    @SuppressWarnings("nls")
    @Test
    public void testNestedMock()
    {
        IServiceProvider provider = new ServiceProvider();
        assertTrue(ProxyFactory.canProxy(IServiceProvider.class, provider));
        IServiceProvider proxy = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider);
        
        assertEquals(2, proxy.getService("A").length("xxx"));
        assertEquals(4, proxy.getService("B").length("xxx"));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        MockValue param = new MockValue(proxy);
        String src = param.toSourceCode(IServiceProvider.class, builder);
        assertEquals("newIServiceProviderMock()", src);
        
        verifyHelperMethod(builder, "newIServiceMock", new String[] {
                "private static MockValueTest.IService newIServiceMock(String arg0, int lengthResult) {",
                "    MockValueTest.IService mock = Mockito.mock(MockValueTest.IService.class);",
                "    Mockito.when(mock.length(arg0)).thenReturn(lengthResult);",
                "    return mock;",
                "}"
        });
        
        verifyHelperMethod(builder, "newIServiceProviderMock", new String[] {
                "private static MockValueTest.IServiceProvider newIServiceProviderMock() {",
                "    MockValueTest.IServiceProvider mock = Mockito.mock(MockValueTest.IServiceProvider.class);",
                "    Mockito.when(mock.getService(\"A\")).thenReturn(newIServiceMock(\"xxx\", 2));",
                "    Mockito.when(mock.getService(\"B\")).thenReturn(newIServiceMock(\"xxx\", 4));",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testNetstedCollectionOfMocks()
    {
        IServiceProvider provider = new ServiceProvider();
        assertTrue(ProxyFactory.canProxy(IServiceProvider.class, provider));
        IServiceProvider proxy = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider);
        List<IService> services = proxy.getServices();
        for (IService service : services) {
            service.length("xxx"); //$NON-NLS-1$
        }
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockValue param = new MockValue(proxy);
        
        boolean isSupported = param.isSupported(IServiceProvider.class, new HashSet<String>());
        assertTrue(isSupported);
        
        String src = param.toSourceCode(IServiceProvider.class, builder);
        
        assertEquals("newIServiceProviderMock(Arrays.<MockValueTest.IService>asList(newIServiceMock(\"xxx\", 2), newIServiceMock(\"xxx\", 4)))", src);
        
        verifyHelperMethod(builder, "newIServiceProviderMock", new String[] {
                "private static MockValueTest.IServiceProvider newIServiceProviderMock(List<MockValueTest.IService> getServicesResult) {",
                "    MockValueTest.IServiceProvider mock = Mockito.mock(MockValueTest.IServiceProvider.class);",
                "    Mockito.when(mock.getServices()).thenReturn(getServicesResult);",
                "    return mock;",
                "}"
        });
        
        verifyHelperMethod(builder, "newIServiceMock", new String[] {
                "private static MockValueTest.IService newIServiceMock(String arg0, int lengthResult) {",
                "    MockValueTest.IService mock = Mockito.mock(MockValueTest.IService.class);",
                "    Mockito.when(mock.length(arg0)).thenReturn(lengthResult);",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testWildcardMockCollection()
    {
        IServiceProvider provider = new ServiceProvider();
        IServiceProvider proxy = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider);
        List<? extends IService> services = proxy.getServicesExt();
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockValue param = new MockValue(proxy);
        
        boolean isSupported = param.isSupported(IServiceProvider.class, new HashSet<String>());
        assertTrue(isSupported);
        
        String src = param.toSourceCode(IServiceProvider.class, builder);
        assertEquals("newIServiceProviderMock(Arrays.asList(newIServiceMock(), newIServiceMock()))", src);
        
        verifyHelperMethod(builder, "newIServiceProviderMock", new String[] {
                "private static MockValueTest.IServiceProvider newIServiceProviderMock(List<? extends MockValueTest.IService> getServicesExtResult) {",
                "    MockValueTest.IServiceProvider mock = Mockito.mock(MockValueTest.IServiceProvider.class);",
                "    Mockito.doReturn(getServicesExtResult).when(mock).getServicesExt();",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenericMock1()
    {
        ICollector<String> collector = new ICollector<String>() {
            public int add(String value) { return 0; } 
        };
        Type collectorType = TypeUtils.parameterize(ICollector.class, String.class);
        ICollector<String> proxy = (ICollector<String>)ProxyFactory.newProxy(collectorType, collector);
        assertNotNull(proxy);
        assertFalse(collector == proxy);
        
        proxy.add("str");
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockValue param = new MockValue(proxy);
        boolean isSupported = param.isSupported(collectorType, new HashSet<String>());
        assertTrue(isSupported);
        
        String src = param.toSourceCode(IServiceProvider.class, builder);
        assertEquals("newICollectorMock(\"str\", 0)", src);
        
        verifyHelperMethod(builder, "newICollectorMock", new String[] {
                "private static <T> MockValueTest.ICollector<T> newICollectorMock(T arg0, int addResult) {",
                "    MockValueTest.ICollector mock = Mockito.mock(MockValueTest.ICollector.class);",
                "    Mockito.when(mock.add(arg0)).thenReturn(addResult);",
                "    return mock;",
                "}"
        });
    }

    @SuppressWarnings("nls")
    @Test
    public void testGenericMock2()
    {
        ICollector<String> collector = new ICollector<String>() {
            public int add(String value) { return 0; } 
        };
        Type collectorType = TypeUtils.parameterize(ICollector.class, String.class);
        ICollector<String> proxy = (ICollector<String>)ProxyFactory.newProxy(collectorType, collector);
        assertNotNull(proxy);
        assertFalse(collector == proxy);
        
        proxy.add("1");
        proxy.add("2");
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockValue param = new MockValue(proxy);
        boolean isSupported = param.isSupported(collectorType, new HashSet<String>());
        assertTrue(isSupported);
        
        String src = param.toSourceCode(IServiceProvider.class, builder);
        assertEquals("newICollectorMock()", src);
        
        verifyHelperMethod(builder, "newICollectorMock", new String[] {
                "private static <T> MockValueTest.ICollector<T> newICollectorMock() {",
                "    MockValueTest.ICollector mock = Mockito.mock(MockValueTest.ICollector.class);",
                "    Mockito.when(mock.add(\"1\")).thenReturn(0);",
                "    Mockito.when(mock.add(\"2\")).thenReturn(0);",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testSingletonMock()
    {
        ICollector<IService> collector = new ICollector<IService>() {
            public int add(IService value) { return 0; } 
        };
        IService service = new IService() {
            public int length(String str) { return 0; }
        };
        Type collectorType = TypeUtils.parameterize(ICollector.class, IService.class);
        ICollector<IService> collectorProxy = (ICollector<IService>)ProxyFactory.newProxy(collectorType, collector);
        IService serviceProxy = (IService)ProxyFactory.newProxy(IService.class, service);
        
        ProxyFactory.getHandler(serviceProxy).incReferencesCount();
        ProxyFactory.getHandler(serviceProxy).incReferencesCount();
        
        serviceProxy.length("text");
        collectorProxy.add(serviceProxy);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockValue param = new MockValue(serviceProxy);
        
        String src = param.toSourceCode(IService.class, builder);
        assertEquals("ISERVICE", src);
        
        verifyHelperMethod(builder, "newIServiceMock", new String[] {
                "private static MockValueTest.IService newIServiceMock(String arg0, int lengthResult) {",
                "    MockValueTest.IService mock = Mockito.mock(MockValueTest.IService.class);",
                "    Mockito.when(mock.length(arg0)).thenReturn(lengthResult);",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testMockCycles()
    {
        IType type = new IType() {public IType get() {return this;}};
        IType proxy = (IType)ProxyFactory.newProxy(IType.class, type);
        proxy.get();
        
        ProxyFactory.getHandler(proxy).incReferencesCount();
        ProxyFactory.getHandler(proxy).incReferencesCount();
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockValue param = new MockValue(proxy);
        
        assertTrue(param.isSupported(IType.class, new HashSet<String>()));
        assertTrue(param.hashCode() > 0);
        
        String src = param.toSourceCode(IType.class, builder);
        assertEquals("ITYPE", src);
        
        verifyHelperMethod(builder, "newITypeMock", new String[] {
                "private static MockValueTest.IType newITypeMock(MockValueTest.IType getResult) {",
                "    MockValueTest.IType mock = Mockito.mock(MockValueTest.IType.class);",
                "    Mockito.when(mock.get()).thenReturn(getResult);",
                "    return mock;",
                "}"
        });
    }
    
    private static void verifyHelperMethod(TestSuiteBuilder builder, String name, String[] expectedLines)
    {
        String src = null;
        List<TestMethod> methods = builder.getHelperMethods();
        for (TestMethod method : methods) {
            if (name.equals(method.getName())) {
                src = method.toSourceCode();
                break;
            }
        }
        assertNotNull(src);
        
        String[] lines = src.split("\\n"); //$NON-NLS-1$
        assertEquals(expectedLines.length, lines.length);
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], expectedLines[i]);
        }
    }
    
    //-----------------------------------------------------------
    
    public static interface ICollector<T>
    {
        int add(T value);
    }
    public static interface IServiceProvider
    {
        IService getService(String name);
        List<IService> getServices();
        List<? extends IService> getServicesExt();
    }
    public static interface IService
    {
        int length(String str);
    }
    public static interface IType
    {
        IType get();
    }
    
    private static class ServiceProvider
        implements IServiceProvider
    {
        @Override
        public IService getService(String name) {
            if ("A".equals(name)) {
                return new ServiceA();
            }
            return new ServiceB();
        }

        @Override
        public List<IService> getServices() {
            return Arrays.asList(new ServiceA(), new ServiceB());
        }
        @Override
        public List<? extends IService> getServicesExt() {
            return getServices();
        }
    }
    
    private static class ServiceA implements IService
    {
        @Override
        public int length(String str) {
            return str.length() - 1;
        }
    }
    private static class ServiceB implements IService
    {
        @Override
        public int length(String str) {
            return str.length() + 1;
        }
    }
    
}
