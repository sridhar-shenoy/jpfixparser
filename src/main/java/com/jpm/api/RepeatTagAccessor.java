package com.jpm.api;

public interface RepeatTagAccessor {
    byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);
    String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);
}
