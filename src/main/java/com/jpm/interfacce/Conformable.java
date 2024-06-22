package com.jpm.interfacce;

public interface Conformable {
    int maxNumberOfTagValuePairPerMessage();
    int maxFixTagSupported();
    char delimiter();
    int maxLengthOfFixMessage();
    int maxNumberOfRepeatingGroupAllowed();
    FixTagLookup dictionary();
}
