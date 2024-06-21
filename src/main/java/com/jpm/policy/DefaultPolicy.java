package com.jpm.policy;

import com.jpm.interfacce.Conformable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPolicy implements Conformable {

    private final int maxNumberOfTagValuePairPerMessage;
    private final int maxFixTagSupported;
    private final Map<Integer, List<Integer>> fixRepeatingGroupLookupMap;
    private final char delimiter;
    private final int maxLengthOfFixMessage;

    public DefaultPolicy(int maxNumberOfTagValuePair, int maxFixTags, int maxLengthOfFixMsg, char fixDelimiter) {
        this.fixRepeatingGroupLookupMap = new HashMap<>();
        maxNumberOfTagValuePairPerMessage = maxNumberOfTagValuePair;
        maxFixTagSupported = maxFixTags;
        setRepeatingGroups();
        delimiter = fixDelimiter;
        maxLengthOfFixMessage = maxLengthOfFixMsg;
    }

    /**
     * For the sake of simplicity, This code only sets a couple of repeating groups
     * for production use this array must be populated based on the actual fix dictionary for fix version
     */
    private void setRepeatingGroups() {
        fixRepeatingGroupLookupMap.put(453, Arrays.asList(448, 457, 442, 2367));
    }

    @Override
    public int getMaxNumberOfTagValuePairPerMessage() {
        return maxNumberOfTagValuePairPerMessage;
    }

    @Override
    public int getMaxFixTagSupported() {
        return maxFixTagSupported;
    }

    @Override
    public char getFixDelimiter() {
        return delimiter;
    }

    @Override
    public int maxLengthOfFixMessage() {
        return maxLengthOfFixMessage;
    }

    @Override
    public boolean isRepeatingGroupBeginTag(int tag) {
        return false;
    }
}
