package sk.uniba.fmph.dcs.terra_futura;

import java.util.Optional;

public class MoveCard  {
    private int index;

    public MoveCard(int index)  {
        this.index = index;
    }

    public boolean moveCard(Pile pile, GridPosition dest, Grid grid)  {
        if (pile == null || dest == null || grid == null)  return false;

        Optional<Card> card = pile.getCard(index);
        if (!card.isPresent())  return false;

        if (!grid.canPutCard(dest))  return false;

        grid.putCard(dest, card.get());
        pile.takeCard(index);
        return true;
    }
}