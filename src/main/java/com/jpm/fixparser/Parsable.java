package com.jpm.fixparser;

public interface Parsable {
    String getStringValueForTag(int tag);
    byte[] getByteValueForTag(int tag);
}
