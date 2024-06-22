package com.jpm.fixparser;

import com.jpm.api.Conformable;
import com.jpm.api.FixMessageParser;
import com.jpm.policy.DefaultPolicy;

public class FixMessageParserFactory {

    public static FixMessageParser getFixMessageParser() {
        return new HighPerformanceLowMemoryFixParser(DefaultPolicy.getDefaultPolicy());
    }

    public static FixMessageParser getFixMessageParser(Conformable policy) {
        return new HighPerformanceLowMemoryFixParser(policy);
    }
}
