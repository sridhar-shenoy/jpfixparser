package com.jpm.fixparser;


import com.jpm.exception.MalformedFixMessageException;
import com.jpm.helper.FixMessageIndexer;
import com.jpm.helper.FixTag;
import com.jpm.interfacce.Conformable;
import com.jpm.interfacce.FixMessageParser;
import com.jpm.interfacce.FixTagAccessor;

import static com.jpm.exception.ErrorMessages.*;

public final class HighPerformanceLowMemoryFixParser implements FixTagAccessor, FixMessageParser {

    private final char delimiter;
    private byte[] rawFixMessage;
    private final FixMessageIndexer indexer;
    private final FixTag fixTag;

    /**
     * @param policy
     */
    public HighPerformanceLowMemoryFixParser(Conformable policy) {
        this.delimiter = policy.getFixDelimiter();
        this.rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
        indexer = new FixMessageIndexer(policy);
        fixTag = new FixTag(policy.getMaxFixTagSupported());
    }

    @Override
    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        initializeInternalCache(msg);

        int currentTagIndex = 0;
        boolean parsingNextTag = true;
        boolean parsedValue = false;

        for (int i = 0; i < msg.length; i++) {
            if (parsingNextTag) {
                if (msg[i] == '=') {
                    //-- At this point we have FixTag constructed. Index it and update flags
                    currentTagIndex = indexTheTag(i);
                    parsingNextTag = false;
                    parsedValue = false;
                } else {
                    //-- At this point we are still constructing fixTag. Validate and continue parsing
                    constructFixTag(msg[i]);
                }
            } else {
                //-- keep moving currentTagIndex until we reach the agreed delimiter
                if (msg[i] == delimiter) {
                    //-- (i - previously recorded Starting position of the value ) is the length of the value
                    int valueLength = i - indexer.getValueIndexForTag(fixTag.getTag());

                    //-- Ensure we have a valid value
                    if (valueLength == 0) {
                        throwException(MISSING_VALUE);
                    }
                    indexer.addValueLength(currentTagIndex, valueLength);
                    //-- Reset values to parse the next Tag Value Pair
                    parsingNextTag = true;
                    parsedValue = true;
                    fixTag.reset();
                }
            }
        }

        if (!parsedValue) {
            throwException(MISSING_DELIMITER);
        }
    }

    private void constructFixTag(byte msg) throws MalformedFixMessageException {
        //-- Convert the fixTag to integer here
        fixTag.build(msg);
        //-- Check if fixTag is valid
        validateFixTag(MALFORMED_TAG_VALUE_PAIR);
    }

    private int indexTheTag(int i) throws MalformedFixMessageException {
        //-- Validate always before indexing
        validateFixTag(INCORRECT_TAG);
        //-- Link parsed fix tag to its lookup Index
        int currentTagIndex = indexer.addTag(fixTag.getTag());
        //-- Record the Starting position of the value of this tag
        indexer.addValueIndex(currentTagIndex, i + 1);

        return currentTagIndex;
    }

    private void initializeInternalCache(byte[] msg) {
        copyToLocalCache(msg);
        indexer.reset();
        fixTag.reset();
    }

    private void validateFixTag(String errorTest) throws MalformedFixMessageException {
        if (fixTag.isInvalid()) {
            throwException(errorTest);
        }
    }

    private void copyToLocalCache(byte[] msg) {
        System.arraycopy(msg, 0, rawFixMessage, 0, msg.length);
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
