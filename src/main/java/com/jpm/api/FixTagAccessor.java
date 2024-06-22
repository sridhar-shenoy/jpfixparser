package com.jpm.api;

public interface FixTagAccessor {
    String getStringValueForTag(int tag);
    byte[] getByteValueForTag(int tag);
}
