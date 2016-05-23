package com.github.sdarioo.testgen.util;

public final class Defaults 
{
    private Defaults() {}
    
    public static Object getDefaultValue(Class<?> clazz) 
    {
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return DEFAULT_BOOLEAN;
            } else if (clazz.equals(byte.class)) {
                return DEFAULT_BYTE;
            } else if (clazz.equals(short.class)) {
                return DEFAULT_SHORT;
            } else if (clazz.equals(int.class)) {
                return DEFAULT_INT;
            } else if (clazz.equals(long.class)) {
                return DEFAULT_LONG;
            } else if (clazz.equals(float.class)) {
                return DEFAULT_FLOAT;
            } else if (clazz.equals(double.class)) {
                return DEFAULT_DOUBLE;
            } else {
                throw new IllegalArgumentException(
                    "Class type " + clazz + " not supported"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (clazz.isEnum()) {
            if (clazz.getEnumConstants().length > 0) {
                return clazz.getEnumConstants()[0];
            }
        }
        return null;
    }
    
    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;
}
