package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the Grid class.
 */
public class GridTest {

    private Grid grid;
    private Card cardCenter;
    private Card cardNew;

    @Before
    public void setUp() {
        grid = new Grid();
        cardCenter = new Card(new Resource[]{Resource.Green}, 0);
        cardNew = new Card(new Resource[]{Resource.Red}, 0);
        grid.putCard(GridPosition.X0_Y0, cardCenter);
    }

    @Test
    public void testGetCardReturnsCard() {
        assertEquals(cardCenter, grid.getCard(GridPosition.X0_Y0));
    }

    @Test
    public void testGetCardReturnsNullForEmpty() {
        assertNull(grid.getCard(GridPosition.X1_Y1));
    }

    @Test
    public void testCanPutCardAdjacentToExisting() {
        assertTrue(grid.canPutCard(GridPosition.X1_Y0));
    }

    @Test
    public void testCanPutCardFarAwayIsFalse() {
        assertFalse(grid.canPutCard(GridPosition.X2_Y2));
    }

    @Test
    public void testPutCardStoresCard() {
        grid.putCard(GridPosition.X1_Y0, cardNew);
        assertEquals(cardNew, grid.getCard(GridPosition.X1_Y0));
    }

    @Test
    public void testCanBeActivatedTrueForExistingCard() {
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));
    }

    @Test
    public void testCanBeActivatedFalseForEmptyCell() {
        assertFalse(grid.canBeActivated(GridPosition.X2_Y2));
    }

    @Test
    public void testSetActivatedPreventsReactivation() {
        grid.setActivated(GridPosition.X0_Y0);
        assertFalse(grid.canBeActivated(GridPosition.X0_Y0));
    }

    @Test
    public void testEndTurnResetsActivation() {
        grid.setActivated(GridPosition.X0_Y0);
        grid.endTurn();
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));
    }

    @Test
    public void testSetActivationPatternStoredInState() {
        List<GridPosition> pattern = List.of(
                GridPosition.X0_Y0,
                GridPosition.X1_Y0
        );
        grid.setActivationPattern(pattern);
        String state = grid.state();
        System.out.println("Grid state:\n" + state);
        assertTrue(state.contains("(0,0)"));
        assertTrue(state.contains("(1,0)"));
    }

    @Test
    public void testStateContainsCardInfo() {
        String state = grid.state();
        assertTrue(state.contains("Green"));
        assertTrue(state.contains("(0,0)"));
    }
}
