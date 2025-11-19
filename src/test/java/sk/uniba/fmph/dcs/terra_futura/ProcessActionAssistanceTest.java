package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for ProcessActionAssistance.
 *
 * Assumed behaviour:
 *  - Uses the LOWER effect of assisingCard (checkLower) for validation.
 *  - The "card" argument is the assistance card of the active player and must have Assistance.
 *  - Inputs are paid from cards in the provided Grid (active player's grid).
 *  - Outputs are placed on cards in the same Grid.
 *  - On success, it configures SelectReward with:
 *        player = assisingPlayer,
 *        card   = assisingCard,
 *        reward = resources actually paid as inputs.
 *  - All changes are atomic: if any validation fails, nothing is modified and no reward is set.
 */
public class ProcessActionAssistanceTest {

    private Grid grid;
    private ProcessActionAssistance process;
    private SelectReward selectReward;

    private Card assistanceCard;   // card with Assistance effect (active player's card)
    private Card sourceCard;       // card from which payment is taken
    private Card outputCard;       // card that receives output resources
    private Card otherPlayerCard;  // assisingCard (belongs to the other player)

    private static final int OTHER_PLAYER_ID = 2;

    /**
     * Simple Effect that marks a card as having Assistance.
     * It always returns true for check(), because ProcessActionAssistance
     * does not use this Effect for cost/output validation.
     */
    private static class AssistanceEffect implements Effect {

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            // Assistance effect itself is not checked by ProcessActionAssistance
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

        // Active player's cards on the grid

        // assistanceCard: has Assistance effect (upper), no specific transformation
        assistanceCard = new Card(
                new Resource[]{}, 0,
                new AssistanceEffect(),  // upperEffect with Assistance
                null                     // lowerEffect unused here
        );

        // sourceCard: contains 1 Green to pay
        sourceCard = new Card(new Resource[]{Resource.Green}, 0);

        // outputCard: will receive Gear
        outputCard = new Card(new Resource[]{}, 0);

        grid.putCard(GridPosition.X0_Y0, assistanceCard);
        grid.putCard(GridPosition.X1_Y0, sourceCard);
        grid.putCard(GridPosition.X0_Y1, outputCard);

        // Other player's card whose LOWER effect we copy

        // Effect: pay [Green] → get [Gear], pollution 0
        Effect lowerEffect = new TransformationFixed(
                List.of(Resource.Green),
                List.of(Resource.Gear),
                0
        );
        otherPlayerCard = new Card(
                new Resource[]{}, 0,
                null,        // upperEffect
                lowerEffect  // lowerEffect used by checkLower()
        );
    }

    @Test
    public void activateCard_successfulAssistance_setsRewardAndAppliesTransformation() {
        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)), // pay from sourceCard
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),  // put output on outputCard
                List.of()                                              // no pollution
        );

        assertTrue("Assistance activation should succeed", ok);

        // Payment applied: sourceCard lost Green
        assertFalse(sourceCard.getResources().contains(Resource.Green));

        // Output applied: outputCard gained Gear
        assertTrue(outputCard.getResources().contains(Resource.Gear));

        // Reward has been configured for OTHER_PLAYER_ID
        String rewardState = selectReward.state();
        System.out.println("SelectReward state after successful assistance:\n" + rewardState);
        JSONObject json = new JSONObject(rewardState);

        assertEquals(OTHER_PLAYER_ID, json.getInt("player"));
        JSONArray selection = json.getJSONArray("selection");
        assertEquals(1, selection.length());
        assertEquals("Green", selection.getString(0));

        // When the other player selects the reward, it should be placed on otherPlayerCard
        selectReward.selectReward(Resource.Green);
        assertTrue(
                "Other player's card should receive the rewarded resource",
                otherPlayerCard.getResources().contains(Resource.Green)
        );
    }

    @Test
    public void activateCard_failsIfCardHasNoAssistance() {
        // Card without Assistance (both effects null)
        Card noAssistCard = new Card(new Resource[]{}, 0);
        grid.putCard(GridPosition.X2_Y0, noAssistCard);

        boolean ok = process.activateCard(
                noAssistCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)), // valid payment
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse("Activation should fail if card has no Assistance", ok);

        // No changes to resources
        assertTrue(sourceCard.getResources().contains(Resource.Green));
        assertFalse(outputCard.getResources().contains(Resource.Gear));

        // No reward configured
        JSONObject json = new JSONObject(selectReward.state());
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
    }

    @Test
    public void activateCard_failsIfLowerEffectCheckFails() {
        // Use wrong output (Money instead of Gear) so otherPlayerCard.checkLower fails
        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Money, GridPosition.X0_Y1)), // mismatched output
                List.of()
        );

        assertFalse("Activation should fail if lower effect does not match", ok);

        // No changes to resources
        assertTrue(sourceCard.getResources().contains(Resource.Green));
        assertFalse(outputCard.getResources().contains(Resource.Money));

        // No reward configured
        JSONObject json = new JSONObject(selectReward.state());
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
    }

    @Test
    public void activateCard_failsIfCannotPayInputs() {
        // Remove Green from sourceCard so payment is impossible
        sourceCard.putResources(List.of(Resource.Green));
        assertFalse(sourceCard.getResources().contains(Resource.Green));

        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)), // trying to pay missing Green
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse("Activation should fail when active player cannot pay", ok);

        // Output should not be granted
        assertFalse(outputCard.getResources().contains(Resource.Gear));

        // No reward configured
        JSONObject json = new JSONObject(selectReward.state());
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
    }

    @Test
    public void activateCard_failsIfGridPositionsInvalid() {
        // Use non-existing grid position as source of input
        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X2_Y2)), // no card here
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertFalse("Activation should fail for invalid grid positions", ok);

        // No mutation
        assertTrue(sourceCard.getResources().contains(Resource.Green));
        assertFalse(outputCard.getResources().contains(Resource.Gear));

        // No reward configured
        JSONObject json = new JSONObject(selectReward.state());
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
    }

    @Test
    public void activateCard_returnsFalseOnNullArguments() {
        List<Pair<Resource, GridPosition>> emptyPairs = new ArrayList<>();
        List<GridPosition> emptyPollution = new ArrayList<>();

        assertFalse(process.activateCard(
                null, grid, OTHER_PLAYER_ID, otherPlayerCard,
                emptyPairs, emptyPairs, emptyPollution));

        assertFalse(process.activateCard(
                assistanceCard, null, OTHER_PLAYER_ID, otherPlayerCard,
                emptyPairs, emptyPairs, emptyPollution));

        assertFalse(process.activateCard(
                assistanceCard, grid, OTHER_PLAYER_ID, null,
                emptyPairs, emptyPairs, emptyPollution));

        assertFalse(process.activateCard(
                assistanceCard, grid, OTHER_PLAYER_ID, otherPlayerCard,
                null, emptyPairs, emptyPollution));

        assertFalse(process.activateCard(
                assistanceCard, grid, OTHER_PLAYER_ID, otherPlayerCard,
                emptyPairs, null, emptyPollution));

        assertFalse(process.activateCard(
                assistanceCard, grid, OTHER_PLAYER_ID, otherPlayerCard,
                emptyPairs, emptyPairs, null));
    }

    @Test
    public void activateCard_succeedsWhenAssistingCardHasNoLowerEffect_butCheckLowerAllows() {
        Card assisting = new Card(new Resource[]{}, 0);

        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                assisting,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Gear, GridPosition.X0_Y1)),
                List.of()
        );

        assertTrue("Activation should succeed when assisting card has no lowerEffect", ok);

        assertTrue(outputCard.getResources().contains(Resource.Gear));

        JSONObject json = new JSONObject(selectReward.state());
        assertEquals(OTHER_PLAYER_ID, json.getInt("player"));
    }

    @Test
    public void activateCard_failsIfOutputPositionDoesNotExist() {
        boolean ok = process.activateCard(
                assistanceCard,
                grid,
                OTHER_PLAYER_ID,
                otherPlayerCard,
                List.of(Pair.of(Resource.Green, GridPosition.X1_Y0)),
                List.of(Pair.of(Resource.Gear, GridPosition.X2_Y2)),
                List.of()
        );

        assertFalse("Activation should fail if output position is invalid", ok);

        assertTrue(sourceCard.getResources().contains(Resource.Green));

        assertFalse(outputCard.getResources().contains(Resource.Gear));

        JSONObject json = new JSONObject(selectReward.state());
        assertTrue(json.isNull("player"));
        assertEquals(0, json.getJSONArray("selection").length());
    }
}