package com.jpm;

import com.jpm.api.FixMessageParser;
import com.jpm.api.FixTagAccessor;
import com.jpm.exception.MalformedFixMessageException;
import com.jpm.fixparser.FixMessageParserFactory;
import com.jpm.policy.DefaultPolicy;

public class Main {

     private static class MyPolicy extends DefaultPolicy {

         public MyPolicy(int maxNumberOfTagValuePair, int maxFixTags, int maxLengthOfFixMsg, char fixDelimiter, int maxRepeatingGroup) {
             super(maxNumberOfTagValuePair, maxFixTags, maxLengthOfFixMsg, fixDelimiter, maxRepeatingGroup);
         }
     }
    public static void main(String[] args) throws MalformedFixMessageException {
         //-- Default parser
        FixMessageParser fixMessageParser = FixMessageParserFactory.getFixMessageParser();
        fixMessageParser.parse("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092\u0001".getBytes());
        System.out.println(fixMessageParser.getStringValueForTag(8));
        System.out.println(fixMessageParser.getStringValueForTag(35));

        //-- Immutable Readonly close
        FixTagAccessor readOnlyClone = fixMessageParser.getReadOnlyClone();
        System.out.println(readOnlyClone.getStringValueForTag(35));

        //-- Parser with Custom Policy
        FixMessageParser fixMessageParser2 = FixMessageParserFactory.getFixMessageParser(new MyPolicy(100,10000,1000,'|',100));
    }
}
