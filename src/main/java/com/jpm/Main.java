package com.jpm;

import com.jpm.api.FixMessageParser;
import com.jpm.api.FixTagAccessor;
import com.jpm.exception.MalformedFixMessageException;
import com.jpm.fixparser.FixMessageParserFactory;
import com.jpm.policy.DefaultPolicy;

public class Main {

    public static void main(String[] args) throws MalformedFixMessageException {
        //-- Default parser
        FixMessageParser fixMessageParser = FixMessageParserFactory.getFixMessageParser();
        String clientsFixMessage = "8=FIX.4.2\u00019=178\u000135=D\u000134=4\u000149=CLIENT12\u000152=20130615-19:30:00\u000156=BROKER12\u0001" +
                "453=2\u0001" +
                "448=JPMORGAN\u0001447=5\u0001452=6\u0001" +
                "448=Client2\u0001447=D\u0001452=7\u0001" +
                "55=0001.HK\u0001" +
                "453=1\u0001" +
                "448=BCAN\u0001447=1\u0001452=100\u0001" +
                "10=037\u0001";
        fixMessageParser.parse(clientsFixMessage.getBytes());

        System.out.println(fixMessageParser.getStringValueForTag(8));
        System.out.println(fixMessageParser.getStringValueForTag(35));

        System.out.println(fixMessageParser.getStringValueForTag(448, 453, 0, 0));


        //-- Immutable Readonly clone with low memory footprint
        FixTagAccessor readOnlyClone = fixMessageParser.getReadOnlyClone();
        System.out.println(readOnlyClone.getStringValueForTag(35));

        //-- Parser with Custom Policy
        FixMessageParser fixMessageParser2 = FixMessageParserFactory.getFixMessageParser(new MyPolicy(100, 10000, 1000, '|', 100));
        fixMessageParser2.parse("8=FIX.4.2|9=148|35=D|34=1000|10=092|".getBytes());

        System.out.println(fixMessageParser2.getStringValueForTag(34));
    }

    private static class MyPolicy extends DefaultPolicy {
        public MyPolicy(int maxNumberOfTagValuePair, int maxFixTags, int maxLengthOfFixMsg, char fixDelimiter, int maxRepeatingGroup) {
            super(maxNumberOfTagValuePair, maxFixTags, maxLengthOfFixMsg, fixDelimiter, maxRepeatingGroup);
        }
    }
}
