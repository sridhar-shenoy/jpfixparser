package com.jpm.fixparser;


import com.jpm.api.*;
import com.jpm.exception.MalformedFixMessageException;
import com.jpm.helper.FixMessageIndexer;
import com.jpm.helper.FixTag;
import com.jpm.helper.RepeatingGroupHandler;

import static com.jpm.exception.ErrorMessages.*;

public final class HighPerformanceLowMemoryFixParser implements FixTagAccessor, FixMessageParser, RepeatTagAccessor {

    public static final char EQUALS_DELIMITER = '=';
    private final char delimiter;

    private final FixMessageIndexer fixMessageIndexer;
    private final RepeatingGroupHandler repeatGroupIndexer;
    private final FixTag fixTag;
    private final FixTag noOfRepeatGroupValue;
    private final FixTagLookup dictionary;
    private final int[][] tagMembersOfRepeatGroup;
    /**
     *  incoming fix message must be copied to a local byte array
     *  length of this byte array is decided by the policy class
     */
    private byte[] rawFixMessage;

    /**
     * This variable is set to length if the fix Message currently being parsed
     */
    private int rawFixMessageLength;
    private int lengthOfTagMemebers = 0;


    /**
     * @param policy
     */
    public HighPerformanceLowMemoryFixParser(Conformable policy) {
        delimiter = policy.delimiter();
        fixTag = new FixTag(policy);
        noOfRepeatGroupValue = new FixTag(policy);
        fixMessageIndexer = new FixMessageIndexer(policy);
        repeatGroupIndexer = new RepeatingGroupHandler(policy);
        dictionary = policy.dictionary();
        rawFixMessage = new byte[policy.maxLengthOfFixMessage()];
        tagMembersOfRepeatGroup = new int[policy.maxNumberOfMemebersInRepeatingGroup()][2];
    }

    @Override
    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        fixTag.reset();
        noOfRepeatGroupValue.reset();
        fixMessageIndexer.reset();
        rawFixMessageLength = msg.length;
        System.arraycopy(msg, 0, rawFixMessage, 0, rawFixMessageLength);

        int currentTagIndex = 0;
        int currentRepeatGroupTagIndex = 0;
        boolean inRepeatGroup = false;
        boolean parsingNextTag = true;
        boolean parsedValue = false;
        short countOfRepeatGroupParsed = 0;
        boolean parsingRepeatingGroupCount = false;
        int repeatGroupBeginTag = 0;


        for (int i = 0; i < rawFixMessageLength; i++) {
            if (parsingNextTag) {
                if (isCharEquals(i, EQUALS_DELIMITER)) {
                    //-- At this point we have FixTag constructed. Index it and update flags

                    int tag = fixTag.getTag();
                    if (!dictionary.isTagMemberOfRepeatGroup(tag, repeatGroupBeginTag)) {
                        inRepeatGroup = false;
                    }
                    if (!inRepeatGroup && dictionary.isRepeatingGroupBeginTag(tag)) {
                        inRepeatGroup = true;
                        parsingRepeatingGroupCount = true;
                        repeatGroupBeginTag = tag;
                        currentRepeatGroupTagIndex = repeatGroupIndexer.addBeginTagAndGetIndex(tag);
                        repeatGroupIndexer.addValueIndex(currentRepeatGroupTagIndex, i + 1);
                        lengthOfTagMemebers = dictionary.copyTagMembersOfRepeatGroupTo(tag, tagMembersOfRepeatGroup);
                    } else if(inRepeatGroup) {
                        parsingRepeatingGroupCount = false;
                        if(countOfRepeatGroupParsed == noOfRepeatGroupValue.getTag()){
                            inRepeatGroup = false;
                            countOfRepeatGroupParsed =0;
                            noOfRepeatGroupValue.reset();
                        }
                        if(alreadyCheckedRepeatGroup(tag)){
                            countOfRepeatGroupParsed++;
                            resetRepeatingGroupMembers(tag);
                        }
                        currentRepeatGroupTagIndex = repeatGroupIndexer.addGroupMemberTagForRepeatBeginTag(tag, currentRepeatGroupTagIndex);
                        repeatGroupIndexer.addValueIndex(currentRepeatGroupTagIndex, i + 1);
                        unsetRepeatingMembers(tag);
                    } else {
                        currentTagIndex = addAndGetIndex(tag);
                        fixMessageIndexer.addValueIndex(currentTagIndex, i + 1);
                    }
                    parsingNextTag = false;
                    parsedValue = false;

                } else {
                    //-- At this point we are still constructing fixTag. Validate and continue parsing
                    parseNumber(rawFixMessage[i], fixTag);
                }
            } else {
                //-- keep moving currentTagIndex until we reach the agreed delimiter
                if (isCharEquals(i, delimiter)) {
                    int valueLength = i - (inRepeatGroup ? repeatGroupIndexer.getValueIndexForRepeatTagIndex(currentRepeatGroupTagIndex) : fixMessageIndexer.getValueIndexForTag(fixTag.getTag()));
                    //-- Ensure we have a valid value
                    if (valueLength == 0) {
                        throwException(MISSING_VALUE);
                    }

                    if (inRepeatGroup) {
                        repeatGroupIndexer.addValueLength(currentRepeatGroupTagIndex, valueLength);
                    } else {
                        //-- At this point we have a value associated with the tag. Index it for tha associated tag.
                        fixMessageIndexer.addValueLength(currentTagIndex, valueLength);
                    }

                    //-- Reset values to parse the next Tag Value Pair
                    parsingNextTag = true;
                    parsedValue = true;
                    fixTag.reset();
                } else {
                    if(inRepeatGroup && parsingRepeatingGroupCount) {
                        parseNumber(rawFixMessage[i], noOfRepeatGroupValue);
                    }
                }
            }
        }
        if (!parsedValue) {
            throwException(MISSING_DELIMITER);
        }
    }

    private void resetRepeatingGroupMembers(int tag) {
        for (int i = 0; i < lengthOfTagMemebers; i++) {
                tagMembersOfRepeatGroup[i][1] = -1;
            }
    }

    private boolean alreadyCheckedRepeatGroup(int tag) {
        for (int i = 0; i < lengthOfTagMemebers; i++) {
            if(tagMembersOfRepeatGroup[i][0] == tag && tagMembersOfRepeatGroup[i][1] == 1){
                return true;
            }
        }
        return false;
    }

    private void unsetRepeatingMembers(int tag) {
        for (int i = 0; i < lengthOfTagMemebers; i++) {
            if(tagMembersOfRepeatGroup[i][0] == tag){
                tagMembersOfRepeatGroup[i][1] = 1;
                return;
            }
        }
    }

    private void parseNumber(byte msg, FixTag tag) throws MalformedFixMessageException {
        //-- Convert the fixTag to integer here
        tag.build(msg);
        //-- Check if fixTag is valid
        validateFixTag(MALFORMED_TAG_VALUE_PAIR);
    }

    private int addAndGetIndex(int tag) throws MalformedFixMessageException {
        //-- Validate always before indexing
        validateFixTag(INCORRECT_TAG);
        //-- Link parsed fix tag to its lookup Index
        //-- Record the Starting position of the value of this tag
        return fixMessageIndexer.addTagAndGetIndex(tag);
    }

    private void validateFixTag(String errorTest) throws MalformedFixMessageException {
        if (fixTag.isInvalid()) {
            throwException(errorTest);
        }
    }

    private static void throwException(String missingTag) throws MalformedFixMessageException {
        throw new MalformedFixMessageException(missingTag);
    }

    @Override
    public String getStringValueForTag(int tag) {
        return new String(getByteValueForTag(tag));
    }

    @Override
    public byte[] getByteValueForTag(int tag) {
        int index = fixMessageIndexer.getIndexForTag(tag);
        if (index != -1) {
            int length = fixMessageIndexer.getValueLengthForTag(tag);
            byte[] value = new byte[length];
            System.arraycopy(rawFixMessage, fixMessageIndexer.getValueIndexForTag(tag), value, 0, length);
            return value;
        }
        return null;
    }

    public boolean isCharEquals(int i, char character) {
        return rawFixMessage[i] == character;
    }

    @Override
    public byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        if(dictionary.isTagMemberOfRepeatGroup(tag,repeatBeginTag)) {
            int index = repeatGroupIndexer.getIndexForTag(tag,instance,instanceInMessage);
            if (index != -1) {
                int length = repeatGroupIndexer.getValueLengthForIndex(index);
                byte[] value = new byte[length];
                System.arraycopy(rawFixMessage, repeatGroupIndexer.getValueIndexForRepeatTagIndex(index), value, 0, length);
                return value;
           }
        }
        return null;
    }

    @Override
    public String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        return new String(getByteValueForTag(tag,repeatBeginTag,instance,instanceInMessage));
    }
}
