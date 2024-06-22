package com.jpm.helper;

import com.jpm.api.Conformable;

import java.util.Arrays;

public final class FixMessageIndexer {

    /**
     * This array links the fix tag to its index in the fixTag array
     * for. e.g
     * Given fix message 35=D|34=100
     * tagLookupIndices[34] = 2
     * tagLookupIndices[35] = 1
     */
    private final int[] tagLookupIndices;

    /**
     * This array holds the link to its actual position within incoming rawFixMessage
     */
    private final int[] fixTags;

    /**
     * This matrix holds the link to value start position and its length
     * <p>
     * Colum 0 = index of first value character in rawFixMessage
     * Colum 1 = length of the tag Value
     */
    private final int[][] valueIndexLengthMatrix;

    /**
     * This counter is incremented every time a tag value pair is parsed
     */
    private int currentTagIndex = 0;


    public FixMessageIndexer(Conformable policy) {
        this.tagLookupIndices = new int[policy.maxFixTagSupported()];
        this.fixTags = new int[policy.maxNumberOfTagValuePairPerMessage()];
        this.valueIndexLengthMatrix = new int[policy.maxNumberOfTagValuePairPerMessage()][2];
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

    public void reset() {
        Arrays.fill(this.tagLookupIndices, -1);
    }
}
