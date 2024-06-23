package com.jpm.api;

/**
 * {@link FixMessageParser} would need to lookup about fix tags and inspect it based on the FIX standards
 * <strong>This implementor of this class has the responsibility to adhere to Strict FIX Standards</strong>
 *
 * @author Sridhar S Shenoy
 */
public interface FixTagLookup {
    boolean isRepeatingGroupBeginTag(int tag);
    boolean isTagMemberOfRepeatGroup(int tag,int repeatGroupBeginTag);
    int copyTagMembersOfRepeatGroupTo(int repeatGroupBeginTag, int[][] output);
}
