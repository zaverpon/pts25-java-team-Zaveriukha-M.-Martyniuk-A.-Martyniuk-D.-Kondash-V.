package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.Before;

import java.util.Optional;
import java.util.List;

import static org.junit.Assert.*;


/* unit tests for Pile class*/

public class PileTest {

    private Pile pile;
    private Card vis1, vis2, hid1, hid2;

    @Before
    public void setUp()  {
        vis1 = new Card(new Resource[0], 0);
        vis2 = new Card(new Resource[0], 1);
        hid1 = new Card(new Resource[0], 0);
        hid2 = new Card(new Resource[0], 1);

        pile =  new Pile(List.of(vis1, vis2), List.of(hid1, hid2));
    }

    /*checks if the State string is null*/
    @Test
    public void nullState() {
        assertNotNull(pile.state());
    }

    @Test
    public void takeCardTr()  {
        Optional<Card> c = pile.getCard(0);
        assertTrue(c.isPresent());
        assertEquals(vis1, c.get());

        pile.takeCard(0);

        assertTrue(pile.getCard(0).isPresent());
        assertSame(hid2, pile.getCard(0).get());
        assertTrue(pile.getCard(1).isPresent());
        assertSame(vis2, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());

    }

    /*checks getCard against existing cards*/
    @Test
    public void getCardTr()  {
        assertTrue(pile.getCard(0).isPresent());
    }

    /*checks getCard with non-existent cards*/
    @Test
    public void getCardFl()  {
        assertFalse(pile.getCard(-1).isPresent());
        assertFalse(pile.getCard(5).isPresent());
    }

    /*Checks whether removeLastCard works correctly when there are no hidden cards.*/
    @Test
    public void remLastCard()  {
        pile.removeLastCard();

        assertSame(vis2, pile.getCard(0).get());
        assertSame(hid2, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());

        pile.removeLastCard();

        assertSame(hid2, pile.getCard(0).get());
        assertSame(hid1, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());

        pile.removeLastCard();

        assertTrue(pile.getCard(0).isPresent());
        assertSame(hid1, pile.getCard(0).get());
        assertFalse(pile.getCard(1).isPresent());
    }

    /*checks whether the hidden card falls exactly on the index
     of the visible card that was taken*/
    @Test
    public void sameIndex()  {
        pile.takeCard(1);

        assertTrue(pile.getCard(1).isPresent());
        assertSame(hid2, pile.getCard(1).get());

        assertSame(vis1, pile.getCard(0).get());
        assertFalse(pile.getCard(2).isPresent());
    }

    /*Checks whether removeLastCard works correctly in the presence of hidden cards.*/
    @Test
    public void remLastCardWithHidd()  {
        pile.removeLastCard();

        assertSame(vis2, pile.getCard(0).get());
        assertSame(hid2, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());
    }


}
