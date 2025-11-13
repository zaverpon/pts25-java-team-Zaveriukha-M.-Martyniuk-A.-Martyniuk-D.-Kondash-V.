package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SelectRewardTest {

    private SelectReward selectReward;
    private TestCard card;

    private static class TestCard extends Card {

        boolean canGetResult = true;
        int canGetCalls = 0;
        final List<List<Resource>> receivedResources = new ArrayList<>();

        TestCard() {
            super(new Resource[] {}, 0);
        }

        @Override
        public boolean canGetResources(final List<Resource> newResources) {
            canGetCalls++;
            return canGetResult;
        }

        @Override
        public void getResources(final List<Resource> newResources) {
            receivedResources.add(new ArrayList<>(newResources));
        }
    }

    @Before
    public void setUp() {
        selectReward = new SelectReward();
        card = new TestCard();
    }

    @Test
    public void initialState_isEmpty() {
        String state = selectReward.state();
        JSONObject json = new JSONObject(state);

        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());

        assertFalse(selectReward.canSelectReward(Resource.Green));
        assertFalse(selectReward.canSelectReward(Resource.Red));
    }

    @Test
    public void setReward_initializesSelection_andCanSelect() {
        card.canGetResult = true;

        selectReward.setReward(2, card, new Resource[] {Resource.Green, Resource.Red});

        assertTrue(selectReward.canSelectReward(Resource.Green));
        assertTrue(selectReward.canSelectReward(Resource.Red));

        assertFalse(selectReward.canSelectReward(Resource.Polution));
    }

    @Test
    public void setReward_failsWhenAlreadyInProgress() {
        card.canGetResult = true;

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        try {
            selectReward.setReward(2, card, new Resource[] {Resource.Red});
            fail("Expected IllegalStateException when selection already in progress");
        } catch (IllegalStateException expected) {
            // ok
        }
    }

    @Test
    public void setReward_nullArgumentsAreRejected() {
        try {
            selectReward.setReward(1, null, new Resource[] {Resource.Green});
            fail("Expected IllegalArgumentException for null card");
        } catch (IllegalArgumentException expected) { }

        try {
            selectReward.setReward(1, card, null);
            fail("Expected IllegalArgumentException for null reward array");
        } catch (IllegalArgumentException expected) { }
    }

    @Test
    public void canSelectReward_falseWhenCardRejectsResource() {
        card.canGetResult = false;

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        assertFalse("Card rejects resource → cannot select",
                selectReward.canSelectReward(Resource.Green));
    }

    @Test
    public void selectReward_appliesToCard_andClearsContext() {
        card.canGetResult = true;

        selectReward.setReward(3, card, new Resource[] {Resource.Green, Resource.Red});

        selectReward.selectReward(Resource.Green);

        assertEquals(1, card.receivedResources.size());
        List<Resource> firstCall = card.receivedResources.get(0);
        assertEquals(1, firstCall.size());
        assertEquals(Resource.Green, firstCall.get(0));

        String state = selectReward.state();
        JSONObject json = new JSONObject(state);
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
        assertFalse(selectReward.canSelectReward(Resource.Red));
    }

    @Test
    public void selectReward_rejectsNotAllowedResource() {
        card.canGetResult = true;

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        try {
            selectReward.selectReward(Resource.Red);
            fail("Expected IllegalArgumentException for not allowed resource");
        } catch (IllegalArgumentException expected) {
            assertTrue(card.receivedResources.isEmpty());
        }
    }

    @Test
    public void selectReward_rejectsWhenCardCannotAccept() {
        card.canGetResult = false;

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        try {
            selectReward.selectReward(Resource.Green);
            fail("Expected IllegalArgumentException when card cannot accept");
        } catch (IllegalArgumentException expected) {
            assertTrue(card.receivedResources.isEmpty());
        }
    }

    @Test
    public void state_serializesPlayerAndSelection() {
        card.canGetResult = true;

        selectReward.setReward(7, card, new Resource[] {Resource.Green, Resource.Red});

        String state = selectReward.state();
        JSONObject json = new JSONObject(state);

        assertEquals(7, json.getInt("player"));
        assertEquals(2, json.getJSONArray("selection").length());
        assertTrue(json.getJSONArray("selection").toList().contains("Green"));
        assertTrue(json.getJSONArray("selection").toList().contains("Red"));
    }

    @Test
    public void selectReward_failsWhenWrongPlayerSelects() {
        card.canGetResult = true;

        selectReward.setReward(1, card, new Resource[] {Resource.Green});
    }

    @Test
    public void twoPlayersSelectRewards_sequentially() {
        TestCard card1 = new TestCard();
        TestCard card2 = new TestCard();

        selectReward.setReward(1, card1, new Resource[] {Resource.Green});
        selectReward.selectReward(Resource.Green);

        selectReward.setReward(2, card2, new Resource[] {Resource.Red});
        selectReward.selectReward(Resource.Red);
    }

    @Test
    public void setReward_emptyRewardArray() {
        selectReward.setReward(1, card, new Resource[] {});

        assertFalse(selectReward.canSelectReward(Resource.Green));
    }
}