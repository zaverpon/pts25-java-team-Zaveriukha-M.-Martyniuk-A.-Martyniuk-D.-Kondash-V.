package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Unit tests for Card class.
 * Verifies delegation of logic to Effect and basic card behavior.
 */
public class CardTest {

    private Effect fakeEffectTrue;
    private Effect fakeEffectFalse;
    private Card cardTrue;
    private Card cardFalse;

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
                return "FakeEffectTrue";
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
                return "FakeEffectFalse";
            }
        };

        cardTrue = new Card(fakeEffectTrue);
        cardFalse = new Card(fakeEffectFalse);
    }

    @Test
    public void testActivateDelegatesToEffect() {
        boolean result = cardTrue.activate(List.of(Resource.Green), List.of(Resource.Gear), 1);
        System.out.println("Card.activate() result = " + result);
        assertTrue(result);

        result = cardFalse.activate(List.of(Resource.Green), List.of(Resource.Gear), 1);
        System.out.println("Card.activate() result = " + result);
        assertFalse(result);
    }

    @Test
    public void testHasAssistanceDelegatesToEffect() {
        assertFalse(cardTrue.hasAssistance());
        assertTrue(cardFalse.hasAssistance());
    }

    @Test
    public void testStateReturnsEffectState() {
        assertEquals("FakeEffectTrue", cardTrue.state());
        assertEquals("FakeEffectFalse", cardFalse.state());
    }

    @Test
    public void testCardCreatedWithEffectNotNull() {
        assertNotNull(cardTrue);
        assertNotNull(cardFalse);
    }
}
