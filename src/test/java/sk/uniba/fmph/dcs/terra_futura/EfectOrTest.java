package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class EfectOrTest {

    private TransformationFixed effect1;
    private TransformationFixed effect2;
    private EfectOr effectOr;

    @Before
    public void setUp() {
        effect1 = new TransformationFixed(
                List.of(Resource.Green), List.of(Resource.Gear), 1);
        effect2 = new TransformationFixed(
                List.of(Resource.Red), List.of(Resource.Car), 0);
        effectOr = new EfectOr(List.of(effect1, effect2));
    }

    @Test
    public void testCheckReturnsTrueIfAnySubEffectMatches() {
        boolean result = effectOr.check(
                List.of(Resource.Red),
                List.of(Resource.Car),
                0);
        System.out.println("EffectOr check() result: " + result);
        assertTrue(result);
    }

    @Test
    public void testCheckReturnsFalseIfNoSubEffectMatches() {
        boolean result = effectOr.check(
                List.of(Resource.Yellow),
                List.of(Resource.Money),
                0);
        System.out.println("EffectOr check() result: " + result);
        assertFalse(result);
    }

    @Test
    public void testHasAssistanceAlwaysFalse() {
        assertFalse(effectOr.hasAssistance());
    }

    @Test
    public void testStateContainsSubEffectInfo() {
        String state = effectOr.state();
        System.out.println("EffectOr state(): " + state);
        assertTrue(state.contains("TransformationFixed"));
        assertTrue(state.contains("EfectOr"));
    }
}

