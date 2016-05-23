// AUTO-GENERATED
package com.github.sdarioo.testgen.generator.impl;

import com.github.sdarioo.testgen.generator.impl.GeneratorApp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(JUnitParamsRunner.class)
public class GeneratorAppTest
{
    @Test
    @Parameters(method = "testCount_Parameters")
    public void testCount(List<GeneratorApp.Pair<String>> list, String expected) throws Exception {
        String result = GeneratorApp.count(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testCountGeneric_Parameters")
    public <T> void testCountGeneric(List<GeneratorApp.Pair<T>> list, int expected) throws Exception {
        int result = GeneratorApp.countGeneric(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testGroupByKey_Parameters")
    public void testGroupByKey(List<GeneratorApp.Pair<String>> pairs, Map<String, GeneratorApp.Pair<String>> expected) throws Exception {
        Map<String, GeneratorApp.Pair<String>> result = GeneratorApp.groupByKey(pairs);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testConcat_Parameters")
    public void testConcat(GeneratorApp.StringList list, String expected) throws Exception {
        String result = GeneratorApp.concat(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testSort_Parameters")
    public <T extends Comparable<T>> void testSort(List<T> list, List<T> expected) throws Exception {
        List<T> result = GeneratorApp.sort(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testProxy_Parameters")
    public void testProxy(GeneratorApp.IListProvider provider, String[] args, List<String> expected) throws Exception {
        List<String> result = GeneratorApp.proxy(provider, args);
        Assert.assertEquals(expected, result);
    }

    @Test(expected=NullPointerException.class)
    public void testCheckedException() throws NullPointerException {
        GeneratorApp.checkedException(null);
    }

    @Test(expected=NullPointerException.class)
    public void testRuntimeException() throws NullPointerException {
        GeneratorApp.runtimeException(null);
    }

    @Test
    @Parameters(method = "testMain_Parameters")
    public void testMain(String[] args) throws Exception {
        GeneratorApp.main(args);
    }

    @SuppressWarnings("unused")
    private static Object[] testCount_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.<GeneratorApp.Pair<String>>asList(newPair("a", "b"), newPair("x", "y")), "2" }
        };
    }

    @SuppressWarnings("unused")
    private static Object[] testCountGeneric_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.asList(newPair("a", "b"), newPair("x", "y")), 0 }
        };
    }

    @SuppressWarnings("unused")
    private static Object[] testGroupByKey_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.<GeneratorApp.Pair<String>>asList(newPair("a", "b"), newPair("x", "y")), asMap(asPair("a", newPair("a", "b")), asPair("x", newPair("x", "y"))) }
        };
    }

    @SuppressWarnings("unused")
    private static Object[] testConcat_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ newStringList(Arrays.<String>asList("x", "y", "z")), "" }
        };
    }

    @SuppressWarnings("unused")
    private static Object[] testSort_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.asList("c", "b", "a"), Arrays.asList("a", "b", "c") }
        };
    }

    @SuppressWarnings("unused")
    private static Object[] testProxy_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ newIListProviderMock(new String[]{"a", "b", "c"}, Arrays.<String>asList("a", "b", "c")), new String[]{"a", "b", "c"}, Arrays.<String>asList("a", "b", "c") }
        };
    }

    @SuppressWarnings("unused")
    private static Object[] testMain_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ new String[]{} }
        };
    }

    private static <T> GeneratorApp.Pair<T> newPair(T x, T y) {
        GeneratorApp.Pair<T> result = new GeneratorApp.Pair<T>(x, y);
        return result;
    }

    private static Map<String, GeneratorApp.Pair<String>> asMap(Object[]... pairs) {
        HashMap<String, GeneratorApp.Pair<String>> map = new HashMap<String, GeneratorApp.Pair<String>>();
        for (Object[] pair : pairs) {
            map.put((String)pair[0], (GeneratorApp.Pair<String>)pair[1]);
        }
        return map;
    }

    private static Object[] asPair(Object key, Object value) {
        return new Object[] { key, value };
    }

    private static GeneratorApp.StringList newStringList(List<String> list) {
        GeneratorApp.StringList result = new GeneratorApp.StringList(list);
        return result;
    }

    private static GeneratorApp.IListProvider newIListProviderMock(String[] arg0, List<String> toListResult) {
        GeneratorApp.IListProvider mock = Mockito.mock(GeneratorApp.IListProvider.class);
        Mockito.when(mock.toList(arg0)).thenReturn(toListResult);
        return mock;
    }

}
