package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ScoringMethodTest {

    private ScoringMethod scoringSingle;
    private Points fivePoints;

    @Before
    public void setUp() {
        // Combination: [Green, Red], 5 points per combination,
        // Player has: Green, Red, Yellow -> exactly 1 combination.
        fivePoints = new Points(5);
        scoringSingle = new ScoringMethod(
                List.of(Resource.Green, Resource.Red),
                fivePoints,
                List.of(Resource.Green, Resource.Red, Resource.Yellow)
        );
    }

    @Test
    public void initialState_hasNoCalculatedTotal() {
        String state = scoringSingle.state();
        System.out.println("ScoringMethod initial state:\n" + state);

        JSONObject json = new JSONObject(state);

        // resources field present and correct
        JSONArray resArr = json.getJSONArray("resources");
        assertEquals(2, resArr.length());
        assertTrue(resArr.toList().contains("Green"));
        assertTrue(resArr.toList().contains("Red"));

        // pointsPerCombination matches constructor
        assertEquals(5, json.getInt("pointsPerCombination"));

        // calculatedTotal should be null before calculation
        assertTrue(json.isNull("calculatedTotal"));
    }

    @Test
    public void selectThisMethodAndCalculate_computesSingleCombination() {
        scoringSingle.selectThisMethodAndCalculate();

        String state = scoringSingle.state();
        System.out.println("ScoringMethod state after 1-combo calculation:\n" + state);

        JSONObject json = new JSONObject(state);
        // 1 combination * 5 points = 5
        assertEquals(5, json.getInt("calculatedTotal"));
    }

    @Test
    public void selectThisMethodAndCalculate_multipleCombinations() {
        // Combination: [Green, Red], 4 points per combination.
        // Available: Green, Green, Green, Red, Red
        // Counts: Green=3, Red=2 -> min(3/1, 2/1) = 2 combinations
        Points fourPoints = new Points(4);
        ScoringMethod scoring = new ScoringMethod(
                List.of(Resource.Green, Resource.Red),
                fourPoints,
                List.of(
                        Resource.Green, Resource.Green, Resource.Green,
                        Resource.Red, Resource.Red
                )
        );

        scoring.selectThisMethodAndCalculate();
        String state = scoring.state();
        System.out.println("ScoringMethod state after multi-combo calculation:\n" + state);

        JSONObject json = new JSONObject(state);
        // 2 combinations * 4 points = 8
        assertEquals(8, json.getInt("calculatedTotal"));
    }

    @Test
    public void selectThisMethodAndCalculate_respectsDuplicateRequirements() {
        // Combination requires [Green, Green, Yellow] (two Greens).
        // Available1: Green, Green, Yellow, Yellow -> 1 combination.
        Points threePoints = new Points(3);
        ScoringMethod scoring = new ScoringMethod(
                List.of(Resource.Green, Resource.Green, Resource.Yellow),
                threePoints,
                List.of(Resource.Green, Resource.Green, Resource.Yellow, Resource.Yellow)
        );

        scoring.selectThisMethodAndCalculate();
        JSONObject json = new JSONObject(scoring.state());
        // min(Green=2/2, Yellow=2/1) = 1 -> 1 * 3 = 3
        assertEquals(3, json.getInt("calculatedTotal"));

        // Available2: Green, Green, Green, Green, Yellow, Yellow
        // Greens=4, Yellows=2 -> min(4/2=2, 2/1=2) = 2 combos -> 2*3 = 6
        ScoringMethod scoring2 = new ScoringMethod(
                List.of(Resource.Green, Resource.Green, Resource.Yellow),
                threePoints,
                List.of(
                        Resource.Green, Resource.Green, Resource.Green, Resource.Green,
                        Resource.Yellow, Resource.Yellow
                )
        );
        scoring2.selectThisMethodAndCalculate();
        JSONObject json2 = new JSONObject(scoring2.state());
        assertEquals(6, json2.getInt("calculatedTotal"));
    }

    @Test
    public void selectThisMethodAndCalculate_zeroWhenNotEnoughResources() {
        // Combination: [Green, Yellow], 10 points.
        // Available: Green, Red, Red -> no Yellow => 0 combinations.
        Points tenPoints = new Points(10);
        ScoringMethod scoring = new ScoringMethod(
                List.of(Resource.Green, Resource.Yellow),
                tenPoints,
                List.of(Resource.Green, Resource.Red, Resource.Red)
        );

        scoring.selectThisMethodAndCalculate();
        JSONObject json = new JSONObject(scoring.state());
        assertEquals("Not enough resources -> 0 total points", 0, json.getInt("calculatedTotal"));
    }

    @Test
    public void selectThisMethodAndCalculate_doesNotMutateAvailableResources() {
        // We want to ensure that "Resources are not used up by scoring."
        // So the original list passed to ScoringMethod must remain unchanged.
        List<Resource> inventory = new ArrayList<>(
                List.of(Resource.Green, Resource.Green, Resource.Red)
        );
        Points twoPoints = new Points(2);
        ScoringMethod scoring = new ScoringMethod(
                List.of(Resource.Green, Resource.Red),
                twoPoints,
                inventory
        );

        scoring.selectThisMethodAndCalculate();

        // inventory must remain exactly the same as before scoring
        assertEquals(
                List.of(Resource.Green, Resource.Green, Resource.Red),
                inventory
        );
    }

    @Test
    public void selectThisMethodAndCalculate_canBeCalledMultipleTimes_idempotent() {
        ScoringMethod scoring = new ScoringMethod(
                List.of(Resource.Green, Resource.Red),
                new Points(5),
                List.of(Resource.Green, Resource.Red, Resource.Red)
        );
        // Available: Green=1, Red=2 -> 1 combination -> 5 points.

        scoring.selectThisMethodAndCalculate();
        int first = new JSONObject(scoring.state()).getInt("calculatedTotal");

        scoring.selectThisMethodAndCalculate();
        int second = new JSONObject(scoring.state()).getInt("calculatedTotal");

        assertEquals(5, first);
        assertEquals("Calling calculation twice should not change the result", first, second);
    }
}
