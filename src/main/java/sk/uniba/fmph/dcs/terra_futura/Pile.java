package sk.uniba.fmph.dcs.terra_futura;

import java.util.*;

public class Pile  {
    private List<Card> visibleCards;
    private Deque<Card> hiddenCards;

    public Pile(List<Card> visible)  {
        if (visible.size() > 4)  {
            throw new IllegalArgumentException("visibleCards must be <= 4");
        }
        this.visibleCards = new ArrayList<>(visible);
        this.hiddenCards = new ArrayDeque<>();
    }

    public Pile(List<Card> visible , List<Card> hidden)  {
        if (visible.size() > 4)  {
            throw new IllegalArgumentException("visibleCards must be <= 4");
        }
        this.visibleCards = new ArrayList<>(visible);
        this.hiddenCards = new ArrayDeque<>(hidden);
    }

    public Optional<Card> getCard(int index)  {
        if (index >= 0 && index < visibleCards.size())  {
            return Optional.of(visibleCards.get(index));
        }  else {
            return Optional.empty();
        }
    }

    public void takeCard(int index)  {
        if (index < 0 || index >= visibleCards.size()) {
            throw new IllegalArgumentException("illegal index");
        }
            visibleCards.remove(index);
            if (!hiddenCards.isEmpty()) {
                visibleCards.add(index, hiddenCards.removeLast());

        }
    }

    public void removeLastCard()  {
        if (visibleCards.isEmpty()) return;
        visibleCards.remove(0);
        if (!hiddenCards.isEmpty())  {
            visibleCards.add(hiddenCards.removeLast());
        }
    }

    public String state()  {
        return "Pile: visible - " + visibleCards.size() + ", hidden - " + hiddenCards.size();
    }
}
