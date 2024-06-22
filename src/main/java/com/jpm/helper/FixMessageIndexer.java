package com.jpm.helper;

import com.jpm.fixparser.HighPerformanceLowMemoryFixParser;
import com.jpm.interfacce.Conformable;

import java.util.Arrays;

public final class FixMessageIndexer {
    private final int[] tagLookupIndices;

    /*
    Colum 0 = fix tag number
     */
    private final int[] fixTags;

    /*
    Colum 0 = tagIndex from fixTags[],
    Colum 1 = length of the tag Value
     */
    private final int[][] valueIndexLengthMatrix;
    private int currentTagIndex = 0;
    private byte[] rawFixMessage;
    private int rawFixMessageLength;

    public FixMessageIndexer(Conformable policy) {
        this.tagLookupIndices = new int[policy.getMaxFixTagSupported()];
        this.fixTags = new int[policy.getMaxNumberOfTagValuePairPerMessage()];
        this.valueIndexLengthMatrix = new int[policy.getMaxNumberOfTagValuePairPerMessage()][2];
        this.rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
    }

    public int addTag(int tag) {
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

    public byte getCharAt(int index) {
        return rawFixMessage[index];
    }

    public boolean isDelimiter(int i, HighPerformanceLowMemoryFixParser highPerformanceLowMemoryFixParser) {
        return getCharAt(i) == highPerformanceLowMemoryFixParser.delimiter;
    }

    public boolean isEquals(int index) {
        return getCharAt(index) == '=';
    }
}
