package com.jpm.fixparser;

import com.jpm.helper.RepeatingGroupHandler;
import com.jpm.policy.DefaultPolicy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepeatingGroupHandlerTest {

    private RepeatingGroupHandler handler;

    @Before
    public void setUp() {
        handler = new RepeatingGroupHandler(DefaultPolicy.getDefaultPolicy());
    }

    @Test
    public void handlerMustReturnMinusOneWhenTagsAreNotAdded() {
        assertEquals(-1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(447, 0));
        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(452, 0));
    }

    @Test
    public void handlerMustReturnLastOccurrenceIndexAfterAddingBeginTags() {
        assertEquals(-1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));

        handler.addBeginTagAndGetIndex(453);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));

        handler.addBeginTagAndGetIndex(453);
        assertEquals(1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));

        handler.addBeginTagAndGetIndex(453);
        assertEquals(2, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
    }

    @Test
    public void handlerMustReturnLastOccurrenceWithinGroupAfterAddingTag() {
        assertEquals(-1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(447, 0));

        int beginTagindex = handler.addBeginTagAndGetIndex(453);
        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagindex);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(447, beginTagindex));

        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagindex);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(447, beginTagindex));

        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(448, 0));
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagindex);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(448, beginTagindex));
    }

    @Test
    public void handlerMustReturnLastOccurrenceWithinGroupAfterAddingMultipleRepeatGroups() {
        assertEquals(-1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(447, 0));

        int beginTagIndex = handler.addBeginTagAndGetIndex(453);
        assertEquals(0, beginTagIndex);

        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(452, beginTagIndex);

        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(447, beginTagIndex));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(448, beginTagIndex));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(452, beginTagIndex));


        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(452, beginTagIndex);

        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(447, beginTagIndex));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(448, beginTagIndex));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(452, beginTagIndex));

        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(452, beginTagIndex);

        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(447, 0));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(448, 0));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(452, 0));

        beginTagIndex = handler.addBeginTagAndGetIndex(453);
        assertEquals(10, beginTagIndex);

        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(452, beginTagIndex);

        assertEquals(1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(447, beginTagIndex));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(448, beginTagIndex));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(452, beginTagIndex));


        beginTagIndex = handler.addBeginTagAndGetIndex(453);
        assertEquals(14, beginTagIndex);

        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(452, beginTagIndex);

        handler.addGroupMemberTagForRepeatBeginTag(447, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(448, beginTagIndex);
        handler.addGroupMemberTagForRepeatBeginTag(452, beginTagIndex);

        assertEquals(2, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(447, beginTagIndex));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(448, beginTagIndex));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(452, beginTagIndex));

        //-- Retrieve Last occurrence from First Repeat group
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(447, 0));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(448, 0));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(452, 0));
    }
}
