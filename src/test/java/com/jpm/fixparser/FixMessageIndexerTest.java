package com.jpm.fixparser;

import com.jpm.helper.FixMessageIndexer;
import com.jpm.policy.DefaultPolicy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixMessageIndexerTest {

    private FixMessageIndexer fixMessageIndexer;

    @Before
    public void setUp() throws Exception {
        fixMessageIndexer = new FixMessageIndexer(DefaultPolicy.getDefaultPolicy());
    }

    @Test
    public void addTagToIndexAndRetrieveLength() {
        //8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092\u0001

        //-- Add tag 8 at 0, Value starts at index 2 with length 7
        int index = fixMessageIndexer.addTagAndGetIndex(8);
        fixMessageIndexer.addValueIndex(index,2 );
        fixMessageIndexer.addValueLength(index, 7);

        //-- Add tag 9 at 10, Value starts at index 12 with length 3
        index = fixMessageIndexer.addTagAndGetIndex(9);
        fixMessageIndexer.addValueIndex(index,12 );
        fixMessageIndexer.addValueLength(index, 3);

        //-- Add tag 35 at 18, Value starts at index 21 with length 4
        index = fixMessageIndexer.addTagAndGetIndex(35);
        fixMessageIndexer.addValueIndex(index,21 );
        fixMessageIndexer.addValueLength(index, 4);

        assertEquals(0,fixMessageIndexer.getIndexForTag(8));
        assertEquals(2,fixMessageIndexer.getValueIndexForTag(8));
        assertEquals(7,fixMessageIndexer.getValueLengthForTag(8));


        assertEquals(2,fixMessageIndexer.getIndexForTag(35));
        assertEquals(21,fixMessageIndexer.getValueIndexForTag(35));
        assertEquals(4,fixMessageIndexer.getValueLengthForTag(35));
    }
}
