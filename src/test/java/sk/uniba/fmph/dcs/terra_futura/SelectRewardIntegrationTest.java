package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.json.*;
import org.junit.*;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Integration tests for SelectReward together with ProcessActionAssistance, Grid and Card.
 * Each test is a single end-to-end scenario.
 */
public class SelectRewardIntegrationTest {

    private Grid grid;
    private SelectReward selectReward;
    private ProcessActionAssistance process;

    private Card assistanceCard;   // card of the active player with Assistance
    private Card sourceCard;       // first source card
    private Card outputCard;       // card receiving output resources
    private Card otherPlayerCard;  // card belonging to the assisting player (reward target)

    private static final int OTHER_PLAYER_ID = 2;

    private static class AssistanceEffect implements Effect {
        @Override
        public boolean check(final List<Resource> input,
                             final List<Resource> output,
                             final int pollution) {
            return true;
        }

        @Override
        public boolean hasAssistance() {
            return true;
        }

        @Override
        public String state() {
            return "AssistanceEffect";
        }
    }

    @Before
    public void setUp() {
        grid = new Grid();
        selectReward = new SelectReward();
        process = new ProcessActionAssistance(selectReward);

        // Active player's cards
        assistanceCard = new Card(
                new Resource[] {}, 0,
                new AssistanceEffect(),
                null
        );

        // sourceCard: will pay Green in first scenario
        sourceCard = new Card(new Resource[] {Resource.Green}, 0);

        // outputCard: will receive Gear
        outputCard = new Card(new Resource[] {}, 0);

        grid.putCard(GridPosition.X0_Y0, assistanceCard);
        grid.putCard(GridPosition.X1_Y0, sourceCard);
        grid.putCard(GridPosition.X0_Y1, outputCard);

        // Other player's card whose LOWER effect we copy
        Effect lowerEffect = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                0
        );
        otherPlayerCard = new Card(
                new Resource[] {}, 0,
                null,
                lowerEffect
        );
    }

    @Test
    public void endToEnd_singleAssistanceActivation_andRewardSelection() {
        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)), // pay from sourceCard
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),  // output to outputCard
                List.of()
        );

        assertTrue("Assistance activation should succeed", ok);

        // Source card paid Green; output card received Gear
        assertFalse(sourceCard.getResources().contains(Resource.Green));
        assertTrue(outputCard.getResources().contains(Resource.Gear));

        // SelectReward configured
        String rewardState = selectReward.state();
        JSONObject json = new JSONObject(rewardState);
        assertEquals(OTHER_PLAYER_ID, json.getInt("player"));
        JSONArray selection = json.getJSONArray("selection");
        assertEquals(1, selection.length());
        assertEquals("Green", selection.getString(0));

        // Other player chooses reward
        selectReward.selectReward(Resource.Green);

        // Reward placed on otherPlayerCard
        assertTrue(otherPlayerCard.getResources().contains(Resource.Green));

        // Context cleared
        JSONObject after = new JSONObject(selectReward.state());
        assertTrue(after.isNull("player"));
        assertEquals(0, after.getJSONArray("selection").length());
    }

    /**
     * Second end-to-end scenario:
     *  - two assistance activations in a row,
     *  - between them assisting player selects reward,
     *  - second activation uses a *different* source card,
     *  - SelectReward is reused correctly and otherPlayerCard gets 2 rewards total.
     */
    @Test
    public void endToEnd_twoAssistanceActivations_withRewardSelectionBetween() {
        // First activation: pay from sourceCard at (1,0)
        boolean ok1 = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );
        assertTrue("First assistance activation should succeed", ok1);

        // First reward selection
        selectReward.selectReward(Resource.Green);

        // After first run: sourceCard has no Green
        long greensSource1 = sourceCard.getResources().stream()
                .filter(r -> r == Resource.Green).count();
        assertEquals(0, greensSource1);

        // Prepare second source card with 1 Green
        Card secondSource = new Card(new Resource[]{Resource.Green}, 0);
        grid.putCard(GridPosition.X2_Y0, secondSource); // adjacent to (1,0), so legal

        // Second activation: pay from secondSource at (2,0)
        boolean ok2 = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X2_Y0)),
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );
        assertTrue("Second assistance activation should succeed", ok2);

        // SelectReward is configured again (reused instance)
        JSONObject stateAfterSecondActivation = new JSONObject(selectReward.state());
        assertEquals(OTHER_PLAYER_ID, stateAfterSecondActivation.getInt("player"));
        assertEquals(1, stateAfterSecondActivation.getJSONArray("selection").length());
        assertEquals("Green",
                stateAfterSecondActivation.getJSONArray("selection").getString(0));

        // Second reward selection
        selectReward.selectReward(Resource.Green);

        // secondSource has no Green left
        long greensSource2 = secondSource.getResources().stream()
                .filter(r -> r == Resource.Green).count();
        assertEquals(0, greensSource2);

        // otherPlayerCard received 2 Greens in total
        long totalGreensOnOther = otherPlayerCard.getResources().stream()
                .filter(r -> r == Resource.Green).count();
        assertEquals(2, totalGreensOnOther);
    }
}
