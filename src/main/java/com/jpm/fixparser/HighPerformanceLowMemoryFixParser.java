package com.jpm.fixparser;


import com.jpm.exception.MalformedFixMessageException;
import com.jpm.interfacce.Conformable;
import com.jpm.interfacce.FixMessageParser;
import com.jpm.interfacce.FixTagAccessor;

import java.util.Arrays;

import static com.jpm.exception.ErrorMessages.*;

public final class HighPerformanceLowMemoryFixParser implements FixTagAccessor, FixMessageParser {

    private final Conformable policy;
    private final char delimiter;
    private byte[] rawFixMessage;

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

    /*
    Colum 0 = repeatingGroup fix tag number ,
    Colum 1 = repeatingGroup fix tag number's occurrence index  within the repeat group
    Colum 2 = repeatingGroup fix tag number's occurrence index  within fix message
     */

    private int currentTagIndex;

    private final FixMessageIndexer indexer;

    /**
     * @param policy
     */
    public HighPerformanceLowMemoryFixParser(Conformable policy) {
        this.policy = policy;
        this.delimiter = policy.getFixDelimiter();
        this.rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
        indexer = new FixMessageIndexer(policy);

        this.tagLookupIndices = new int[policy.getMaxFixTagSupported()];

        this.fixTags = new int[policy.getMaxNumberOfTagValuePairPerMessage()];
        this.valueIndexLengthMatrix = new int[policy.getMaxNumberOfTagValuePairPerMessage()][2];
    }

    @Override
    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        System.arraycopy(msg, 0, rawFixMessage, 0, msg.length);
        Arrays.fill(this.tagLookupIndices, -1);
        this.currentTagIndex = 0;
        int fixTag = 0;
        boolean parsingNextTag = true;
        boolean parsedValue = false;

        for (int i = 0; i < msg.length; i++) {
            if (parsingNextTag) {
                if (msg[i] == '=') {

                    //-- Ensure we have a valid Fix Tag
                    if (fixTag <= 0 || fixTag > policy.getMaxFixTagSupported()) {
                        throwException(INCORRECT_TAG);
                    }

                    //-- Link parsed fix tag to its lookup Index
                    fixTags[currentTagIndex] = fixTag;
                    tagLookupIndices[fixTag] = currentTagIndex;

                    //-- Record the Starting position of the value of this tag
                    valueIndexLengthMatrix[currentTagIndex][0] = i + 1;

                    //-- Now that Tag is parsed, we enable the flag to parse its corresponding value
                    parsingNextTag = false;
                    parsedValue = false;
                } else {
                    //-- Convert the fixTag to integer here
                    fixTag = (fixTag * 10) + (msg[i] - '0');

                    //-- Check if fixTag is valid
                    if (fixTag <= 0 || fixTag > policy.getMaxFixTagSupported()) {
                        throwException(MALFORMED_TAG_VALUE_PAIR);
                    }
                }
            } else {
                //-- keep moving currentTagIndex until we reach the agreed delimiter
                if (msg[i] == delimiter) {
                    //-- (i - previously recorded Starting position of the value ) is the length of the value
                    int valueLength = i - valueIndexLengthMatrix[currentTagIndex][0];

                    //-- Ensure we have a valid value
                    if (valueLength == 0) {
                        throwException(MISSING_VALUE);
                    }
                    valueIndexLengthMatrix[currentTagIndex][1] = valueLength;
                    currentTagIndex++;
                    //-- Reset values to parse the next Tag Value Pair
                    parsingNextTag = true;
                    parsedValue = true;
                    fixTag = 0;
                }
            }
        }

        if (!parsedValue) {
            throwException(MISSING_DELIMITER);
        }
    }

    private static void throwException(String missingTag) throws MalformedFixMessageException {
        throw new MalformedFixMessageException(missingTag);
    }

    public String getStringValueForTag(int tag) {
        return new String(getByteValueForTag(tag));
    }

    /**
     * @param tag
     * @return
     */
    public byte[] getByteValueForTag(int tag) {
        int index = tagLookupIndices[tag];
        if (index != -1) {
            int start = valueIndexLengthMatrix[index][0];
            int length = valueIndexLengthMatrix[index][1];
            byte[] value = new byte[length];
            System.arraycopy(rawFixMessage, start, value, 0, length);
            return value;
        }
        return null;
    }
}
