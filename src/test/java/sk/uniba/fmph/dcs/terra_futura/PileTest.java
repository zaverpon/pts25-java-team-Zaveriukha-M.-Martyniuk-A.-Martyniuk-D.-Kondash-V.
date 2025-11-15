package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.Before;

import java.util.Optional;
import java.util.List;

import static org.junit.Assert.*;


/* unit tests for Pile class*/

public class PileTest {

    private Pile pile;
    private Card card1, card2;

    @Before
    public void setUp()  {
        card1 = new Card(new Resource[0], 0);
        card2 = new Card(new Resource[0], 1);

        pile =  new Pile(List.of(card1, card2));
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
        assertEquals(card1, c.get());

        pile.takeCard(0);

        assertTrue(pile.getCard(0).isPresent());
        assertEquals(card2, pile.getCard(0).get());
        assertFalse(pile.getCard(1).isPresent());

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

    @Test
    public void remLastCard()  {
        pile.removeLastCard();
        pile.removeLastCard();

        pile.removeLastCard();
        assertFalse(pile.getCard(0).isPresent());
    }
}
