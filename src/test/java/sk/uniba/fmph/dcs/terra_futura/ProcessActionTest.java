package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ProcessActionTest {

    private Grid grid;
    private ProcessAction process;
    private Card activatedCard;
    private Card sourceCard;
    private Card destCard;

    @Before
    public void setUp() {
        grid = new Grid();
        process = new ProcessAction();

        Effect fixed = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                0
        );
        activatedCard = new Card(new Resource[]{}, 0, fixed, null);

        sourceCard = new Card(new Resource[]{Resource.Green}, 0);

        destCard = new Card(new Resource[]{}, 0);

        grid.putCard(GridPosition.X0_Y0, activatedCard);
        grid.putCard(GridPosition.X1_Y0, sourceCard);
        grid.putCard(GridPosition.X0_Y1, destCard);
    }

    @Test
    public void activateCard_success_simpleTransformation() {
        boolean ok = process.activateCard(
                activatedCard,
                grid,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertTrue("Activation should succeed", ok);

        assertFalse(sourceCard.getResources().contains(Resource.Green));
        assertTrue(destCard.getResources().contains(Resource.Gear));
    }

    @Test
    public void activateCard_fails_whenPositionMissing() {
        boolean ok = process.activateCard(
                activatedCard,
                grid,
                List.of(Pair.of(Resource.Green, GridPosition.X2_Y2)),
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse(ok);
        assertTrue(sourceCard.getResources().contains(Resource.Green));
        assertFalse(destCard.getResources().contains(Resource.Gear));
    }

    @Test
    public void activateCard_fails_whenCardCheckFails() {
        boolean ok = process.activateCard(
                activatedCard,
                grid,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Money, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse(ok);
        assertTrue("Source should keep Green on failure", sourceCard.getResources().contains(Resource.Green));
        assertFalse("Destination should not get Money", destCard.getResources().contains(Resource.Money));
    }

    @Test
    public void activateCard_fails_whenCannotPay() {
        sourceCard.putResources(List.of(Resource.Green));

        boolean ok = process.activateCard(
                activatedCard,
                grid,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse(ok);
        assertFalse(destCard.getResources().contains(Resource.Gear));
    }

    @Test
    public void activateCard_fails_whenDestinationRejectsOutputs() {
        boolean ok = process.activateCard(
                activatedCard,
                grid,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Polution, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse("Destination card should reject Polution", ok);
        assertTrue(sourceCard.getResources().contains(Resource.Green));
        assertFalse(destCard.getResources().contains(Resource.Polution));
    }

    @Test
    public void activateCard_isAtomic_whenAnyCheckFails() {
        Effect badOut = new TransformationFixed(List.of(Resource.Green), List.of(Resource.Polution), 0);
        Card specialActivated = new Card(new Resource[]{}, 0, badOut, null);
        grid.putCard(GridPosition.X1_Y1, specialActivated);

        boolean ok = process.activateCard(
                specialActivated,
                grid,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Polution, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse(ok);
        assertTrue(sourceCard.getResources().contains(Resource.Green));
        assertFalse(destCard.getResources().contains(Resource.Polution));
    }

    @Test
    public void activateCard_returnsFalse_onNullArgs() {
        assertFalse(process.activateCard(null, grid, List.of(), List.of(), List.of()));
        assertFalse(process.activateCard(activatedCard, null, List.of(), List.of(), List.of()));
        assertFalse(process.activateCard(activatedCard, grid, null, List.of(), List.of()));
        assertFalse(process.activateCard(activatedCard, grid, List.of(), null, List.of()));
        assertFalse(process.activateCard(activatedCard, grid, List.of(), List.of(), null));
    }
}