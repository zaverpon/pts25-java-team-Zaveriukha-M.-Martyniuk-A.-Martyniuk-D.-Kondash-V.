package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public final class Game implements TerraFuturaInterface {

    private final int playerCount;
    private int onTurn;
    private int startingPlayer;
    private int turnNumber;

    private GameState state;
    private final TerraFuturaComponent component;

    public Game(
            int playerCount,
            GameObserver observer,
            Map<Integer, Map<Deck, Pile>> initialPiles,
            Map<Integer, Grid> initialGrids,
            Map<Integer, List<ActivationPattern>> patterns,
            Map<Integer, List<ScoringMethod>> scorings
    ) {
        this.playerCount = playerCount;
        this.onTurn = 0;
        this.startingPlayer = 0;
        this.turnNumber = 1;
        this.state = GameState.TAKE_CARD_NO_CARD_DISCARDED;

        this.component = new TerraFuturaComponent(
                playerCount,
                observer,
                initialPiles,
                initialGrids,
                patterns,
                scorings
        );
    }

    private boolean invalidPlayer(int id) {
        return id < 0 || id >= playerCount;
    }

    @Override
    public boolean takeCard(int playerId, CardSource source, GridPosition destination) {
        if (invalidPlayer(playerId) || playerId != onTurn) return false;

        if (state != GameState.TAKE_CARD_NO_CARD_DISCARDED &&
                state != GameState.TAKE_CARD_CARD_DISCARDED)
            return false;

        boolean ok = component.takeCard(playerId, source, destination);

        if (ok) {
            state = GameState.ACTIVATE_CARD;
        }
        return ok;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        if (invalidPlayer(playerId) || playerId != onTurn) return false;

        if (state != GameState.TAKE_CARD_NO_CARD_DISCARDED)
            return false;

        boolean ok = component.discardLastCardFromDeck(playerId, deck);

        if (ok) {
            state = GameState.TAKE_CARD_CARD_DISCARDED;
        }
        return ok;
    }

    @Override
    public boolean activateCard(
            int playerId,
            GridPosition card,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Integer otherPlayer,
            GridPosition otherCard
    ) {
        if (invalidPlayer(playerId) || playerId != onTurn) return false;

        if (state != GameState.ACTIVATE_CARD)
            return false;

        boolean ok = component.activateCard(
                playerId,
                card,
                inputs,
                outputs,
                pollution,
                otherPlayer,
                otherCard
        );

        if (!ok) return false;


        boolean wasAssistance = (otherPlayer != null);

        if (wasAssistance) {
            state = GameState.SELECT_REWARD;
        } else {
            state = GameState.ACTIVATE_CARD;
        }

        return true;
    }

    @Override
    public boolean selectReward(int playerId, Resource resource) {
        if (invalidPlayer(playerId)) return false;

        if (state != GameState.SELECT_REWARD)
            return false;

        boolean ok = component.selectReward(playerId, resource);

        if (ok) {
            state = GameState.ACTIVATE_CARD;
        }
        return ok;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int patternId) {
        if (invalidPlayer(playerId) || playerId != onTurn) return false;

        if (state != GameState.ACTIVATE_CARD)
            return false;

        boolean ok = component.selectActivationPattern(playerId, patternId);

        if (ok) {
            state = GameState.ACTIVATE_CARD;
        }
        return ok;
    }

    @Override
    public boolean selectScoring(int playerId, int scoringId) {
        if (invalidPlayer(playerId) || playerId != onTurn) return false;

        if (state != GameState.ACTIVATE_CARD &&
                state != GameState.SELECT_SCORING_METHOD)
            return false;

        boolean ok = component.selectScoring(playerId, scoringId);

        if (!ok) return false;

        if (state == GameState.ACTIVATE_CARD) {
            state = GameState.SELECT_SCORING_METHOD;
        } else {
            state = GameState.FINISH;
        }
        return true;
    }
    @Override
    public boolean turnFinished(int playerId) {
        if (invalidPlayer(playerId) || playerId != onTurn) return false;

        if (state != GameState.FINISH)
            return false;

        boolean ok = component.turnFinished(playerId);

        if (!ok) return false;

        onTurn = (onTurn + 1) % playerCount;
        turnNumber++;

        state = GameState.TAKE_CARD_NO_CARD_DISCARDED;

        return true;
    }

    public GameState getState() {
        return state;
    }

    public int getOnTurn() {
        return onTurn;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

}
