package sk.uniba.fmph.dcs.terra_futura;


import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;


public class GameObserverTest  {
    static class fObserver implements TerraFuturaObserverInterface  {
        String last;
        int count;
        @Override
        public void notify(String gameState)  {
            last = gameState;
            count++;
        }
    }

    /*Checks for the creation of duplicate players.*/
    @Test
    public void regAndReplace()  {
        GameObserver obs = new GameObserver();
        assertEquals(0, obs.size());

        obs.register(1, new fObserver());
        obs.register(2, new fObserver());
        assertEquals(2, obs.size());

        obs.register(2, new fObserver());
        assertEquals(2, obs.size());
    }

    /*checks for a null observer*/
    @Test
    public void regNull()  {
        GameObserver obs = new GameObserver();
        try {
            obs.register(1, null);
            fail("IllegalArgumentException expected");
        }  catch (IllegalArgumentException expected)  {}
    }

    /*checks if it was sent to everyone*/
    @Test
    public void send()  {
        GameObserver obs = new GameObserver();
        fObserver p1 = new fObserver();
        fObserver p2 = new fObserver();
        obs.register(1, p1);
        obs.register(2, p2);

        obs.notifyAll(Map.of(1,"s1",2,"s2"));

        assertEquals("s1", p1.last);
        assertEquals("s2", p2.last);
        assertEquals(1, p1.count);
        assertEquals(1, p2.count);
    }

    /*checks for a null message*/
    @Test
    public void nullMessage()  {
        GameObserver obs = new GameObserver();
        fObserver p1 = new fObserver();
        obs.register(1, p1);

        obs.notifyAll(null);
        obs.notifyAll(Map.of(1, null));
        assertNull(p1.last);
        assertEquals(0, p1.count);
    }

    /*checks remove observers*/
    @Test
    public void removesObserver()  {
        GameObserver obs = new GameObserver();
        FakeObserver p1 = new FakeObserver();
        obs.register(1, p1);
        obs.unregister(1);

        obs.notifyAll(Map.of(1, "x"));
        assertNull(p1.last);
        assertEquals(0, p1.count);
        assertEquals(0, obs.size());
    }

}
