package com.jpm.fixparser;

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

        handler.addBeginTag(453);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));

        handler.addBeginTag(453);
        assertEquals(1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));

        handler.addBeginTag(453);
        assertEquals(2, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
    }

    @Test
    public void handlerMustReturnLastOccurrenceWithinGroupAfterAddingTag() {
        assertEquals(-1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(447, 0));

        int occurrenceIndexOfBeginTagWithinMessage = handler.addBeginTag(453);
        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));

        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));

        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(448, 0));
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(448, occurrenceIndexOfBeginTagWithinMessage));
    }

    @Test
    public void handlerMustReturnLastOccurrenceWithinGroupAfterAddingMultipleRepeatGroups() {
        assertEquals(-1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(-1, handler.getOccurrenceIndexWithinRepeatingGroup(447, 0));

        int occurrenceIndexOfBeginTagWithinMessage = handler.addBeginTag(453);
        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        handler.addGroupMemberTagForRepeatBeginTag(452, 453);

        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(448, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(452, occurrenceIndexOfBeginTagWithinMessage));

        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        handler.addGroupMemberTagForRepeatBeginTag(452, 453);

        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(448, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(452, occurrenceIndexOfBeginTagWithinMessage));

        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        handler.addGroupMemberTagForRepeatBeginTag(452, 453);

        assertEquals(0, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(448, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(452, occurrenceIndexOfBeginTagWithinMessage));

        occurrenceIndexOfBeginTagWithinMessage = handler.addBeginTag(453);
        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        handler.addGroupMemberTagForRepeatBeginTag(452, 453);

        assertEquals(1, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(448, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(0, handler.getLastOccurrenceIndexOfTagWithinGroup(452, occurrenceIndexOfBeginTagWithinMessage));


        occurrenceIndexOfBeginTagWithinMessage = handler.addBeginTag(453);
        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        handler.addGroupMemberTagForRepeatBeginTag(452, 453);

        handler.addGroupMemberTagForRepeatBeginTag(447, 453);
        handler.addGroupMemberTagForRepeatBeginTag(448, 453);
        handler.addGroupMemberTagForRepeatBeginTag(452, 453);

        assertEquals(2, handler.getLastOccurrenceIndexOfBeginTagWithinMessage(453));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(447, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(448, occurrenceIndexOfBeginTagWithinMessage));
        assertEquals(1, handler.getLastOccurrenceIndexOfTagWithinGroup(452, occurrenceIndexOfBeginTagWithinMessage));

        //-- Retrieve Last occurrence from First Repeat group
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(447, 0));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(448, 0));
        assertEquals(2, handler.getLastOccurrenceIndexOfTagWithinGroup(452, 0));
    }
}
