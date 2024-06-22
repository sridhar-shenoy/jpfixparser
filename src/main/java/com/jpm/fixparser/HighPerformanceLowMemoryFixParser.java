package com.jpm.fixparser;


import com.jpm.exception.MalformedFixMessageException;
import com.jpm.helper.FixMessageIndexer;
import com.jpm.helper.FixTag;
import com.jpm.helper.RepeatingGroupHandler;
import com.jpm.interfacce.Conformable;
import com.jpm.interfacce.FixMessageParser;
import com.jpm.interfacce.FixTagAccessor;
import com.jpm.interfacce.FixTagLookup;

import static com.jpm.exception.ErrorMessages.*;

public final class HighPerformanceLowMemoryFixParser implements FixTagAccessor, FixMessageParser {

    public static final char EQUALS_DELIMITER = '=';
    private final char delimiter;

    private final FixMessageIndexer fixMessageIndexer;
    private final RepeatingGroupHandler repeatGroupIndexer;
    private final FixTag fixTag;
    private final FixTagLookup dictionary;

    /**
     * @param policy
     */
    public HighPerformanceLowMemoryFixParser(Conformable policy) {
        delimiter = policy.delimiter();
        fixTag = new FixTag(policy);
        fixMessageIndexer = new FixMessageIndexer(policy);
        repeatGroupIndexer = new RepeatingGroupHandler(policy);
        dictionary = policy.dictionary();
    }

    @Override
    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        initializeInternalCache(msg);

        int currentTagIndex = 0;
        int currentRepeatGroupTagIndex = 0;
        boolean inRepeatGroup = false;
        boolean parsingNextTag = true;
        boolean parsedValue = false;

        for (int i = 0; i < fixMessageIndexer.getMessageLength(); i++) {
            if (parsingNextTag) {
                if (fixMessageIndexer.isCharEquals(i, EQUALS_DELIMITER)) {
                    //-- At this point we have FixTag constructed. Index it and update flags

                    int tag = fixTag.getTag();
                    if (dictionary.isRepeatingGroupBeginTag(tag)) {
                        inRepeatGroup = true;
                        repeatGroupIndexer.addBeginTagAndGetIndex(tag);
                    } else {
                        currentTagIndex = addAndGetIndex(i, tag);
                    }
                    parsingNextTag = false;
                    parsedValue = false;

                } else {
                    //-- At this point we are still constructing fixTag. Validate and continue parsing
                    constructFixTag(msg[i]);
                }
            } else {
                //-- keep moving currentTagIndex until we reach the agreed delimiter
                if (fixMessageIndexer.isCharEquals(i, delimiter)) {

                    if (inRepeatGroup) {

                    } else {
                        //-- At this point we have a value associated with the tag. Index it for tha associated tag.
                        linkValueLength(i, currentTagIndex);
                    }

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

    private void linkValueLength(int i, int currentTagIndex) throws MalformedFixMessageException {
        int valueLength = i - fixMessageIndexer.getValueIndexForTag(fixTag.getTag());

        //-- Ensure we have a valid value
        if (valueLength == 0) {
            throwException(MISSING_VALUE);
        }
        fixMessageIndexer.addValueLength(currentTagIndex, valueLength);
    }

    private void constructFixTag(byte msg) throws MalformedFixMessageException {
        //-- Convert the fixTag to integer here
        fixTag.build(msg);
        //-- Check if fixTag is valid
        validateFixTag(MALFORMED_TAG_VALUE_PAIR);
    }

    private int addAndGetIndex(int i, int tag) throws MalformedFixMessageException {
        //-- Validate always before indexing
        validateFixTag(INCORRECT_TAG);
        //-- Link parsed fix tag to its lookup Index
        return fixMessageIndexer.linkAndGetIndex(i, tag);
    }

    private void initializeInternalCache(byte[] msg) {
        fixTag.reset();
        fixMessageIndexer.copyToLocalCache(msg);
    }

    private void validateFixTag(String errorTest) throws MalformedFixMessageException {
        if (fixTag.isInvalid()) {
            throwException(errorTest);
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
        return fixMessageIndexer.getByteValueForTag(tag);
    }
}
