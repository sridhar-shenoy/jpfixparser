package com.jpm.helper;

import com.jpm.api.Conformable;

public class RepeatingGroupHandler {
    public static final int TAG_NUMBER = 0;
    public static final int TAG_OCCURENCE_WITHIN_GROUP = 1;
    public static final int REPEAT_GROUP_OCCURRENCE = 2;
    public static final int VALUE_LENGTH = 3;
    private final int[][] repeatingGroupOccurrenceIndex;
    private int currentArrayIndex = 0;

    public RepeatingGroupHandler(Conformable policy) {
        repeatingGroupOccurrenceIndex = new int[policy.maxNumberOfRepeatingGroupAllowed()][4];
    }

    public int getOccurrenceIndexWithinRepeatingGroup(int tag, int occurrenceIndexInMessage) {
        return -1;
    }

    public int addBeginTagAndGetIndex(int repeatGroupBeginTag) {
        int indexOfBeginTagWithinMessage = getLastOccurrenceIndexOfBeginTagWithinMessage(repeatGroupBeginTag);
        repeatingGroupOccurrenceIndex[currentArrayIndex][TAG_NUMBER] = repeatGroupBeginTag;
        repeatingGroupOccurrenceIndex[currentArrayIndex][TAG_OCCURENCE_WITHIN_GROUP] = 0; // Begin tags will always have one occurrence within a group
        repeatingGroupOccurrenceIndex[currentArrayIndex][REPEAT_GROUP_OCCURRENCE] = ++indexOfBeginTagWithinMessage;
        return currentArrayIndex++;
    }

    public int addGroupMemberTagForRepeatBeginTag(int repeatGroupMemberTag, int repeatGroupBeginTag) {
        int occurrenceIndexOfBeginTagWithinMessage = getLastOccurrenceIndexOfBeginTagWithinMessage(repeatingGroupOccurrenceIndex[repeatGroupBeginTag][0]);
        int occurrenceIndexOfTagWithinGroup = getLastOccurrenceIndexOfTagWithinGroup(repeatGroupMemberTag, repeatGroupBeginTag);
        repeatingGroupOccurrenceIndex[currentArrayIndex][TAG_NUMBER] = repeatGroupMemberTag;
        repeatingGroupOccurrenceIndex[currentArrayIndex][TAG_OCCURENCE_WITHIN_GROUP] = ++occurrenceIndexOfTagWithinGroup;
        repeatingGroupOccurrenceIndex[currentArrayIndex][REPEAT_GROUP_OCCURRENCE] = occurrenceIndexOfBeginTagWithinMessage;
        return currentArrayIndex++;
    }

    public int getLastOccurrenceIndexOfBeginTagWithinMessage(int repeatingGroupBeginTag) {
        int currentIndex = -1;
        for (int i = 0; i < currentArrayIndex; i++) {
            if (repeatingGroupOccurrenceIndex[i][TAG_NUMBER] == repeatingGroupBeginTag) {
                currentIndex = repeatingGroupOccurrenceIndex[i][REPEAT_GROUP_OCCURRENCE];
            }
        }
        return currentIndex;
    }

    public int getLastOccurrenceIndexOfTagWithinGroup(int repeatGroupMemberTag, int beginTagIndex) {
        int currentGroupOccurrenceIndex = -1;
        for (int i = 0; i < currentArrayIndex; i++) {
            if (repeatingGroupOccurrenceIndex[i][TAG_NUMBER] == repeatGroupMemberTag
                    && repeatingGroupOccurrenceIndex[i][REPEAT_GROUP_OCCURRENCE] == repeatingGroupOccurrenceIndex[beginTagIndex][REPEAT_GROUP_OCCURRENCE]) {
                currentGroupOccurrenceIndex = repeatingGroupOccurrenceIndex[i][TAG_OCCURENCE_WITHIN_GROUP];
            }
        }
        return currentGroupOccurrenceIndex;
    }

    public void addValueLength(int currentRepeatGroupTagIndex, int valueLength) {
        repeatingGroupOccurrenceIndex[currentRepeatGroupTagIndex][VALUE_LENGTH] = valueLength;
    }

    public int getValueIndexForTag(int currentTagIndex) {
        return repeatingGroupOccurrenceIndex[currentTagIndex][VALUE_LENGTH];
    }
}
