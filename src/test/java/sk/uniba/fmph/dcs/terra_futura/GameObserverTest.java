package sk.uniba.fmph.dcs.terra_futura;


import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

/*unit tests for GameObserver*/

class TestObserver implements TerraFuturaObserverInterface  {
    int count;
    String last;
    @Override
    public void notify(String gState)  {
        last = gState;
        count++;
    }
}


public class GameObserverTest  {
    private TestObserver pl1, pl2;
    private GameObserver obs;
    private HashMap<Integer, TerraFuturaObserverInterface> reg;


    @Before
    public void setUp()  {
        reg = new HashMap<>();
        pl1 = new TestObserver();
        pl2 = new TestObserver();
        obs = new GameObserver(reg);
    }

    /*Checks for the creation of duplicate players.*/
    @Test
    public void regAndReplace()  {
        assertEquals(0, reg.size());

        reg.put(1, pl1);
        reg.put(2, pl2);
        assertEquals(2, reg.size());

        TestObserver new2 = new TestObserver();
        reg.put(2, new2);
        obs.notifyAll(Map.of(2, "x"));

        assertEquals("x", new2.last);
        assertNull(pl2.last);
    }

    /*checks for a null observer*/
    @Test
    public void regNull()  {
        reg.put(1, null);
        reg.put(2, pl2);

        Map<Integer, String> message = new HashMap<>();
        message.put(1, "x");
        message.put(2, "y");
        obs.notifyAll(message);

        assertEquals("y", pl2.last);
        assertEquals(1, pl2.count);


    }

    /*checks if it was sent to everyone*/
    @Test
    public void send()  {
        reg.put(1, pl1);
        reg.put(2, pl2);

        obs.notifyAll(Map.of(1, "s1", 2, "s2"));

        assertEquals("s1", pl1.last);
        assertEquals("s2", pl2.last);
        assertEquals(1, pl1.count);
        assertEquals(1, pl2.count);
    }

    /*checks for a null message*/
    @Test
    public void nullMessage()  {
        reg.put(1, pl1);

        Map<Integer, String> state = new HashMap<>();
        state.put(1, null);
        obs.notifyAll(state);

        assertNull(pl1.last);
        assertEquals(0, pl1.count);
    }

    /*checks remove observers*/
    @Test
    public void removesObserver()  {
        reg.put(1, pl1);
        reg.remove(1);
        obs.notifyAll(Map.of(1, "x"));

        assertNull(pl1.last);
        assertEquals(0, reg.size());
        assertEquals(0, pl1.count);

    }

}
