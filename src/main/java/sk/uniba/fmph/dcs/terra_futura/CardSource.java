package sk.uniba.fmph.dcs.terra_futura;

public class CardSource {
    private final Deck deck;
    private final int index;

    public CardSource(Deck deck, int index) {
        this.deck = deck;
        this.index = index;
    }

    /** Returns the deck (LEVEL_I or LEVEL_II). */
    public Deck getDeck() {
        return deck;
    }

    /** Returns the index (0–3 for visible cards, -1 for top of deck). */
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "CardSource{" +
                "deck=" + deck +
                ", index=" + index +
                '}';
    }
}
