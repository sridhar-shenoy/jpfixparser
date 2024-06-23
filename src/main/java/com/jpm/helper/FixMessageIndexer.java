package com.jpm.helper;

import com.jpm.api.ParsingPolicy;

import java.util.Arrays;

/**
 * This class maintains a <strong>Single Responsibility to handle position of all simple tags ( non-repeatable ) within a fix message.</strong>
 *
 * The actual fix message in its raw form resides in {@link com.jpm.fixparser.FixMessage} class
 * <p>{@code fixTags} holds reference to all tags within a fix message
 * {@code currentTagIndex} maintains the current index of this array</p>
 *
 * <p>{@code tagLookupIndices} is a fixed length array whose index is the actual fix tag number
 * and value is the index to {@code fixtags}
 *
 * @author Sridhar S Shenoy
 * */
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


    public FixMessageIndexer(ParsingPolicy policy) {
        this.tagLookupIndices = new int[policy.maxFixTagSupported()];
        this.fixTags = new int[policy.maxNumberOfTagValuePairPerMessage()];
        this.valueIndexLengthMatrix = new int[policy.maxNumberOfTagValuePairPerMessage()][2];
    }

    /**
     * This class accepts fix tag to be indexed for parsing.
     * based on the {@code currentTagIndex }
     *
     * This method returns the index for this tag.
     * @param tag
     * @return
     */
    public int addTagAndGetIndex(int tag) {
        fixTags[currentTagIndex] = tag;
        tagLookupIndices[tag] = currentTagIndex;
        return currentTagIndex++;
    }

    /**
     * When a fix tag ia parsed, it must be added to index using {@code addTagAndGetIndex} method
     * This method sets the starting position of the value within the {@code rawfixMessage} in {@link com.jpm.fixparser.FixMessage } class
     * @param index
     * @param valueIndex
     */
    public void addValueIndex(int index, int valueIndex) {
        valueIndexLengthMatrix[index][0] = valueIndex;
    }

    /**
     * When a fix tag ia parsed, it must be added to index using {@code addTagAndGetIndex} method
     * This method sets the length of the value within the {@code rawfixMessage} in {@link com.jpm.fixparser.FixMessage } class
     * @param index
     * @param length
     */
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
        currentTagIndex =0;
    }
}
