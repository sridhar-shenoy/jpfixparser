package com.jpm.api;

public interface Conformable {
    int maxNumberOfTagValuePairPerMessage();
    int maxFixTagSupported();
    char delimiter();
    int maxLengthOfFixMessage();
    int maxNumberOfRepeatingGroupAllowed();
    FixTagLookup dictionary();
}
