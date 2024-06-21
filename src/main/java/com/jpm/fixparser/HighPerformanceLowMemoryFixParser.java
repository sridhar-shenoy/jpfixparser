package com.jpm.fixparser;


import com.jpm.exception.MalformedFixMessageException;

import java.util.Arrays;

import static com.jpm.exception.ErrorMessages.*;

public final class HighPerformanceLowMemoryFixParser implements Parsable {

    private final int maxNumberOfFixTagsSupported;
    private final char delimiter;
    private byte[] rawFixMessage;
    private final int[] fixTags;
    private final int[] tagLookupIndices;
    private final int[][] valueIndexLengthMatrix;
    private int currentTagIndex;

    /**
     * @param maxNumberOfTagValuePairPerMessage
     * @param maxFixTagSupported
     * @param delis
     */
    public HighPerformanceLowMemoryFixParser(int maxNumberOfTagValuePairPerMessage, int maxFixTagSupported, char delis) {
        this.fixTags = new int[maxNumberOfTagValuePairPerMessage / 2];
        this.tagLookupIndices = new int[maxFixTagSupported];
        this.valueIndexLengthMatrix = new int[maxNumberOfTagValuePairPerMessage / 2][2];
        this.maxNumberOfFixTagsSupported = maxFixTagSupported;
        this.delimiter = delis;
    }

    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        this.rawFixMessage = msg;
        Arrays.fill(this.tagLookupIndices, -1);
        this.currentTagIndex = 0;
        int fixTag = 0;
        boolean parsingNextTag = true;
        boolean parsedValue = false;

        for (int i = 0; i < msg.length; i++) {
            if (parsingNextTag) {
                if (msg[i] == '=') {

                    //-- Ensure we have a valid Fix Tag
                    if (fixTag <= 0 || fixTag > maxNumberOfFixTagsSupported) {
                        throwException(MALFORMED_TAG_VALUE_PAIR);
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
                    if (fixTag <= 0 || fixTag > maxNumberOfFixTagsSupported) {
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
