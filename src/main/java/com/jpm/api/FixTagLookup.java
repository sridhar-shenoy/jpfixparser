package com.jpm.api;

public interface FixTagLookup {
    boolean isRepeatingGroupBeginTag(int tag);
    boolean isTagMemberOfRepeatGroup(int tag,int repeatGroupBeginTag);
    int copyTagMembersOfRepeatGroupTo(int repeatGroupBeginTag, int[][] output);
}