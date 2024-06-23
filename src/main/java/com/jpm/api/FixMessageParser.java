package com.jpm.api;

import com.jpm.exception.MalformedFixMessageException;

/**
 *
 * This is the public interface available to clients.
 * All operations on you wish to expose to clients must be available here
 *
 * @author Sridhar S Shenoy
 */
public interface FixMessageParser extends FixTagAccessor {

    void parse(byte[] msg) throws MalformedFixMessageException;

    FixTagAccessor getReadOnlyClone() throws MalformedFixMessageException;
}
