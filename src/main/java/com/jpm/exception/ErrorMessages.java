package com.jpm.exception;

public final class ErrorMessages {
    private ErrorMessages(){
    }
    public static final String MALFORMED_TAG_VALUE_PAIR = "Unable to parse fix message due to malformed tag value pair";
    public static final String INCORRECT_TAG = "Value of Tag is Incorrect";
    public static final String MISSING_VALUE = "Unable to parse fix message due to missing value";
    public static final String MISSING_DELIMITER = "Unable to parse due to missing delimiter";
    public static final String NULL_MESSAGE = "Input message is Null";
}
