package com.jpm.dictionary;

import com.jpm.api.FixTagLookup;

import java.util.Arrays;

/**
 * This is a mocked up Dictionary. In its ideal state this should load from FIX xml files to identify the tag and its associated
 * Repeating groups.
 *
 * For the sake pf demonstration only RepeatGroup 453 has been hardcoded.
 *
 * @author Sridhar S Shenoy
 */
public class DefaultFixDictionary implements FixTagLookup {
    private int[] repeatGroupBeginTag;
    private int[][] repeatGroupMembers;

    public DefaultFixDictionary(int maxFixTagSupported, int maxNumberOfRepeatingGroupAllowed) {
        repeatGroupBeginTag = new int[maxFixTagSupported];
        repeatGroupMembers = new int[maxNumberOfRepeatingGroupAllowed][20];
        Arrays.fill(repeatGroupBeginTag, -1);

        populateMatrix();
    }

    private void populateMatrix() {
        //-- Mocking the value for simplicity
        //-- In production this will be populated using fix dictionary xml
        repeatGroupBeginTag[453] = 0; //-- Link to repeatGroupMembers

        repeatGroupMembers[0][0] = 3; //-- Number of members
        repeatGroupMembers[0][1] = 447;
        repeatGroupMembers[0][2] = 448;
        repeatGroupMembers[0][3] = 452;
    }

    @Override
    public boolean isRepeatingGroupBeginTag(int tag) {
        return repeatGroupBeginTag[tag] != -1;
    }

    @Override
    public boolean isTagMemberOfRepeatGroup(int tag, int beginTag) {
        return isRepeatingGroupBeginTag(beginTag) && contains(repeatGroupBeginTag[beginTag],tag);
    }

    @Override
    public int copyTagMembersOfRepeatGroupTo(int repeatBeginTag, int output[][]) {
        int index = repeatGroupBeginTag[repeatBeginTag];
        int length = repeatGroupMembers[index][0];
        for (int i = 1; i<= length; i++){
            output[i-1][0]= repeatGroupMembers[index][i];
            output[i-1][1]= -1;
        }
        return length;
    }

    private boolean contains(int index, int tag) {
        for (int i = 1; i <= repeatGroupMembers[index][0]; i++) {
            if(tag == repeatGroupMembers[index][i]){
                return true;
            }
        }
        return false;
    }
}
