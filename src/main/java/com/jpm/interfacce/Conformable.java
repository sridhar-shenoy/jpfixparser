package com.jpm.interfacce;

public interface Conformable {
    int getMaxNumberOfTagValuePairPerMessage();
    int getMaxFixTagSupported();
    char getFixDelimiter();
    int maxLengthOfFixMessage();
    boolean isRepeatingGroupBeginTag(int tag);
}
