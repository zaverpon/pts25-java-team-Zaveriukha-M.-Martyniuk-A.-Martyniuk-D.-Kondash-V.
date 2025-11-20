package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Integration tests for the Grid class.
 *
 * These tests verify end-to-end behavior:
 * Grid + Card + Effect working together in realistic gameplay scenarios.
 */
public class GridIntegrationTest {

    /**
     * Scenario 1:
     * A simple card with a fixed effect is placed on the grid.
     * We verify:
     *  - placement works
     *  - activation rules behave correctly
     *  - activation resets after endTurn()
     *  - card effect information appears in JSON state
     */
    @Test
    public void testSimpleCardActivationScenario() {
        Grid grid = new Grid();

        Effect effect = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                1);

        Card card = new Card(new Resource[]{Resource.Green}, 0, effect, null);

        // placing card at starting position
        grid.putCard(GridPosition.X0_Y0, card);

        // should be activatable before activation
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));

        // after activation – no longer activatable
        grid.setActivated(GridPosition.X0_Y0);
        assertFalse(grid.canBeActivated(GridPosition.X0_Y0));

        // after endTurn – activation resets
        grid.endTurn();
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));

        // state must contain effect name and coordinates
        String state = grid.state();
        assertTrue(state.contains("TransformationFixed"));
        assertTrue(state.contains("(0, 0)"));
    }


    /**
     * Scenario 2:
     * Multiple cards with different effects:
     *  - TransformationFixed
     *  - ArbitraryBasic
     *  - EffectOr
     *
     * Verify:
     *  - adjacency placement rules
     *  - activation works for multiple cards
     *  - JSON state includes all effect types and coordinates
     */
    @Test
    public void testMultipleEffectsIntegrationScenario() {
        Grid grid = new Grid();

        Effect fixed = new TransformationFixed(List.of(Resource.Green), List.of(Resource.Gear), 1);
        Effect arb = new ArbitraryBasic(2, List.of(Resource.Money), 1);
        Effect orEffect = new EfectOr(List.of(
                new TransformationFixed(List.of(Resource.Red), List.of(Resource.Car), 0),
                new TransformationFixed(List.of(Resource.Green), List.of(Resource.Gear), 0)
        ));

        Card card1 = new Card(new Resource[]{Resource.Green}, 0, fixed, null);
        Card card2 = new Card(new Resource[]{Resource.Red, Resource.Green}, 0, arb, null);
        Card card3 = new Card(new Resource[]{Resource.Red}, 0, orEffect, null);

        // place cards adjacent to each other
        grid.putCard(GridPosition.X0_Y0, card1);
        grid.putCard(GridPosition.X0_Y1, card2);
        grid.putCard(GridPosition.X1_Y0, card3);

        // activate all cards
        grid.setActivated(GridPosition.X0_Y0);
        grid.setActivated(GridPosition.X0_Y1);
        grid.setActivated(GridPosition.X1_Y0);

        // the state should reflect all effect types and coordinates
        String state = grid.state();
        System.out.println("Grid state:\n" + state);

        assertTrue(state.contains("TransformationFixed"));
        assertTrue(state.contains("ArbitraryBasic"));
        assertTrue(state.contains("EfectOr"));
        assertTrue(state.contains("(0, 0)"));
        assertTrue(state.contains("(1, 0)"));
        assertTrue(state.contains("(0, 1)"));
    }
}
