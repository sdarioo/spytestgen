/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.logging;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Wrapper for java.util.Logger
 */
public class Logger 
{
    
    private static final java.util.logging.Logger LOGGER;
    private static final String NAME = "TestGen"; //$NON-NLS-1$
    
    static
    {
        //Must be called first before logger is used
        System.setProperty("java.util.logging.manager", CustomLogManager.class.getName()); //$NON-NLS-1$
        
        LOGGER = java.util.logging.Logger.getLogger(NAME);
        configureLogger();
    }
    
    public static void shutdown()
    {
        info("Shutdown logger."); //$NON-NLS-1$
        CustomLogManager.resetFinally();
    }
    
    public static void info(String fmt, Object... args)
    {
        String msg = MessageFormat.format(fmt, args);
        info(msg);
    }
    
    public static void info(String msg)
    {
        String[] caller = inferCaller();
        LOGGER.logp(Level.INFO, caller[0], caller[1], msg);
    }
    
    public static void warn(String msg)
    {
        String[] caller = inferCaller();
        LOGGER.logp(Level.WARNING, caller[0], caller[1], msg);
    }
    
    public static void warn(String msg, Throwable t)
    {
        String[] caller = inferCaller();
        LOGGER.logp(Level.WARNING, caller[0], caller[1], msg, t);
    }
    
    public static void error(String msg)
    {
        String[] caller = inferCaller();
        LOGGER.logp(Level.SEVERE, caller[0], caller[1], msg);
    }
    
    public static void error(String msg, Throwable t)
    {
        String[] caller = inferCaller();
        LOGGER.logp(Level.SEVERE, caller[0], caller[1], msg, t);
    }
    
    
    // Private method to infer the caller's class and method names
    private static String[] inferCaller() 
    {
        Throwable throwable = new Throwable();
        StackTraceElement[] frame = throwable.getStackTrace();
        return new String[]{frame[2].getClassName(), frame[2].getMethodName()};
    }
    
    private static void configureLogger()
    {
        try {
            InputStream is = Logger.class.getResourceAsStream("/logging.properties"); //$NON-NLS-1$
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
                is.close();
            } else {
                LOGGER.warning("Cannot read logging.properties file.");    //$NON-NLS-1$
            }
        } catch (Throwable thr) {
            LOGGER.log(Level.SEVERE, "Error while configuring logger.", thr); //$NON-NLS-1$
        }
    }
    
    /**
     * Custom log manager that overrwrites standard reset and 
     * enables logging from shutdown hooks.
     */
    public static class CustomLogManager 
        extends LogManager
    {
        static CustomLogManager instance;
        
        public CustomLogManager() 
        { 
            instance = this; 
        }
        
        @Override 
        public void reset() 
        { 
            /* don't reset yet. */ 
        }
        
        private void reset0()
        {
            super.reset(); 
        }
        
        public static void resetFinally() 
        {
            if (instance != null) {
                instance.reset0();
            }
        }
    }
}
