package sk.uniba.fmph.dcs.terra_futura;

import java.util.*;

public class Grid {
    private final Map<GridPosition, Card> cards;
    private final Set<GridPosition> activatedCards;
    private List<GridPosition> activationPattern;

    public Grid() {
        cards = new HashMap<>();
        activatedCards = new HashSet<>();
        activationPattern = new ArrayList<>();
    }
    public Grid(Map<GridPosition, Card> cards) {
        this.cards = new HashMap<>(cards);
        this.activatedCards = new HashSet<>();
        this.activationPattern = new ArrayList<>();
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.ofNullable(cards.get(coordinate));
    }

    public boolean canPutCard(GridPosition coordinate) {
        if (cards.containsKey(coordinate)) return false;

        for (GridPosition pos: cards.keySet()) {
            int dx = Math.abs(pos.x - coordinate.x);
            int dy = Math.abs(pos.y - coordinate.y);
            if (dx + dy == 1) return true;
        }

        return cards.isEmpty() && coordinate == GridPosition.X0_Y0;
    }

    public void putCard(GridPosition coordinate, Card card) {
        if (canPutCard(coordinate)) {
            cards.put(coordinate, card);
        }
    }

    public boolean canBeActivated(GridPosition coordinate) {
        return cards.containsKey(coordinate) && !activatedCards.contains(coordinate);
    }

    public void setActivated(GridPosition coordinate){
        if (canBeActivated(coordinate)){
            activatedCards.add(coordinate);
        }
    }

    public void setActivationPattern(List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }

    public void endTurn(){
        activatedCards.clear();
    }

    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"cards\": [");
        int count = 0;
        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            if (count++ > 0) sb.append(", ");
            sb.append("{ \"pos\": \"").append(entry.getKey().toString())
                    .append("\", \"card\": ").append(entry.getValue().state()).append(" }");
        }
        sb.append("], ");

        sb.append("\"activated\": [");
        count = 0;
        for (GridPosition pos : activatedCards) {
            if (count++ > 0) sb.append(", ");
            sb.append("\"").append(pos.toString()).append("\"");
        }
        sb.append("], ");

        sb.append("\"pattern\": [");
        count = 0;
        for (GridPosition pos : activationPattern) {
            if (count++ > 0) sb.append(", ");
            sb.append("\"").append(pos.toString()).append("\"");
        }
        sb.append("] }");
        return sb.toString();
    }
}
