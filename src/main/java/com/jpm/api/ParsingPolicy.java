package com.jpm.api;

public interface ParsingPolicy {
    int maxNumberOfTagValuePairPerMessage();
    int maxFixTagSupported();
    char delimiter();
    int maxLengthOfFixMessage();
    int maxNumberOfRepeatingGroupAllowed();
    FixTagLookup dictionary();
    int maxNumberOfMemebersInRepeatingGroup();
}
