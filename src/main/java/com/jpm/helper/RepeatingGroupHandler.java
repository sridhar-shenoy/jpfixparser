package com.jpm.helper;

import com.jpm.interfacce.Conformable;

public class RepeatingGroupHandler {
    private final int[][] repeatingGroupOccurrenceIndex;
    private final Conformable policy;
    private int currentArrayIndex = 0;

    public RepeatingGroupHandler(Conformable policy) {
        this.policy = policy;
        repeatingGroupOccurrenceIndex = new int[policy.getMaxNumberOfRepeatingGroupAllowed()][3];
    }

    public int getOccurrenceIndexWithinRepeatingGroup(int tag, int occurrenceIndexInMessage) {
        return -1;
    }

    public int addBeginTag(int repeatGroupBeginTag) {
        int occurrenceIndexOfBeginTagWithinMessage = getLastOccurrenceIndexOfBeginTagWithinMessage(repeatGroupBeginTag);
        repeatingGroupOccurrenceIndex[currentArrayIndex][0] = repeatGroupBeginTag;
        repeatingGroupOccurrenceIndex[currentArrayIndex][1] = 0; // Begin tags will always have one occurrence within a group
        repeatingGroupOccurrenceIndex[currentArrayIndex][2] = ++occurrenceIndexOfBeginTagWithinMessage;
        currentArrayIndex++;
        return occurrenceIndexOfBeginTagWithinMessage;
    }

    public void addGroupMemberTagForRepeatBeginTag(int repeatGroupMemberTag,int repeatGroupBeginTag) {
        int occurrenceIndexOfBeginTagWithinMessage = getLastOccurrenceIndexOfBeginTagWithinMessage(repeatGroupBeginTag);
        int occurrenceIndexOfTagWithinGroup = getLastOccurrenceIndexOfTagWithinGroup(repeatGroupMemberTag, occurrenceIndexOfBeginTagWithinMessage);
        repeatingGroupOccurrenceIndex[currentArrayIndex][0] = repeatGroupMemberTag;
        repeatingGroupOccurrenceIndex[currentArrayIndex][1] = ++occurrenceIndexOfTagWithinGroup;
        repeatingGroupOccurrenceIndex[currentArrayIndex][2] = occurrenceIndexOfBeginTagWithinMessage;
        currentArrayIndex++;
    }

    public int getLastOccurrenceIndexOfBeginTagWithinMessage(int repeatingGroupBeginTag) {
        int currentIndex = -1;
        for (int i = 0; i < currentArrayIndex; i++) {
            if(repeatingGroupOccurrenceIndex[i][0] == repeatingGroupBeginTag){
                currentIndex = repeatingGroupOccurrenceIndex[i][2];
            }
        }
        return currentIndex;
    }

    public int getLastOccurrenceIndexOfTagWithinGroup(int repeatGroupMemberTag, int occurrenceIndexOfBeginTagWithinMessage) {
        int currentGroupOccurrenceIndex = -1;
        for (int i = 0; i < currentArrayIndex; i++) {
            if(repeatingGroupOccurrenceIndex[i][0] == repeatGroupMemberTag && repeatingGroupOccurrenceIndex[i][2] == occurrenceIndexOfBeginTagWithinMessage ){
                currentGroupOccurrenceIndex = repeatingGroupOccurrenceIndex[i][1];
            }
        }
        return currentGroupOccurrenceIndex;
    }
}
