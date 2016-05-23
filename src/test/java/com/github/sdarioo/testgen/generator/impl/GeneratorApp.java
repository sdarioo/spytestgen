/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.util.*;

public class GeneratorApp 
{
    public static void main(String[] args) 
    {
        Pair<String> p1 = new Pair<String>("a", "b");
        Pair<String> p2 = new Pair<String>("x", "y");
     
         
        List<Pair<String>> pairList = Arrays.asList(p1, p2);
        count(pairList);
        countGeneric(pairList);
        groupByKey(pairList);
        
        StringList stringList = new StringList(Arrays.asList("x", "y", "z"));
        concat(stringList);
        
        List<String> listOfString = Arrays.asList("c", "b", "a");
        sort(listOfString);
        
        proxy(new IListProvider() {
            @Override
            public List<String> toList(String... args) {
                return Arrays.asList(args);
            }
        }, "a", "b", "c");

        try { checkedException(null); } catch (NullPointerException e) { }
        try { runtimeException(null); } catch (NullPointerException e) { }
        
        System.out.println("OK");
    }
    
    public static String count(List<Pair<String>> list)
    {
        return String.valueOf(list.size());
    }
    
    public static <T> int countGeneric(List<Pair<T>> list)
    {
        return 0;
    }
       
    public static String concat(StringList list)
    {
        return "";
    }
    
    public static List<String> proxy(IListProvider provider, String... args)
    {
        return provider.toList(args);
    }
    
    public static <T extends Comparable<T>> List<T> sort(List<T> list)
    {
        Collections.sort(list);
        return list;
    }
    
    public static Map<String, Pair<String>> groupByKey(List<Pair<String>> pairs)
    {
        Map<String, Pair<String>> result = new HashMap<String, GeneratorApp.Pair<String>>();
        for (Pair<String> pair : pairs) {
            result.put(pair.getX(), pair);
        }
        return result;
    }
    
    public static void checkedException(String str)
    {
        if ("text".length() == 4) {
            throw new NullPointerException();
        }
    }
    
    public static int runtimeException(String str)
    {
        return str.length();
    }
    
    static class Pair<T>
    {
        final T _x, _y;
        
        Pair(T x, T y) {
            _x = x;
            _y = y;
        }
        
        T getX() { return _x; }
        T getY() { return _y; }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                Pair other = (Pair)obj;
                return _x.equals(other._x) && _y.equals(other._y);
            }
            return false;
        }
    }
    
    static class StringList
    {
        private final List<String> list;
        
        StringList(List<String> list)
        {
            this.list = list;
        }
    }
    
    public static interface IListProvider
    {
        List<String> toList(String... args);
    }

}
