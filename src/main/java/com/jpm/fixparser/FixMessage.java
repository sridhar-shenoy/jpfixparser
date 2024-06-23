package com.jpm.fixparser;

import com.jpm.api.ParsingPolicy;
import com.jpm.api.FixTagAccessor;
import com.jpm.api.FixTagLookup;
import com.jpm.helper.FixMessageIndexer;
import com.jpm.helper.RepeatingGroupIndexer;

/**
 * This class the <strong>Single Responsibility to wrap the raw fix message and delegate operation based on tags</strong>
 * This class holds reference to {@link FixMessageIndexer} & {@link RepeatingGroupIndexer} who maintain the indexes of these tags and values
 *
 * Any operation of rawfixmessage must be defined here.
 * @author Sridhar S Shenoy
 */
final class FixMessage implements FixTagAccessor {

    public static final char EQUALS_SIGN = '=';
    private final FixMessageIndexer fixMessageIndexer;
    private final RepeatingGroupIndexer repeatGroupIndexer;
    private final char delimiter;
    private final FixTagLookup dictionary;

    /**
     * incoming fix message must be copied to a local byte array
     * length of this byte array is decided by the policy class
     */
    private byte[] rawFixMessage;

    /**
     * This variable is set to length if the fix Message currently being parsed
     */
    private int rawFixMessageLength;

    public FixMessage(ParsingPolicy policy) {
        fixMessageIndexer = new FixMessageIndexer(policy);
        repeatGroupIndexer = new RepeatingGroupIndexer(policy);
        rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
        delimiter = policy.delimiter();
        dictionary = policy.dictionary();
    }

    @Override
    public String getStringValueForTag(int tag) {
        return new String(getByteValueForTag(tag));
    }

    @Override
    public byte[] getByteValueForTag(int tag) {
        int index = fixMessageIndexer.getIndexForTag(tag);
        if (index != -1) {
            int length = fixMessageIndexer.getValueLengthForTag(tag);
            byte[] value = new byte[length];
            System.arraycopy(rawFixMessage, fixMessageIndexer.getValueIndexForTag(tag), value, 0, length);
            return value;
        }
        return null;
    }

    @Override
    public int copyByteValuesToArray(int tag, byte[] output) {
        int index = fixMessageIndexer.getIndexForTag(tag);
        if (index != -1) {
            int length = fixMessageIndexer.getValueLengthForTag(tag);
            if (length <= output.length) {
                System.arraycopy(rawFixMessage, fixMessageIndexer.getValueIndexForTag(tag), output, 0, length);
                return length;
            }
        }
        return -1;
    }

    @Override
    public byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        if (dictionary.isTagMemberOfRepeatGroup(tag, repeatBeginTag)) {
            int index = repeatGroupIndexer.getIndexForTag(tag, instance, instanceInMessage);
            if (index != -1) {
                int length = repeatGroupIndexer.getValueLengthForIndex(index);
                byte[] value = new byte[length];
                System.arraycopy(rawFixMessage, repeatGroupIndexer.getValueIndexForRepeatTagIndex(index), value, 0, length);
                return value;
            }
        }
        return null;
    }

    @Override
    public int copyByteValuesToArray(int tag, int repeatBeginTag, int instance, int instanceInMessage, byte[] output) {
        if (dictionary.isTagMemberOfRepeatGroup(tag, repeatBeginTag)) {
            int index = repeatGroupIndexer.getIndexForTag(tag, instance, instanceInMessage);
            if (index != -1) {
                int length = repeatGroupIndexer.getValueLengthForIndex(index);
                if (length <= output.length) {
                    System.arraycopy(rawFixMessage, repeatGroupIndexer.getValueIndexForRepeatTagIndex(index), output, 0, length);
                    return length;
                }
            }
        }
        return -1;
    }

    @Override
    public String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        return new String(getByteValueForTag(tag, repeatBeginTag, instance, instanceInMessage));
    }

    public void copyFixMessageToLocal(byte[] bytes) {
        this.rawFixMessageLength = bytes.length;
        System.arraycopy(bytes, 0, this.rawFixMessage, 0, rawFixMessageLength);
    }

    public void reset() {
        fixMessageIndexer.reset();
        repeatGroupIndexer.reset();
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
