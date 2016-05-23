package com.github.sdarioo.testgen.agent;

import java.lang.instrument.Instrumentation;
import java.util.Timer;
import java.util.TimerTask;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.Generator;
import com.github.sdarioo.testgen.instrument.TestGenClassAdapter;
import com.github.sdarioo.testgen.logging.Logger;

public class Agent 
{
    public static void premain(String args, Instrumentation inst) 
    {
        Logger.info("Agent started."); //$NON-NLS-1$
        
        AgentArgs parsedArgs = parseArgs(args);
        if (parsedArgs == null) {
            logUsage();
            return;
        }
        Transformer transformer = new Transformer(parsedArgs.clazz, parsedArgs.method);
        inst.addTransformer(transformer);
     
        if (Configuration.getDefault().isBackgroundGenerationEnabled()) {
            startBackgroundGeneration(5000L, 5000L);
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Logger.info("Agent ShutdownHook started."); //$NON-NLS-1$
                Generator.getDefault().generateTests();
                Logger.shutdown();
            }
        }));
    }
    
    private static void startBackgroundGeneration(long delay, long period)
    {
        Logger.info("Starting Generator background thread. Delay={0}, period={1}", delay, period); //$NON-NLS-1$
        
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Generator.getDefault().generateTests();
            }
        }, delay, period);
    }
    
    private static AgentArgs parseArgs(String args)
    {
        if ((args == null) || (args.length() == 0)) {
            return null;
        }
        int index = args.lastIndexOf('.');
        if (index > 0) {
            return new AgentArgs(args.substring(0, index), args.substring(index + 1));
        }
        return new AgentArgs(args);
    }
    
    private static void logUsage()
    {
        Logger.warn("Missing agent parameters. Usage: -javaagent:<agent-jar>=<class>.<method>"); //$NON-NLS-1$
    }
    
    private static class AgentArgs
    {
        final String clazz;
        final String method;
        
        AgentArgs(String clazz)
        {
            this(clazz, TestGenClassAdapter.ALL_METHODS);
        }
        
        AgentArgs(String clazz, String method)
        {
            this.clazz = clazz;
            this.method = method;
        }
    }
}
