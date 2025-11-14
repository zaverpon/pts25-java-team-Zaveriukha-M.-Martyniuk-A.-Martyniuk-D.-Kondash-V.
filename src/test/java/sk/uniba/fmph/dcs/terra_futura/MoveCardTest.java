package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.*;

public class MoveCardTest  {
    private Pile pile;
    private Grid grid;
    private Card vis1, vis2, hid;

    @Before
    public void setUp()  {
        grid = new Grid();
        vis1 = new Card(new Resource[]{}, 0);
        vis2 = new Card(new Resource[]{}, 0);
        hid = new Card(new Resource[]{}, 0);
    }

    /*checks movements from hidden to visible cards*/
    @Test
    public void reffFromHidd()  {
        pile = new Pile(List.of(vis1, vis2), List.of(hid));
        MoveCard mov = new MoveCard(pile, grid);

        boolean t = mov.moveCard(1, GridPosition.X0_Y0);
        assertTrue(t);

        assertTrue(grid.state().contains("(0, 0)"));

        assertTrue(pile.getCard(1).isPresent());
        assertSame(hid, pile.getCard(1).get());
        assertSame(vis1, pile.getCard(0).get());


    }

    /*if there are no more hidden cards in the pile*/
    @Test
    public void hiddEmpty()  {
        pile = new Pile(List.of(vis1, vis2));
        MoveCard mov = new MoveCard(pile, grid);

        boolean t = mov.moveCard(1, GridPosition.X0_Y0);
        assertTrue(t);


        assertSame(vis1, pile.getCard(0).get());
        assertFalse(pile.getCard(1).isPresent());
    }

    /*checking for an invalid index*/
    @Test
    public void badIndex()  {
        pile = new Pile(List.of(vis1, vis2));
        MoveCard mov = new MoveCard(pile, grid);

        boolean t = mov.moveCard(5, GridPosition.X0_Y0);
        assertFalse(t);

        assertSame(vis1, pile.getCard(0).get());
        assertSame(vis2, pile.getCard(1).get());

        assertFalse(grid.state().contains("(0, 0)"));
    }
}
