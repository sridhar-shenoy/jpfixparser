package com.jpm.benchmark;

import com.jpm.api.FixMessageParser;
import com.jpm.api.FixTagAccessor;
import com.jpm.exception.MalformedFixMessageException;

import java.util.HashMap;

public class TestOnlyParser implements FixMessageParser {

    private HashMap<String, String> keyValueMap = new HashMap<>();

    /**
     * @param msg
     */


    @Override
    public void parse(byte[] msg) {
        keyValueMap = new HashMap<>();
        String inputstring = new String(msg);
        String[] keyValuePairs = inputstring.split("\u0001");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            keyValueMap.put(keyValue[0],keyValue[1]);
        }
    }

    /**
     * @return
     * @throws MalformedFixMessageException
     */
    @Override
    public FixTagAccessor getReadOnlyClone() throws MalformedFixMessageException {
        throw new UnsupportedOperationException("Not Implemented as its only for testing");
    }

    /**
     * @param tag
     * @return
     */
    @Override
    public String getStringValueForTag(int tag) {
        return keyValueMap.get(Integer.toString(tag));
    }

    /**
     * @param tag
     * @return
     */
    @Override
    public byte[] getByteValueForTag(int tag) {
        String key = Integer.toString(tag);
        return keyValueMap.containsKey(key)?keyValueMap.get(key).getBytes():null;
    }

    /**
     * @param tag
     * @param output
     * @return
     */
    @Override
    public int copyByteValuesToArray(int tag, byte[] output) {

        String s = keyValueMap.get(Integer.toString(tag));
        if(s.length() <= output.length) {
            System.arraycopy(s.getBytes(), 0, output, 0, s.length());
            return s.length();
        }
        return -1;
    }

    /**
     * @param tag
     * @param repeatBeginTag
     * @param instance
     * @param instanceInMessage
     * @return
     */
    @Override
    public byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        throw new UnsupportedOperationException("Not Implemented as its only for testing");
    }

    /**
     * @param tag
     * @param repeatBeginTag
     * @param instance
     * @param instanceInMessage
     * @return
     */
    @Override
    public String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        throw new UnsupportedOperationException("Not Implemented as its only for testing");
    }

    /**
     * @param tag
     * @param repeatBeginTag
     * @param instance
     * @param instanceInMessage
     * @param output
     * @return
     */
    @Override
    public int copyByteValuesToArray(int tag, int repeatBeginTag, int instance, int instanceInMessage, byte[] output) {
        throw new UnsupportedOperationException("Not Implemented as its only for testing");
    }
}
