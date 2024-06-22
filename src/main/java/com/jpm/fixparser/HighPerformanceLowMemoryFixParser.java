package com.jpm.fixparser;


import com.jpm.exception.MalformedFixMessageException;
import com.jpm.interfacce.Conformable;
import com.jpm.interfacce.FixMessageParser;
import com.jpm.interfacce.FixTagAccessor;

import static com.jpm.exception.ErrorMessages.*;

public final class HighPerformanceLowMemoryFixParser implements FixTagAccessor, FixMessageParser {

    private final Conformable policy;
    private final char delimiter;
    private byte[] rawFixMessage;
    private final FixMessageIndexer indexer;

    /**
     * @param policy
     */
    public HighPerformanceLowMemoryFixParser(Conformable policy) {
        this.policy = policy;
        this.delimiter = policy.getFixDelimiter();
        this.rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
        indexer = new FixMessageIndexer(policy);
    }

    @Override
    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        System.arraycopy(msg, 0, rawFixMessage, 0, msg.length);
        indexer.reset();
        int currentTagIndex = 0;
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
                    currentTagIndex = indexer.addTag(fixTag);

                    //-- Record the Starting position of the value of this tag
                    indexer.addValueIndex(currentTagIndex,i+1);


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
                    int valueLength = i - indexer.getValueIndexForTag(fixTag);

                    //-- Ensure we have a valid value
                    if (valueLength == 0) {
                        throwException(MISSING_VALUE);
                    }
                    indexer.addValueLength(currentTagIndex,valueLength);
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
        int index = indexer.getIndexForTag(tag);
        if (index != -1) {
            int start = indexer.getValueIndexForTag(tag);
            int length = indexer.getValueLengthForTag(tag);
            byte[] value = new byte[length];
            System.arraycopy(rawFixMessage, start, value, 0, length);
            return value;
        }
        return null;
    }
}
