package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/*Integration tests for Pile class */

public class PileIntegrationTest {

    /*
    * test1
    *Scenario: Select a visible card vis1 from the Pile,
    *place it in the Grid, and insert a card hid from the hidden cards
    *in place of the one selected by index.
    */
    @Test
    public void remToGrid_rellFromHidd()  {
        Grid grid = new Grid();
        Card vis1 = new Card(new Resource[]{}, 1);
        Card vis2 = new Card(new Resource[]{}, 1);
        Card hid = new Card(new Resource[]{}, 1);
        Pile pile = new Pile(List.of(vis1, vis2), List.of(hid));
        MoveCard mov = new MoveCard(0);

        assertTrue(mov.moveCard(pile, GridPosition.X0_Y0, grid));

        assertTrue(grid.state().contains("(0, 0)"));
        assertTrue(pile.getCard(0).isPresent());
        assertSame(hid, pile.getCard(0).get());
        assertSame(vis2, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());

    }

    /*
    test2
    Scenario: Use removeLastCard.
    Insert a card from the hidden list in place of the removed card.
    Select the card to insert into the Grid.
    */
    @Test
    public void remLastCard_rellFromHidd2()  {
        Grid grid = new Grid();
        Card vis1 = new Card(new Resource[]{}, 1);
        Card vis2 = new Card(new Resource[]{}, 1);
        Card hid1 = new Card(new Resource[]{}, 1);
        Card hid2 = new Card(new Resource[]{}, 1);
        Pile pile = new Pile(List.of(vis1, vis2), List.of(hid1, hid2));
        MoveCard mov = new MoveCard(1);

        pile.removeLastCard();

        assertSame(vis2, pile.getCard(0).get());
        assertSame(hid2, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());

        assertTrue(mov.moveCard(pile, GridPosition.X0_Y0, grid));

        assertTrue(grid.state().contains("(0, 0)"));
        assertSame(vis2, pile.getCard(0).get());
        assertSame(hid1, pile.getCard(1).get());
        assertFalse(pile.getCard(2).isPresent());




    }

}
