package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for the Effect interface.
 * Verifies cooperation between Effect, Card and Grid classes.
 */
public class EfectIntegrationTest {

    private Grid grid;
    private Card cardFixed;
    private Card cardOr;
    private Card cardArbitrary;

    @Before
    public void setUp() {
        grid = new Grid();

        // Simple fixed transformation: Green → Gear, pollution +1
        Effect fixedEffect = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                0);
        cardFixed = new Card(new Resource[]{Resource.Green}, 0, fixedEffect, null);

        // OR effect: (Green→Car) OR (Red→Gear)
        Effect effect1 = new TransformationFixed(List.of(Resource.Green), List.of(Resource.Car), 0);
        Effect effect2 = new TransformationFixed(List.of(Resource.Red), List.of(Resource.Gear), 0);
        Effect effectOr = new EfectOr(List.of(effect1, effect2));
        cardOr = new Card(new Resource[]{Resource.Red}, 0, effectOr, null);

        // Arbitrary: any 2 → Money, pollution +1
        Effect arb = new ArbitraryBasic(2, List.of(Resource.Money), 1);
        cardArbitrary = new Card(new Resource[]{Resource.Green, Resource.Red}, 0, arb, null);

        // Place cards on the grid
        grid.putCard(GridPosition.X0_Y0, cardFixed);
        grid.putCard(GridPosition.X1_Y0, cardOr);
        grid.putCard(GridPosition.X0_Y1, cardArbitrary);
    }

    @Test
    public void testTransformationFixedEffectThroughCard() {
        boolean result = cardFixed.check(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                0);

        assertTrue("Card with TransformationFixed should pass check()", result);

        // Simulate activation
        boolean activated = grid.canBeActivated(GridPosition.X0_Y0);
        assertTrue(activated);
        grid.setActivated(GridPosition.X0_Y0);
        assertFalse(grid.canBeActivated(GridPosition.X0_Y0));

        // End turn resets activation
        grid.endTurn();
        assertTrue(grid.canBeActivated(GridPosition.X0_Y0));
    }

    @Test
    public void testEffectOrChoosesCorrectVariant() {
        // should pass for (Red → Gear)
        boolean result1 = cardOr.check(
                List.of(Resource.Red),
                List.of(Resource.Gear),
                0);
        assertTrue(result1);

        // should fail for unrelated transformation
        boolean result2 = cardOr.check(
                List.of(Resource.Green),
                List.of(Resource.Yellow),
                0);
        assertFalse(result2);
    }

    @Test
    public void testArbitraryBasicEffectIntegration() {
        boolean result = cardArbitrary.check(
                List.of(Resource.Green, Resource.Red),
                List.of(Resource.Money),
                1);
        assertTrue("ArbitraryBasic should allow transforming any 2 resources to Money", result);
    }

    @Test
    public void testCardActivationOnGrid() {
        assertTrue("Card exists and can be activated",
                grid.canBeActivated(GridPosition.X0_Y0));

        grid.setActivated(GridPosition.X0_Y0);
        assertFalse("After activation, card cannot be re-activated this turn",
                grid.canBeActivated(GridPosition.X0_Y0));

        grid.endTurn();
        assertTrue("After endTurn, card can be activated again",
                grid.canBeActivated(GridPosition.X0_Y0));
    }

    @Test
    public void testStateContainsEffectInfo() {
        String state = grid.state();
        System.out.println("Grid state:\n" + state);
        assertTrue(state.contains("Green"));
        assertTrue(state.contains("Gear"));
        assertTrue(state.contains("(0, 0)"));
    }
}
