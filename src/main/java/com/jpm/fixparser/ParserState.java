package com.jpm.fixparser;

final class ParserState {
    int currentTagIndex = 0;
    int currentRepeatGroupTagIndex = 0;
    boolean inRepeatGroup = false;
    boolean parsingNextTag = true;
    boolean parsedValue = false;
    short countOfRepeatGroupParsed = 0;
    boolean parsingRepeatingGroupCount = false;
    int repeatGroupBeginTag = 0;

    void reset() {
        currentTagIndex = 0;
        currentRepeatGroupTagIndex = 0;
        inRepeatGroup = false;
        parsingNextTag = true;
        parsedValue = false;
        countOfRepeatGroupParsed = 0;
        parsingRepeatingGroupCount = false;
        repeatGroupBeginTag = 0;
    }

    public boolean shouldParseForTag() {
        return parsingNextTag;
    }

    boolean isFirstTagOfRepeatingGroup() {
        return inRepeatGroup && parsingRepeatingGroupCount;
    }

    public boolean isParsingRepeatingGroup() {
        return inRepeatGroup;
    }
}
