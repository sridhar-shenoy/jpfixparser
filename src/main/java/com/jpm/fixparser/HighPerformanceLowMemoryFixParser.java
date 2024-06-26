package com.jpm.fixparser;


import com.jpm.api.*;
import com.jpm.exception.MalformedFixMessageException;
import com.jpm.helper.NumericValue;
import com.jpm.policy.DefaultPolicy;

import static com.jpm.exception.ErrorMessages.*;

/**
 * The <strong>single responsibility of this class is to parse and delegate read operations to FixMessage</strong>
 * Parsing logic must happen here.During parsing
 *  ** any interim state needed must be delegated to {@link ParserState}
 *  ** all position keeping tasks must be delegated to {@link FixMessage}
 *
 *  This class handles the core logic involved in parsing a fix message and
 *  decisions need to be taken during ambiguity,
 *
 *  -- This class achieves the performance by doing all the parsing in one loop to retain O(N) complexity
 *
 *  Example usage of accessing parser
 * <blockquote>
 *  <pre>
 *     FixMessageParser fixMessageParser = FixMessageParserFactory.getFixMessageParser();
 *         String clientsFixMessage = "8=FIX.4.2\u00019=178\u000135=D\u000134=4\u000149=CLIENT12\u000152=20130615-19:30:00\u000156=BROKER12\u0001" +
 *                 "453=2\u0001" +
 *                 "448=JPMORGAN\u0001447=5\u0001452=6\u0001" +
 *                 "448=Client2\u0001447=D\u0001452=7\u0001" +
 *                 "55=0001.HK\u0001" +
 *                 "453=1\u0001" +
 *                 "448=BCAN\u0001447=1\u0001452=100\u0001" +
 *                 "10=037\u0001";
 *         fixMessageParser.parse(clientsFixMessage.getBytes());
 *
 *         System.out.println(fixMessageParser.getStringValueForTag(8));
 *         System.out.println(fixMessageParser.getStringValueForTag(35));
 *
 *         System.out.println(fixMessageParser.getStringValueForTag(448, 453, 0, 0));
 *  </pre>
 *  </blockquote>
 *
 *
 *
 * @author Sridhar S Shenoy
 */

public final class HighPerformanceLowMemoryFixParser implements FixMessageParser {

    private final NumericValue numericValue;
    private final NumericValue noOfRepeatGroupValue;
    private final FixTagLookup dictionary;
    private final int[][] tagMembersOfRepeatGroup;

    private final ParserState state;
    private final FixMessage fixMessage;
    private int lengthOfTagMemebers = 0;

    public HighPerformanceLowMemoryFixParser(ParsingPolicy policy) {
        numericValue = new NumericValue(policy);
        noOfRepeatGroupValue = new NumericValue(policy);
        dictionary = policy.dictionary();
        tagMembersOfRepeatGroup = new int[policy.maxNumberOfMemebersInRepeatingGroup()][2];
        state = new ParserState();
        fixMessage = new FixMessage(policy);
    }

    /**
     * This method is responsible to parse the incoming fix message
     * This has just one loop to maintain O(N) complexity
     *
     * @param msg
     * @throws MalformedFixMessageException
     */
    @Override
    public void parse(byte[] msg) throws MalformedFixMessageException {
        if (msg == null) {
            throwException(NULL_MESSAGE);
        }
        resetParserState();
        fixMessage.copyFixMessageToLocal(msg);

        for (int i = 0; i < fixMessage.getMessageLength(); i++) {
            parseBytesAt(i);
        }

        if (!state.parsedValue) {
            throwException(MISSING_DELIMITER);
        }
    }

    @Override
    public FixTagAccessor getReadOnlyClone() throws MalformedFixMessageException {
        int messageLength = fixMessage.getMessageLength();
        if (messageLength == 0) {
            throwException("Un-initialized object cannot be cloned");
        }
        /*
            The policy can be customised to ensure clone has minimal memory footprint.
         */
        HighPerformanceLowMemoryFixParser highPerformanceLowMemoryFixParser = new HighPerformanceLowMemoryFixParser(DefaultPolicy.getDefaultPolicy());
        byte[] newArray = new byte[messageLength];
        System.arraycopy(fixMessage.getRawFixMessage(),0,newArray,0,messageLength);
        highPerformanceLowMemoryFixParser.parse(newArray);
        return highPerformanceLowMemoryFixParser;
    }

    /*
        Each character can be part of tag or value
     */
    private void parseBytesAt(int index) throws MalformedFixMessageException {
        if (state.shouldParseForTag()) {
            parseTag(index);
        } else {
            parseValue(index);
        }
    }

    /*
        Parsing tag has two routes
         - continue to parse tag(as number)
         - tag is complete
     */
    private void parseTag(int index) throws MalformedFixMessageException {
        if (fixMessage.hasCompletedParsingTag(index)) {
            handleTagParsingCompletionTasks(index);
        } else {
            parseNumber(fixMessage.byteAt(index), numericValue);
        }
    }

    /*
        parsing value is tricky
        - it can be the beginning of repeatGroup in which case we need to store the value
        - end of simple tag
        - end of repeatGroup last tag
        - still in repeat group
        - end of inner repeat group
        - handle case where all tags are not mandatory
     */
    private void parseValue(int index) throws MalformedFixMessageException {
        if (fixMessage.hasCompletedParsingTagValuePair(index)) {
            handleTagValueParsingCompletionTasks(index);
        } else if (state.isFirstTagOfRepeatingGroup()) {
            parseNumber(fixMessage.getRawFixMessage()[index], noOfRepeatGroupValue);
        }
    }

    private void handleTagParsingCompletionTasks(int index) throws MalformedFixMessageException {
        int tag = numericValue.getInt();
        //-- handle the case when repeating group is completed and fresh tag is being parsed
        if (!dictionary.isTagMemberOfRepeatGroup(tag, state.repeatGroupBeginTag)) {
            state.inRepeatGroup = false;
        }

        //-- handle the case when tag is beginning of repeating group
        boolean repeatingGroupBeginTag = dictionary.isRepeatingGroupBeginTag(tag);
        if (!state.inRepeatGroup && repeatingGroupBeginTag) {
            state.inRepeatGroup = true;
            state.parsingRepeatingGroupCount = true;
            state.repeatGroupBeginTag = tag;
            state.currentRepeatGroupTagIndex = fixMessage.getRepeatGroupIndexer().addBeginTagAndGetIndex(tag);
            fixMessage.getRepeatGroupIndexer().addValueIndex(state.currentRepeatGroupTagIndex, index + 1);
            lengthOfTagMemebers = dictionary.copyTagMembersOfRepeatGroupTo(tag, tagMembersOfRepeatGroup);
        }

        //-- When we have moved past first repeating marker. Reset the flags
        if (!state.inRepeatGroup && !repeatingGroupBeginTag) {
            state.parsingRepeatingGroupCount = false;
        }

        if (state.isParsingRepeatingGroup()) {
            handleRepeatGroupTag(tag, index);
        } else {
            state.currentTagIndex = addAndGetIndex(tag);
            fixMessage.getFixMessageIndexer().addValueIndex(state.currentTagIndex, index + 1);
        }

        state.parsingNextTag = false;
        state.parsedValue = false;
    }


    private void handleRepeatGroupTag(int tag, int index) {
        if (alreadyCheckedRepeatGroup(tag)) {
            state.countOfRepeatGroupParsed++;
            resetRepeatingGroupMembers(tag);
        }
        state.currentRepeatGroupTagIndex = fixMessage.getRepeatGroupIndexer().addGroupMemberTagForRepeatBeginTag(tag, state.currentRepeatGroupTagIndex);
        fixMessage.getRepeatGroupIndexer().addValueIndex(state.currentRepeatGroupTagIndex, index + 1);
        unsetRepeatingMembers(tag);
    }

    private void handleTagValueParsingCompletionTasks(int index) throws MalformedFixMessageException {
        int valueLength = index - (state.inRepeatGroup ?
                fixMessage.getRepeatGroupIndexer().getValueIndexForRepeatTagIndex(state.currentRepeatGroupTagIndex)
                : fixMessage.getFixMessageIndexer().getValueIndexForTag(numericValue.getInt()));

        if (valueLength == 0) {
            throwException(MISSING_VALUE);
        }

        if (state.inRepeatGroup) {
            fixMessage.getRepeatGroupIndexer().addValueLength(state.currentRepeatGroupTagIndex, valueLength);
        } else {
            fixMessage.getFixMessageIndexer().addValueLength(state.currentTagIndex, valueLength);
        }

        state.parsingNextTag = true;
        state.parsedValue = true;
        numericValue.reset();
    }

    private void resetRepeatingGroupMembers(int tag) {
        for (int i = 0; i < lengthOfTagMemebers; i++) {
            tagMembersOfRepeatGroup[i][1] = -1;
        }
    }

    private boolean alreadyCheckedRepeatGroup(int tag) {
        for (int i = 0; i < lengthOfTagMemebers; i++) {
            if (tagMembersOfRepeatGroup[i][0] == tag && tagMembersOfRepeatGroup[i][1] == 1) {
                return true;
            }
        }
        return false;
    }

    private void unsetRepeatingMembers(int tag) {
        for (int i = 0; i < lengthOfTagMemebers; i++) {
            if (tagMembersOfRepeatGroup[i][0] == tag) {
                tagMembersOfRepeatGroup[i][1] = 1;
                return;
            }
        }
    }

    private void parseNumber(byte b, NumericValue value) throws MalformedFixMessageException {
        value.append(b);
        validateFixTag(MALFORMED_TAG_VALUE_PAIR);
    }

    private int addAndGetIndex(int tag) throws MalformedFixMessageException {
        validateFixTag(INCORRECT_TAG);
        return fixMessage.getFixMessageIndexer().addTagAndGetIndex(tag);
    }

    private void validateFixTag(String errorTest) throws MalformedFixMessageException {
        if (numericValue.isInvalid()) {
            throwException(errorTest);
        }
    }

    private static void throwException(String missingTag) throws MalformedFixMessageException {
        throw new MalformedFixMessageException(missingTag);
    }

    @Override
    public String getStringValueForTag(int tag) {
        return fixMessage.getStringValueForTag(tag);
    }

    @Override
    public byte[] getByteValueForTag(int tag) {
        return fixMessage.getByteValueForTag(tag);
    }

    @Override
    public int copyByteValuesToArray(int tag, byte[] output) {
        return fixMessage.copyByteValuesToArray(tag,output);
    }

    @Override
    public byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
       return fixMessage.getByteValueForTag(tag,repeatBeginTag,instance,instanceInMessage);
    }

    @Override
    public String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage) {
        return fixMessage.getStringValueForTag(tag, repeatBeginTag, instance, instanceInMessage);
    }

    @Override
    public int copyByteValuesToArray(int tag, int repeatBeginTag, int instance, int instanceInMessage, byte[] output) {
        return fixMessage.copyByteValuesToArray(tag,repeatBeginTag,instance,instanceInMessage,output);
    }

    private void resetParserState() {
        numericValue.reset();
        noOfRepeatGroupValue.reset();
        fixMessage.reset();
        state.reset();
    }
}
