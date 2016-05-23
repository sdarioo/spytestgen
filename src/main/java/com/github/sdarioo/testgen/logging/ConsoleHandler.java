/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.logging;

import java.util.logging.*;

public class ConsoleHandler extends StreamHandler {

    @Override
    public void publish(LogRecord record)
    {
        if (getFormatter() == null)
        {
            setFormatter(new SimpleFormatter());
        }

        try {
            String message = getFormatter().format(record);
            if (record.getLevel().intValue() >= Level.WARNING.intValue())
            {
                System.err.write(message.getBytes());                       
            }
            else
            {
                System.out.write(message.getBytes());
            }
        } catch (Exception exception) {
            reportError(null, exception, ErrorManager.FORMAT_FAILURE);
        }

    }

    @Override
    public void close() throws SecurityException {}
    @Override
    public void flush(){}
}
