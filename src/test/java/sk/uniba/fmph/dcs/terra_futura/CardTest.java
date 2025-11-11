package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit tests for the Card class.
 */
public class CardTest {

    private Card card;
    private Effect fakeEffectTrue;
    private Effect fakeEffectFalse;

    @Before
    public void setUp() {
        fakeEffectTrue = new Effect() {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return true;
            }

            @Override
            public boolean hasAssistance() {
                return false;
            }

            @Override
            public String state() {
                return "FakeTrue";
            }
        };

        fakeEffectFalse = new Effect() {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return false;
            }

            @Override
            public boolean hasAssistance() {
                return true;
            }

            @Override
            public String state() {
                return "FakeFalse";
            }
        };

        card = new Card(new Resource[]{Resource.Green, Resource.Red}, 0, fakeEffectTrue, fakeEffectFalse);
    }

    @Test
    public void testCanGetResourcesTrue() {
        List<Resource> incoming = List.of(Resource.Green, Resource.Gear);
        assertTrue(card.canGetResources(incoming));
    }

    @Test
    public void testCanGetResourcesFalseForPollution() {
        List<Resource> incoming = List.of(Resource.Green, Resource.Polution);
        assertFalse(card.canGetResources(incoming));
    }

    @Test
    public void testGetResourcesAddsWhenAllowed() {
        List<Resource> newRes = new ArrayList<>();
        newRes.add(Resource.Yellow);
        card.getResources(newRes);

        assertTrue(card.getResources().contains(Resource.Yellow));
    }

    @Test
    public void testCanPutResourcesTrue() {
        List<Resource> outgoing = List.of(Resource.Green);
        assertTrue(card.canPutResources(outgoing));
    }

    @Test
    public void testCanPutResourcesFalse() {
        List<Resource> outgoing = List.of(Resource.Money);
        assertFalse(card.canPutResources(outgoing));
    }

    @Test
    public void testPutResourcesRemovesWhenAllowed() {
        List<Resource> outgoing = new ArrayList<>();
        outgoing.add(Resource.Green);
        card.putResources(outgoing);

        assertFalse(card.getResources().contains(Resource.Green));
    }

    @Test
    public void testCheckReturnsFalseWhenLowerFails() {
        boolean result = card.check(List.of(Resource.Green), List.of(Resource.Car), 1);
        assertFalse(result);
    }

    @Test
    public void testHasAssistanceFalseWhenAnyEffectHasAssistance() {
        assertFalse(card.hasAssistance());
    }

    @Test
    public void testStateNotEmptyAndContainsResources() {
        String state = card.state();
        System.out.println("Card state: " + state);
        assertTrue(state.contains("Green"));
        assertTrue(state.contains("pollutionSpaces"));
        assertFalse(state.isEmpty());
    }

    @Test
    public void testCheckLowerOnly() {
        assertFalse(card.checkLower(List.of(Resource.Red), List.of(Resource.Car), 0));
    }
}
