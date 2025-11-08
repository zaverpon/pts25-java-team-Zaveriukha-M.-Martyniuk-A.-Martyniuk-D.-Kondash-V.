package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;


// Minimal(temporary) version for compile tests

class Grid {
    private Card card;

    void putCard(GridPosition pos, Card c) { this.card = c; }

    boolean canBeActivated(GridPosition pos) { return false; }

    boolean activateCard(GridPosition pos, List<Resource> in, List<Resource> out, int pol) {
        return false;
    }
}

class GridPosition {
    int x, y;
    GridPosition(int x, int y) { this.x = x; this.y = y; }
}


/**
 * Integration test for the Effect interface and its implementations.
 * This test verifies how Effects interact with Cards and the Grid.
 */
public class EfectIntegrationTest {

    private Grid grid;
    private Card card;

    @Before
    public void setUp() {
        Effect effect = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                1);

        card = new Card(effect);

        grid = new Grid();
        grid.putCard(new GridPosition(0, 0), card);
    }

    @Test
    public void testCardActivationChangesResources() {
        assertTrue(grid.canBeActivated(new GridPosition(0, 0)));

        boolean activated = card.activate(
                List.of(Resource.Green), // input
                List.of(Resource.Gear),  // output
                1);                      // pollution

        assertTrue(activated);

        String state = card.state();
        System.out.println("Card state: " + state);
        assertTrue(state.contains("Gear"));
        assertTrue(state.contains("pollution"));
    }

    @Test
    public void testEffectIntegrationWithGrid() {
        boolean result = grid.activateCard(
                new GridPosition(0, 0),
                List.of(Resource.Green),
                List.of(Resource.Gear),
                1);

        System.out.println("Activation via Grid: " + result);
        assertTrue(result);

        assertFalse(grid.canBeActivated(new GridPosition(0, 0)));
    }
}
