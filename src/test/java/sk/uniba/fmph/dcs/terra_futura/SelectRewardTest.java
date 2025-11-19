package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONObject;
import org.junit.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyList;

public class SelectRewardTest {
    private SelectReward selectReward;
    private Card card;

    @Before
    public void setUp() {
        selectReward = new SelectReward();
        card = mock(Card.class);
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
        when(card.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(2, card, new Resource[] {Resource.Green, Resource.Red});

        assertTrue(selectReward.canSelectReward(Resource.Green));
        assertTrue(selectReward.canSelectReward(Resource.Red));

        assertFalse(selectReward.canSelectReward(Resource.Polution));
    }

    @Test(expected = IllegalStateException.class)
    public void setReward_failsWhenAlreadyInProgress() {
        when(card.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        selectReward.setReward(2, card, new Resource[] {Resource.Red});
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
        when(card.canGetResources(anyList())).thenReturn(false);

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        assertFalse("Card rejects resource -> cannot select",
                selectReward.canSelectReward(Resource.Green));
        verify(card).canGetResources(anyList());
    }

    @Test
    public void selectReward_appliesToCard_andClearsContext() {
        when(card.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(3, card, new Resource[]{Resource.Green, Resource.Red});

        selectReward.selectReward(Resource.Green);

        verify(card).getResources(
                java.util.Collections.singletonList(Resource.Green)
        );

        String state = selectReward.state();
        JSONObject json = new JSONObject(state);
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());

        assertFalse(selectReward.canSelectReward(Resource.Red));
    }

    @Test
    public void selectReward_rejectsNotAllowedResource() {
        when(card.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        try {
            selectReward.selectReward(Resource.Red);
            fail("Expected IllegalArgumentException for not allowed resource");
        } catch (IllegalArgumentException expected) {
            verify(card, never()).getResources(anyList());
        }
    }

    @Test
    public void selectReward_rejectsWhenCardCannotAccept() {
        when(card.canGetResources(anyList())).thenReturn(false);

        selectReward.setReward(1, card, new Resource[] {Resource.Green});

        try {
            selectReward.selectReward(Resource.Green);
            fail("Expected IllegalArgumentException when card cannot accept");
        } catch (IllegalArgumentException expected) {
            verify(card, never()).getResources(anyList());
        }
    }

    @Test
    public void state_serializesPlayerAndSelection() {
        when(card.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(7, card, new Resource[] {Resource.Green, Resource.Red});

        String state = selectReward.state();
        JSONObject json = new JSONObject(state);

        assertEquals(7, json.getInt("player"));
        assertEquals(2, json.getJSONArray("selection").length());
        assertTrue(json.getJSONArray("selection").toList().contains("Green"));
        assertTrue(json.getJSONArray("selection").toList().contains("Red"));
    }

    @Test
    public void twoPlayersSelectRewards_sequentially() {
        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);

        when(card1.canGetResources(anyList())).thenReturn(true);
        when(card2.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(1, card1, new Resource[] {Resource.Green});
        selectReward.selectReward(Resource.Green);
        verify(card1).getResources(
                java.util.Collections.singletonList(Resource.Green)
        );

        selectReward.setReward(2, card2, new Resource[] {Resource.Red});
        selectReward.selectReward(Resource.Red);
        verify(card2).getResources(
                java.util.Collections.singletonList(Resource.Red)
        );
    }

    @Test
    public void setReward_emptyRewardArray() {
        when(card.canGetResources(anyList())).thenReturn(true);

        selectReward.setReward(1, card, new Resource[] {});

        assertFalse(selectReward.canSelectReward(Resource.Green));

        String state = selectReward.state();
        JSONObject json = new JSONObject(state);
        assertEquals(0, json.getJSONArray("selection").length());
    }
}