package com.jpm.helper;

public class FixTag {
    private final int maxTagSupported;

    public FixTag(int maxTagSupported) {
        this.maxTagSupported = maxTagSupported;
    }

    private int tag = 0;

    public void build(byte b){
        tag = (tag * 10) + (b - '0');
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
