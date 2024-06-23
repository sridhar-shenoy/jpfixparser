package com.jpm.fixparser;

import com.jpm.api.ParsingPolicy;
import com.jpm.api.FixMessageParser;
import com.jpm.policy.DefaultPolicy;

public final class FixMessageParserFactory {

    private FixMessageParserFactory() {
    }

    public static FixMessageParser getFixMessageParser() {
        return new HighPerformanceLowMemoryFixParser(DefaultPolicy.getDefaultPolicy());
    }

    public static FixMessageParser getFixMessageParser(ParsingPolicy policy) {
        return new HighPerformanceLowMemoryFixParser(policy);
    }
}
