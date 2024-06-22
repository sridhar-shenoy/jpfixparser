package com.jpm.helper;

import com.jpm.interfacce.Conformable;

public class RepeatingGroupHandler {
    private final int[][] repeatingGroupOccurrenceIndex;
    private int currentArrayIndex = 0;

    public RepeatingGroupHandler(Conformable policy) {
        repeatingGroupOccurrenceIndex = new int[policy.maxNumberOfRepeatingGroupAllowed()][3];
    }

    public int getOccurrenceIndexWithinRepeatingGroup(int tag, int occurrenceIndexInMessage) {
        return -1;
    }

    public int addBeginTagAndGetIndex(int repeatGroupBeginTag) {
        int indexOfBeginTagWithinMessage = getLastOccurrenceIndexOfBeginTagWithinMessage(repeatGroupBeginTag);
        repeatingGroupOccurrenceIndex[currentArrayIndex][0] = repeatGroupBeginTag;
        repeatingGroupOccurrenceIndex[currentArrayIndex][1] = 0; // Begin tags will always have one occurrence within a group
        repeatingGroupOccurrenceIndex[currentArrayIndex][2] = ++indexOfBeginTagWithinMessage;
        return currentArrayIndex++;
    }

    public int addGroupMemberTagForRepeatBeginTag(int repeatGroupMemberTag, int repeatGroupBeginTag) {
        int occurrenceIndexOfBeginTagWithinMessage = getLastOccurrenceIndexOfBeginTagWithinMessage(repeatingGroupOccurrenceIndex[repeatGroupBeginTag][0]);
        int occurrenceIndexOfTagWithinGroup = getLastOccurrenceIndexOfTagWithinGroup(repeatGroupMemberTag, repeatGroupBeginTag);
        repeatingGroupOccurrenceIndex[currentArrayIndex][0] = repeatGroupMemberTag;
        repeatingGroupOccurrenceIndex[currentArrayIndex][1] = ++occurrenceIndexOfTagWithinGroup;
        repeatingGroupOccurrenceIndex[currentArrayIndex][2] = occurrenceIndexOfBeginTagWithinMessage;
        return currentArrayIndex++;
    }

    public int getLastOccurrenceIndexOfBeginTagWithinMessage(int repeatingGroupBeginTag) {
        int currentIndex = -1;
        for (int i = 0; i < currentArrayIndex; i++) {
            if (repeatingGroupOccurrenceIndex[i][0] == repeatingGroupBeginTag) {
                currentIndex = repeatingGroupOccurrenceIndex[i][2];
            }
        }
        return currentIndex;
    }

    public int getLastOccurrenceIndexOfTagWithinGroup(int repeatGroupMemberTag, int beginTagIndex) {
        int currentGroupOccurrenceIndex = -1;
        for (int i = 0; i < currentArrayIndex; i++) {
            if (repeatingGroupOccurrenceIndex[i][0] == repeatGroupMemberTag
                    && repeatingGroupOccurrenceIndex[i][2] == repeatingGroupOccurrenceIndex[beginTagIndex][2]) {
                currentGroupOccurrenceIndex = repeatingGroupOccurrenceIndex[i][1];
            }
        }
        return currentGroupOccurrenceIndex;
    }
}
