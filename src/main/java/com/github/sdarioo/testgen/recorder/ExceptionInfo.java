
package com.github.sdarioo.testgen.recorder;

import java.util.Objects;

public final class ExceptionInfo 
{
    private final String _className;
    private final String _message;

    public ExceptionInfo(Throwable exception) 
    {
        _className = exception.getClass().getName();
        String message = exception.getMessage();
        _message = (message != null) ? message : ""; //$NON-NLS-1$
    }
    
    public String getClassName()
    {
        return _className;
    }
    
    public String getMessage()
    {
        return _message;
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(_className, _message);
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof ExceptionInfo)) {
            return false;
        }
        ExceptionInfo other = (ExceptionInfo)obj;
        return _className.equals(other._className) &&
               _message.equals(other._message);
    }
}
