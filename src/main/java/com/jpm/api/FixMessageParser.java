package com.jpm.api;

import com.jpm.exception.MalformedFixMessageException;

public interface FixMessageParser {
    void parse(byte[] msg) throws MalformedFixMessageException;
}
