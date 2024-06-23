package com.jpm.api;

/**
 * Implementor of this class is given the option to tune {@link FixMessageParser}
 * Any parameter needed by the parser that clients could tune must be defined here
 *
 * @author Sridhar S Shenoy
 */
public interface ParsingPolicy {
    int maxNumberOfTagValuePairPerMessage();
    int maxFixTagSupported();
    char delimiter();
    int maxLengthOfFixMessage();
    int maxNumberOfRepeatingGroupAllowed();
    FixTagLookup dictionary();
    int maxNumberOfMemebersInRepeatingGroup();
}
