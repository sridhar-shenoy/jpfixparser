package com.jpm.helper;

import com.jpm.api.ParsingPolicy;

/**
 * To not use objects, we parse the tag's integer equivalent by cumulative calculating the tag's numeric value
 * Independently this class cannot be used.
 *
 * <strong>The only responsibility is to maintain the numeric state of the tag or value </strong>
 * Caller of this class must maintain the logic to call to achieve the desired goal.
 *
 * @author Sridhar S Shenoy
 */
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
