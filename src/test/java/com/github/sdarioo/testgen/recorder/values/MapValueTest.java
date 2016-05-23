package com.github.sdarioo.testgen.recorder.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.values.MapValue;

public class MapValueTest
{
    @Test
    public void testIsMapSupported() throws Exception
    {
        Method m = MapValueTest.class.getMethod("foo1", Map.class);
        MapValue p = new MapValue(new HashMap());
        assertTrue(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
        
        m = MapValueTest.class.getMethod("foo2", TreeMap.class);
        p = new MapValue(new HashMap());
        assertTrue(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
        
        m = MapValueTest.class.getMethod("foo5", SortedMap.class);
        p = new MapValue(new HashMap());
        assertTrue(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEquals()
    {
        Map<Integer, String> map = new HashMap<Integer, String>();
        assertEquals(new MapValue(map), new MapValue(map));
        
        map.put(1, "value");
        assertEquals(new MapValue(map), new MapValue(map));
        
        assertNotEquals(new MapValue(Collections.emptyMap()), new MapValue(map));
    }
    
    @Test
    public void testRawMap() throws Exception
    {
        MapValue p = new MapValue(Collections.emptyMap());
        testMap(p, Map.class, "asMap()", "private static Map asMap(Object[]... pairs) {");
    }
    
    @Test
    public void testGenericMap() throws Exception
    {
        Method m = MapValueTest.class.getMethod("foo1", Map.class);
        MapValue p = new MapValue(Collections.emptyMap());
        
        testMap(p, m.getGenericParameterTypes()[0], 
                "asMap()", "private static Map<Integer, String> asMap(Object[]... pairs) {");
    }
    
    @Test
    public void testWildcardMap() throws Exception
    {
        Method m = MapValueTest.class.getMethod("foo3", Map.class);
        MapValue p = new MapValue(Collections.<Integer, String>emptyMap());
        
        testMap(p, m.getGenericParameterTypes()[0], 
                "asMap()", "private static Map asMap(Object[]... pairs) {");
    }
    
    @Test
    public void testMapOfLists() throws Exception
    {
        Method m = MapValueTest.class.getMethod("foo4", Map.class);
        MapValue p = new MapValue(Collections.<Integer, String>emptyMap());
        
        testMap(p, m.getGenericParameterTypes()[0],
                "asMap()", "private static Map<Integer, List<String>> asMap(Object[]... pairs) {");
    }
    
    private void testMap(MapValue p, Type targetType, String sourceCode, String expectedSignature)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals(sourceCode, p.toSouceCode(targetType, builder));
        
        List<TestMethod> helperMethods = builder.getHelperMethods();
        assertEquals(2, helperMethods.size());
        
        boolean found = false;
        for (TestMethod helperMethod : helperMethods) {
            if (helperMethod.getName().equals("asMap")) {
                found = true;
                String signature = getFirstLine(helperMethod.toSourceCode());
                assertEquals(expectedSignature, signature);
            }
        }
        assertTrue("asMap not found", found);
    }

    private static String getFirstLine(String text)
    {
        return text.split("\\n")[0];
    }

    // DONT REMOVE - USED IN TEST 
    public void foo1(Map<Integer, String> map)   {}
    public void foo2(TreeMap map)                {}
    public static <K,V> void foo3(Map<K,V> map)  {}
    public void foo4(Map<Integer, List<String>> map)   {}
    public void foo5(SortedMap map)                {}
    // DONT REMOVE - USED IN TEST
    
    

}
