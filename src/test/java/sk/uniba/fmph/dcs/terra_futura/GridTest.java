package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the Grid class.
 *
 * These tests validate individual methods in isolation:
 *  placement, activation logic, Optional handling, and JSON state construction.
 */
public class GridTest {

    private Grid grid;
    private Card cardCenter;
    private Card cardNew;

    /**
     * Common grid setup:
     * One card is placed at (0,0), all other positions empty.
     */
    @Before
    public void setUp() {
        grid = new Grid();
        cardCenter = new Card(new Resource[]{Resource.Green}, 0);
        cardNew = new Card(new Resource[]{Resource.Red}, 0);
        grid.putCard(GridPosition.X0_Y0, cardCenter);
    }

    /** getCard should return the card wrapped in Optional when present. */
    @Test
    public void testGetCardReturnsCard() {
        assertEquals(cardCenter, grid.getCard(GridPosition.X0_Y0).orElse(null));
    }

    /** getCard should return empty Optional for empty positions. */
    @Test
    public void testGetCardReturnsNullForEmpty() {
        assertNull(grid.getCard(GridPosition.X1_Y1).orElse(null));
    }

    /** Adjacent placement should be allowed. */
    @Test
    public void testCanPutCardAdjacentToExisting() {
        assertTrue(grid.canPutCard(GridPosition.X1_Y0));
    }

    /** Non-adjacent placement should be rejected. */
    @Test
    public void testCanPutCardFarAwayIsFalse() {
        assertFalse(grid.canPutCard(GridPosition.X2_Y2));
    }

    /** putCard should store the card correctly. */
    @Test
    public void testPutCardStoresCard() {
        grid.putCard(GridPosition.X1_Y0, cardNew);
        assertEquals(cardNew, grid.getCard(GridPosition.X1_Y0).orElse(null));
    }

    /** Existing, non-activated card should be activatable. */
    @Test
    public void testCanBeActivatedTrueForExistingCard() {
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));
    }

    /** Empty positions should not be activatable. */
    @Test
    public void testCanBeActivatedFalseForEmptyCell() {
        assertFalse(grid.canBeActivated(GridPosition.X2_Y2));
    }

    /** Once activated, the card should not be reactivated. */
    @Test
    public void testSetActivatedPreventsReactivation() {
        grid.setActivated(GridPosition.X0_Y0);
        assertFalse(grid.canBeActivated(GridPosition.X0_Y0));
    }

    /** endTurn() resets activation state. */
    @Test
    public void testEndTurnResetsActivation() {
        grid.setActivated(GridPosition.X0_Y0);
        grid.endTurn();
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));
    }

    /** Activation pattern should be stored and appear in state(). */
    @Test
    public void testSetActivationPatternStoredInState() {
        List<GridPosition> pattern = List.of(
                GridPosition.X0_Y0,
                GridPosition.X1_Y0
        );
        grid.setActivationPattern(pattern);

        String state = grid.state();
        System.out.println("Grid state:\n" + state);

        assertTrue(state.contains("(0, 0)"));
        assertTrue(state.contains("(1, 0)"));
    }

    /** state() should include resource and coordinate information for debugging/UI. */
    @Test
    public void testStateContainsCardInfo() {
        String state = grid.state();
        assertTrue(state.contains("Green"));
        assertTrue(state.contains("(0, 0)"));
    }
}
