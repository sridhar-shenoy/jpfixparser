package com.jpm.fixparser;


import com.jpm.exception.ErrorMessages;
import com.jpm.exception.MalformedFixMessageException;

import java.util.Arrays;

import static com.jpm.exception.ErrorMessages.*;

public class HighPerformanceLowMemoryFixParser implements Parsable {

    private int maxNumberOfFixTagsSupported;
    private final char delimiter;
    private byte[] rawFixMessage;
    private final int[] fixTags;
    private int maxNumberOfTagValuePairPerMessage;
    private final int[] tagLookupIndices;
    private final int[][] valueIndexLengthMatrix;
    private int currentTagIndex;

    /**
     * @param maxNumberOfTagValuePairPerMessage
     * @param maxNumberOfFixTagsSupported
     * @param delimiter
     */
    public HighPerformanceLowMemoryFixParser(int maxNumberOfTagValuePairPerMessage, int maxNumberOfFixTagsSupported, char delimiter) {
        this.fixTags = new int[maxNumberOfTagValuePairPerMessage / 2];
        this.maxNumberOfTagValuePairPerMessage = maxNumberOfTagValuePairPerMessage;
        this.tagLookupIndices = new int[maxNumberOfFixTagsSupported];
        this.valueIndexLengthMatrix = new int[maxNumberOfTagValuePairPerMessage / 2][2];
        this.maxNumberOfFixTagsSupported = maxNumberOfFixTagsSupported;
        this.delimiter = delimiter;
    }

    public void parse(byte[] msg) throws MalformedFixMessageException {
        this.rawFixMessage = msg;
        Arrays.fill(this.tagLookupIndices, -1);
        this.currentTagIndex = 0;
        int fixTag = 0;
        boolean parsingNextTag = true;
        boolean parsedValue = false;

        for (int i = 0; i < msg.length; i++) {
            if (parsingNextTag) {
                if (msg[i] == '=') {
                    //-- Link parsed fix tag to its lookup Index
                    if (fixTag <= 0 || fixTag > maxNumberOfFixTagsSupported) {
                        throw new MalformedFixMessageException(MISSING_TAG);
                    }
                    fixTags[currentTagIndex] = fixTag;
                    tagLookupIndices[fixTag] = currentTagIndex;

                    //-- Record the Starting position of the value of this tag
                    valueIndexLengthMatrix[currentTagIndex][0] = i + 1;

                    //-- Now that Tag is parsed, we enable the flag to parse its corresponding value
                    parsingNextTag = false;
                    parsedValue = false;
                } else {
                    //-- Convert the fixTag to integer here
                    fixTag = fixTag * 10 + (msg[i] - '0');
                    if (fixTag <= 0 || fixTag > maxNumberOfFixTagsSupported) {
                        throw new MalformedFixMessageException(MISSING_TAG);
                    }
                }
            } else {
                //-- keep moving currentTagIndex until we reach the agreed delimiter
                if (msg[i] == delimiter) {
                    //-- i - previously recorded Starting position of the value is the length of the value
                    int valueLength = i - valueIndexLengthMatrix[currentTagIndex][0];
                    if (valueLength == 0) {
                        throw new MalformedFixMessageException(MISSING_VALUE);
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
        if (parsedValue == false) {
            throw new MalformedFixMessageException(MISSING_DELIMITER);
        }
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
