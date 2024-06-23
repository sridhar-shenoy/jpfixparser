package com.jpm.api;

/**
 * This interface defines the readonly operations available for clients
 *
 * @author Sridhar S Shenoy
 */
public interface FixTagAccessor {
    String getStringValueForTag(int tag);
    byte[] getByteValueForTag(int tag);
    int copyByteValuesToArray(int tag, byte[] output);

    byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);
    String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);
    int copyByteValuesToArray(int tag, int repeatBeginTag, int instance, int instanceInMessage, byte[] output);

}
