package com.jpm.api;

public interface FixTagAccessor {
    String getStringValueForTag(int tag);
    byte[] getByteValueForTag(int tag);
    byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);
    String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);

}
