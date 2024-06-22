package com.jpm.api;

import com.jpm.exception.MalformedFixMessageException;

public interface Parsable {
    void parse(byte[] msg) throws MalformedFixMessageException;
}
