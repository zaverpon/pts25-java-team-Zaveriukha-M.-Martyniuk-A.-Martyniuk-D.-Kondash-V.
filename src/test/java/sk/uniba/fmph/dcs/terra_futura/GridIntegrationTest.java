package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;


/**
 * Integration tests for the Grid class.
 */
public class GridIntegrationTest {

    @Test
    public void testSimpleCardActivationScenario() {
        Grid grid = new Grid();

        Effect effect = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                1);
        Card card = new Card(new Resource[]{Resource.Green}, 0, effect, null);

        grid.putCard(GridPosition.X0_Y0, card);

        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));

        grid.setActivated(GridPosition.X0_Y0);
        assertFalse(grid.canBeActivated(GridPosition.X0_Y0));

        grid.endTurn();
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));

        String state = grid.state();
        assertTrue(state.contains("TransformationFixed"));
        assertTrue(state.contains("(0, 0)"));
    }


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

        grid.putCard(GridPosition.X0_Y0, card1);
        grid.putCard(GridPosition.X0_Y1, card2);
        grid.putCard(GridPosition.X1_Y0, card3);

        grid.setActivated(GridPosition.X0_Y0);
        grid.setActivated(GridPosition.X0_Y1);
        grid.setActivated(GridPosition.X1_Y0);

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
