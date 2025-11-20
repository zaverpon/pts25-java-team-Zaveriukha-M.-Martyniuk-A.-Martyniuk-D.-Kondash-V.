package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Represents the player's 5x5 grid in Terra Futura.
 *
 * The grid stores:
 *  - placed cards, mapped by GridPosition
 *  - which cards have been activated during the current turn
 *  - a chosen activation pattern for card activation order
 *
 * Grid enforces adjacency rules when placing cards, delegates activation
 * validation to the Card itself, and provides a JSON-based snapshot of its state.
 */
public final class Grid {

    /** All cards placed on the grid, keyed by their coordinate. */
    private final Map<GridPosition, Card> cards;

    /** Coordinates of cards that have already been activated this turn. */
    private final Set<GridPosition> activatedCards;

    /** The order in which cards will be activated (chosen by the player). */
    private List<GridPosition> activationPattern;


    /**
     * Creates an empty grid (initial state of a player).
     */
    public Grid() {
        this.cards = new HashMap<>();
        this.activatedCards = new HashSet<>();
        this.activationPattern = new ArrayList<>();
    }

    /**
     * Creates a grid from an existing card map (mostly for testing).
     */
    public Grid(Map<GridPosition, Card> cards) {
        this.cards = new HashMap<>(cards);
        this.activatedCards = new HashSet<>();
        this.activationPattern = new ArrayList<>();
    }


    /**
     * Retrieves the card located at the given coordinate.
     *
     * @param coordinate the grid position
     * @return Optional containing the card, or empty if there is no card
     */
    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.ofNullable(cards.get(coordinate));
    }


    /**
     * Determines whether a card can be placed at the given coordinate.
     * Rules:
     *  - You cannot place a card on an already occupied position.
     *  - You can place a card only adjacent to an existing card.
     *  - Exception: the first card may be placed at the starting position (0,0).
     */
    public boolean canPutCard(GridPosition coordinate) {
        if (cards.containsKey(coordinate)) return false;

        // adjacency rule: at least one neighboring cell must be occupied
        for (GridPosition pos : cards.keySet()) {
            int dx = Math.abs(pos.x - coordinate.x);
            int dy = Math.abs(pos.y - coordinate.y);
            if (dx + dy == 1) {
                return true;
            }
        }

        // first card must be placed at (0,0)
        return cards.isEmpty() && coordinate == GridPosition.X0_Y0;
    }


    /**
     * Places a card onto the grid if allowed.
     */
    public void putCard(GridPosition coordinate, Card card) {
        if (canPutCard(coordinate)) {
            cards.put(coordinate, card);
        }
    }


    /**
     * Checks whether a card at the given coordinate can be activated this turn.
     *
     * Requirements:
     *  - The card exists at that position.
     *  - It has not been activated earlier in this turn.
     */
    public boolean canBeActivated(GridPosition coordinate) {
        return cards.containsKey(coordinate) && !activatedCards.contains(coordinate);
    }


    /**
     * Marks a card as activated for the current turn.
     */
    public void setActivated(GridPosition coordinate) {
        if (canBeActivated(coordinate)) {
            activatedCards.add(coordinate);
        }
    }


    /**
     * Sets the order in which cards will be activated.
     */
    public void setActivationPattern(List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }


    /**
     * Ends the current turn and resets all activation states.
     */
    public void endTurn() {
        activatedCards.clear();
    }


    /**
     * Creates a JSON snapshot of the grid state.
     *
     * Structure:
     * {
     *   "cards": [
     *       { "pos": "(0,0)", "card": { ...card state... } },
     *       { "pos": "(1,0)", "card": { ... } }
     *   ],
     *   "activated": ["(0,0)", "(1,0)"],
     *   "pattern": ["(0,0)", "(1,0)", "(0,1)"]
     * }
     */
    public String state() {
        JSONObject result = new JSONObject();

        // ----- Cards section -----
        JSONArray cardArray = new JSONArray();
        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            JSONObject one = new JSONObject();
            one.put("pos", entry.getKey().toString());
            one.put("card", new JSONObject(entry.getValue().state()));
            cardArray.put(one);
        }
        result.put("cards", cardArray);

        // ----- Activated cards -----
        JSONArray activatedArray = new JSONArray();
        for (GridPosition pos : activatedCards) {
            activatedArray.put(pos.toString());
        }
        result.put("activated", activatedArray);

        // ----- Activation pattern -----
        JSONArray patternArray = new JSONArray();
        for (GridPosition pos : activationPattern) {
            patternArray.put(pos.toString());
        }
        result.put("pattern", patternArray);

        return result.toString();
    }
}
