package com.jpm.interfacce;

import com.jpm.exception.MalformedFixMessageException;

public interface FixMessageParsable {
    void parse(byte[] msg) throws MalformedFixMessageException;
}
