/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

import java.util.Arrays;
import java.util.Objects;

public class ResourceFile 
{
    private final String _fileName;
    
    private final byte[] _bytes;
    private final String _content;
    
    public ResourceFile(String fileName, String content)
    {
        _fileName = fileName;
        _content = content;
        _bytes = null;
    }
    
    public ResourceFile(String fileName, byte[] bytes)
    {
        _fileName = fileName;
        _bytes = bytes;
        _content = null;
    }
    
    public String getFileName()
    {
        return _fileName;
    }
    
    public String getContent()
    {
        return _content;
    }
    
    public byte[] getBinaryContent()
    {
        return _bytes;
    }
    
    public boolean isBinary()
    {
        return _bytes != null;
    }
    
    @Override
    public int hashCode() 
    {
        return (_content != null) ? 
                _content.hashCode() : Arrays.hashCode(_bytes);
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof ResourceFile)) {
            return false;
        }
        ResourceFile other = (ResourceFile)obj;
        return Objects.equals(_content, other._content) &&
               Objects.equals(_bytes, other._bytes);
    }
    
}
