package com.github.sdarioo.testgen.recorder.values.mock;

import java.lang.reflect.Type;
import java.util.*;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.values.AbstractValue;
import com.github.sdarioo.testgen.recorder.values.IAggregateValue;
import com.github.sdarioo.testgen.recorder.values.IValue;

public class MockValue
    extends AbstractValue
    implements IAggregateValue
{
    private final Object _proxy;
    
    public MockValue(Object proxy)
    {
        super(proxy.getClass());
        _proxy = proxy;
    }

    @Override
    public Class<?> getRecordedType() 
    {
        RecordingInvocationHandler handler = getHandler();
        return handler.getType();
    }
    
    /**
     * @see com.github.sdarioo.testgen.recorder.values.IAggregateValue#getComponents()
     */
    @Override
    public Collection<IValue> getComponents() 
    {
        List<IValue> components = new ArrayList<IValue>();
        for (Call call : getHandler().getCalls()) {
            components.addAll(call.args());
            components.add(call.getResult());
        }
        return components;
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        RecordingInvocationHandler handler = getHandler();
        if (handler == null) {
            errors.add("Unexpected proxy InvocationHandler."); //$NON-NLS-1$
            return false;
        }
        if (targetType == null) {
            errors.add("Mock prameter target type is unknown."); //$NON-NLS-1$
            return false;
        }
        
        Set<String> subErrors = new HashSet<>();
        
        if (!super.isAssignable(handler.getType(), targetType, subErrors)) {
            errors.add(createErrorMessage(subErrors));
            return false;
        }
        if (!handler.isSupported(subErrors)) {
            errors.add(createErrorMessage(subErrors));
            return false;
        }
        return true;
    }

    @Override
    public String toSourceCode(Type targetType, TestSuiteBuilder builder)
    {
        MockValueHelper helper = new MockValueHelper(getHandler(), builder);
        return helper.toSouceCode();
    }
    
    public RecordingInvocationHandler getHandler()
    {
        return ProxyFactory.getHandler(_proxy);
    }
    
    @SuppressWarnings("nls")
    private String createErrorMessage(Set<String> subErrors)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Problems while recording mock invocations for %s:%n", getHandler().getType().getName()));
        for (String msg : subErrors) {
            sb.append(String.format("  - %s%n", msg));
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MockValue other = (MockValue)obj;
        return getHandler().equals(other.getHandler());
    }
    
    @Override
    public int hashCode() 
    {
        RecordingInvocationHandler handler = getHandler();
        return handler.hashCode();
    }

}
