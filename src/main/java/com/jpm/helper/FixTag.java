package com.jpm.helper;

public final class FixTag {
    public static final int BASE = 10;
    private final int maxTagSupported;

    public FixTag(int maxTagSupported) {
        this.maxTagSupported = maxTagSupported;
    }

    private int tag = 0;

    public void build(byte b){
        tag = (tag * BASE) + (b - '0');
    }

    public boolean isInvalid(){
        return tag <= 0 || tag > maxTagSupported;
    }

    public int getTag(){
        return tag;
    }

    public void reset() {
        tag = 0;
    }
}
