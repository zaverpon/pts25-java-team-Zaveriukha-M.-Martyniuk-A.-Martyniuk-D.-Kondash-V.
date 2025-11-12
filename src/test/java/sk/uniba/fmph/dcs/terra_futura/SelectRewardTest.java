package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


import static org.junit.Assert.*;

public class SelectRewardTest {

    private SelectReward selectReward;
    private Card card;

    @Before
    public void setUp() {
        selectReward = new SelectReward();
        card = new Card(new Resource[]{Resource.Green}, 0);
    }

    @Test
    public void initialState_isEmpty() {
        String state = selectReward.state();
        JSONObject json = new JSONObject(state);
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
        // No active selection -> no reward can be selected
        assertFalse(selectReward.canSelectReward(Resource.Green));
    }

    @Test
    public void setReward_initializesSelection_andCanSelect() {
        selectReward.setReward(2, card, new Resource[]{Resource.Green, Resource.Red});

        // Only resources in the 'selection' list AND accepted by the card should be allowed
        assertTrue(selectReward.canSelectReward(Resource.Green));
        assertTrue(selectReward.canSelectReward(Resource.Red));
        assertFalse(selectReward.canSelectReward(Resource.Yellow)); // Not in selection list
        assertFalse(selectReward.canSelectReward(Resource.Polution)); // Card does not accept Polution
    }

    @Test
    public void setReward_rejectsNullArgs() {
        try {
            selectReward.setReward(1, null, new Resource[]{Resource.Green});
            fail("Expected IllegalArgumentException for null card");
        } catch (IllegalArgumentException expected) { /* ok */ }

        try {
            selectReward.setReward(1, card, null);
            fail("Expected IllegalArgumentException for null reward array");
        } catch (IllegalArgumentException expected) { /* ok */ }
    }

    @Test
    public void setReward_failsWhenAlreadyInProgress() {
        selectReward.setReward(1, card, new Resource[]{Resource.Green});
        try {
            selectReward.setReward(2, card, new Resource[]{Resource.Red});
            fail("Expected IllegalStateException when selection already in progress");
        } catch (IllegalStateException expected) { /* ok */ }
    }

    @Test
    public void canSelectReward_falseWhenCardRejectsResource() {
        // Polution is put into the selection list, but the card cannot accept it
        selectReward.setReward(1, card, new Resource[]{Resource.Polution});
        assertFalse(selectReward.canSelectReward(Resource.Polution));
    }

    @Test
    public void selectReward_appliesToCard_andClearsContext() {
        selectReward.setReward(3, card, new Resource[]{Resource.Red, Resource.Gear});

        // Before selection, the card does not have Resource.Red
        assertFalse(card.getResources().contains(Resource.Red));

        selectReward.selectReward(Resource.Red);

        // The resource has been added to the card
        List<Resource> after = card.getResources();
        assertTrue(after.contains(Resource.Red));

        // The context should be cleared
        String state = selectReward.state();
        JSONObject json = new JSONObject(state);
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
        assertFalse(selectReward.canSelectReward(Resource.Gear)); // Cannot select after context is cleared
    }

    @Test
    public void selectReward_rejectsNotAllowedResource() {
        selectReward.setReward(1, card, new Resource[]{Resource.Green});
        try {
            selectReward.selectReward(Resource.Red); // Not in selection list
            fail("Expected IllegalArgumentException for not allowed resource");
        } catch (IllegalArgumentException expected) { /* ok */ }

        // Make sure the card did not change
        assertEquals(Arrays.asList(Resource.Green), card.getResources());
    }

    @Test
    public void selectReward_rejectsWhenCardCannotAccept() {
        selectReward.setReward(1, card, new Resource[]{Resource.Polution});
        try {
            selectReward.selectReward(Resource.Polution); // Card does not accept Polution
            fail("Expected IllegalArgumentException when card cannot accept");
        } catch (IllegalArgumentException expected) { /* ok */ }
    }

    @Test
    public void state_serializesPlayerAndSelection() {
        selectReward.setReward(7, card, new Resource[]{Resource.Green, Resource.Red});
        String state = selectReward.state();

        JSONObject json = new JSONObject(state);
        assertEquals(7, json.getInt("player"));
        // The order of elements is guaranteed by our setReward implementation (ArrayList from Arrays.asList)
        assertEquals(2, json.getJSONArray("selection").length());
        assertTrue(json.getJSONArray("selection").toList().contains("Green"));
        assertTrue(json.getJSONArray("selection").toList().contains("Red"));
    }
}