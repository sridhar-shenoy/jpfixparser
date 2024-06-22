package com.jpm.api;

import com.jpm.exception.MalformedFixMessageException;

public interface FixMessageParser extends FixTagAccessor {

    void parse(byte[] msg) throws MalformedFixMessageException;

    FixTagAccessor getReadOnlyClone() throws MalformedFixMessageException;
}
