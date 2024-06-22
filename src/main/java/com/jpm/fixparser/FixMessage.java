package com.jpm.fixparser;

import com.jpm.api.Conformable;
import com.jpm.api.FixTagAccessor;
import com.jpm.helper.FixMessageIndexer;
import com.jpm.helper.RepeatingGroupIndexer;

class FixMessage implements FixTagAccessor {

    public static final char EQUALS_SIGN = '=';
    private final FixMessageIndexer fixMessageIndexer;
    private final RepeatingGroupIndexer repeatGroupIndexer;
    private final char delimiter;

    /**
     *  incoming fix message must be copied to a local byte array
     *  length of this byte array is decided by the policy class
     */
    private byte[] rawFixMessage;

    /**
     * This variable is set to length if the fix Message currently being parsed
     */
    private int rawFixMessageLength;

    public FixMessage(Conformable policy) {
        fixMessageIndexer = new FixMessageIndexer(policy);
        repeatGroupIndexer = new RepeatingGroupIndexer(policy);
        rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
        delimiter = policy.delimiter();
    }

    @Override
    public String getStringValueForTag(int tag) {
        return "";
    }

    @Override
    public byte[] getByteValueForTag(int tag) {
        return new byte[0];
    }

    @Override
    public byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        return new byte[0];
    }

    @Override
    public String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        return "";
    }

    public void copyFixMessageToLocal(byte[] rawFixMessage) {
        this.rawFixMessageLength = rawFixMessage.length;
        System.arraycopy(rawFixMessage, 0, this.rawFixMessage, 0, rawFixMessageLength);
    }

    public void reset() {
        fixMessageIndexer.reset();
        repeatGroupIndexer.reset();
    }

    public int fixMessageLength() {
        return rawFixMessageLength;
    }

    public boolean hasCompletedParsingTag(int index) {
        return isEquals(index, EQUALS_SIGN);
    }

    public boolean hasCompletedParsingTagValuePair(int index) {
        return isEquals(index, delimiter);
    }

    private boolean isEquals(int index, char character) {
        return rawFixMessage[index] == character;
    }

    public int getMessageLength() {
        return rawFixMessageLength;
    }

    public byte[] getRawFixMessage() {
        return rawFixMessage;
    }

    public byte byteAt(int index) {
        return rawFixMessage[index];
    }

    public FixMessageIndexer getFixMessageIndexer() {
        return fixMessageIndexer;
    }

    public RepeatingGroupIndexer getRepeatGroupIndexer() {
        return repeatGroupIndexer;
    }
}
