package com.jpm.interfacce;

import com.jpm.exception.MalformedFixMessageException;

public interface FixMessageParser {
    void parse(byte[] msg) throws MalformedFixMessageException;
}
