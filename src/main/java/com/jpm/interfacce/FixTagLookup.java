package com.jpm.interfacce;

public interface FixTagLookup {
    boolean isRepeatingGroupBeginTag(int tag);
    boolean isTagMemberOfRepeatGroup(int tag,int repeatGroupBeginTag);
}
