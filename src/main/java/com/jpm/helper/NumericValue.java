package com.jpm.helper;

import com.jpm.api.ParsingPolicy;

public final class NumericValue {
    public static final int BASE = 10;
    private final int maxTagSupported;

    public NumericValue(ParsingPolicy policy) {
        this.maxTagSupported = policy.maxFixTagSupported();
    }

    private int number = 0;

    public void append(byte b){
        number = (number * BASE) + (b - '0');
    }

    public boolean isInvalid(){
        return number <= 0 || number > maxTagSupported;
    }

    public int getInt(){
        return number;
    }

    public void reset() {
        number = 0;
    }
}
