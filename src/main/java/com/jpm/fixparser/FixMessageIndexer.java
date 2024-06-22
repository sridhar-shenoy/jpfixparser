package com.jpm.fixparser;

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

    public FixMessageIndexer(Conformable policy) {
        this.tagLookupIndices = new int[policy.getMaxFixTagSupported()];
        this.fixTags = new int[policy.getMaxNumberOfTagValuePairPerMessage()];
        this.valueIndexLengthMatrix = new int[policy.getMaxNumberOfTagValuePairPerMessage()][2];
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

    public void reset() {
        Arrays.fill(this.tagLookupIndices, -1);
    }
}
