package com.jpm.policy;

import com.jpm.api.ParsingPolicy;
import com.jpm.dictionary.DefaultFixDictionary;
import com.jpm.api.FixTagLookup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPolicy implements ParsingPolicy {

    private final int maxNumberOfTagValuePairPerMessage;
    private final int maxFixTagSupported;
    private final Map<Integer, List<Integer>> fixRepeatingGroupLookupMap;
    private final char delimiter;
    private final int maxLengthOfFixMessage;
    private final int maxNumberOfRepeatingGroup;
    private final FixTagLookup dictionary;

    public DefaultPolicy(int maxNumberOfTagValuePair, int maxFixTags, int maxLengthOfFixMsg, char fixDelimiter, int maxRepeatingGroup,FixTagLookup dictionary) {
        this.fixRepeatingGroupLookupMap = new HashMap<>();
        maxNumberOfTagValuePairPerMessage = maxNumberOfTagValuePair;
        maxFixTagSupported = maxFixTags;
        delimiter = fixDelimiter;
        maxLengthOfFixMessage = maxLengthOfFixMsg;
        maxNumberOfRepeatingGroup = maxRepeatingGroup;
        this.dictionary = dictionary;
        populateRepeatingGroups();
    }

    /**
     * For the sake of simplicity, This code only sets a couple of repeating groups
     * for production use this array must be populated based on the actual fix dictionary for fix version
     */
    private void populateRepeatingGroups() {
        fixRepeatingGroupLookupMap.put(453, Arrays.asList(448, 457, 442, 2367));
    }

     @Override
    public int maxNumberOfTagValuePairPerMessage() {
        return maxNumberOfTagValuePairPerMessage;
    }

    @Override
    public int maxFixTagSupported() {
        return maxFixTagSupported;
    }

    @Override
    public char delimiter() {
        return delimiter;
    }

    @Override
    public int maxLengthOfFixMessage() {
        return maxLengthOfFixMessage;
    }

    @Override
    public int maxNumberOfRepeatingGroupAllowed() {
        return maxNumberOfRepeatingGroup;
    }

    @Override
    public FixTagLookup dictionary() {
        return new DefaultFixDictionary(this.maxFixTagSupported(), this.maxNumberOfRepeatingGroupAllowed());
    }

    @Override
    public int maxNumberOfMemebersInRepeatingGroup() {
        return 10;
    }

    public static ParsingPolicy getDefaultPolicy(){
        return new DefaultPolicy(1000,1000,1000,'\u0001',100,new DefaultFixDictionary(1000,100));
    }

    public static ParsingPolicy getCustomPolicy(int maxNumberOfTagValuePair, int maxFixTags, int maxLengthOfFixMsg, char fixDelimiter, int maxRepeatingGroup){
        return new DefaultPolicy(maxNumberOfTagValuePair, maxFixTags, maxLengthOfFixMsg, fixDelimiter, maxRepeatingGroup, new DefaultFixDictionary(maxFixTags,maxRepeatingGroup));
    }
}
