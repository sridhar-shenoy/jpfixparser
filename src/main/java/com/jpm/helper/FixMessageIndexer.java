package com.jpm.helper;

import com.jpm.interfacce.Conformable;

import java.util.Arrays;

public final class FixMessageIndexer {

    /**
     *  This array links the fix tag to its index in the fixTag array
     *  for. e.g
     *  Given fix message 35=D|34=100
     *  tagLookupIndices[34] = 2
     *  tagLookupIndices[35] = 1
     */
    private final int[] tagLookupIndices;

    /**
     * This array holds the link to its actual position within incoming rawFixMessage
     */
    private final int[] fixTags;

    /**
     * This matrix holds the link to value start position and its length
     *
     * Colum 0 = index of first value character in rawFixMessage
     * Colum 1 = length of the tag Value
     */
    private final int[][] valueIndexLengthMatrix;

    /**
     * This counter is incremented every time a tag value pair is parsed
     */
    private int currentTagIndex = 0;

    /**
     *  incoming fix message must be copied to a local byte array
     *  length of this byte array is decided by the policy class
     */
    private byte[] rawFixMessage;

    /**
     * This variable is set to length if the fixmessage currently being parsed
     */
    private int rawFixMessageLength;

    public FixMessageIndexer(Conformable policy) {
        this.tagLookupIndices = new int[policy.maxFixTagSupported()];
        this.fixTags = new int[policy.maxNumberOfTagValuePairPerMessage()];
        this.valueIndexLengthMatrix = new int[policy.maxNumberOfTagValuePairPerMessage()][2];
        this.rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
    }

    public int addTagAndGetIndex(int tag) {
        fixTags[currentTagIndex] = tag;
        tagLookupIndices[tag] = currentTagIndex;
        return currentTagIndex++;
    }

    public void addValueIndex(int index, int valueIndex) {
        valueIndexLengthMatrix[index][0] = valueIndex;
    }

    public void addValueLength(int index, int length) {
        valueIndexLengthMatrix[index][1] = length;
    }

    public int getIndexForTag(int tag) {
        return tagLookupIndices[tag];
    }

    public int getValueIndexForTag(int tag) {
        return valueIndexLengthMatrix[getIndexForTag(tag)][0];
    }

    public int getValueLengthForTag(int tag) {
        return valueIndexLengthMatrix[getIndexForTag(tag)][1];
    }

    private void reset() {
        Arrays.fill(this.tagLookupIndices, -1);
        rawFixMessageLength = 0;
    }

    public int getMessageLength() {
        return rawFixMessageLength;
    }

    public void copyToLocalCache(byte[] msg) {
        reset();
        rawFixMessageLength = msg.length;
        System.arraycopy(msg, 0, rawFixMessage, 0, rawFixMessageLength);
    }

    public byte[] getByteValueForTag(int tag) {
        int index = getIndexForTag(tag);
        if (index != -1) {
            int length = getValueLengthForTag(tag);
            byte[] value = new byte[length];
            System.arraycopy(rawFixMessage, getValueIndexForTag(tag), value, 0, length);
            return value;
        }
        return null;
    }

    public boolean isCharEquals(int i, char character) {
        return rawFixMessage[i] == character;
    }

    public int linkAndGetIndex(int i, int tag) {
        int currentTagIndex = addTagAndGetIndex(tag);
        //-- Record the Starting position of the value of this tag
        addValueIndex(currentTagIndex, i + 1);
        return currentTagIndex;
    }
}
