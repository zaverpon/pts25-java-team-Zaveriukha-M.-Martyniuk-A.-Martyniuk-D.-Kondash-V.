package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import java.util.*;

public final class TerraFuturaComponent implements TerraFuturaInterface {

    private final int playerCount;
    private final GameObserver observer;


    private final Map<Integer, Map<Deck, Pile>> piles = new HashMap<>();
    private final Map<Integer, Grid> grids = new HashMap<>();
    private final Map<Integer, List<ActivationPattern>> activationPatterns = new HashMap<>();
    private final Map<Integer, List<ScoringMethod>> scoringMethods = new HashMap<>();

    private final ProcessAction processAction = new ProcessAction();
    private final SelectReward selectReward = new SelectReward();
    private final ProcessActionAssistance processActionAssistance =
            new ProcessActionAssistance(selectReward);

    public TerraFuturaComponent(
            int playerCount,
            GameObserver observer,
            Map<Integer, Map<Deck, Pile>> initialPiles,
            Map<Integer, Grid> initialGrids,
            Map<Integer, List<ActivationPattern>> patterns,
            Map<Integer, List<ScoringMethod>> scorings
    ) {
        if (observer == null ||
                initialPiles == null ||
                initialGrids == null ||
                patterns == null ||
                scorings == null) {
            throw new IllegalArgumentException("Constructor arguments cannot be null");
        }

        this.playerCount = playerCount;
        this.observer = observer;

        if (initialPiles.size() != playerCount ||
                initialGrids.size() != playerCount ||
                patterns.size() != playerCount ||
                scorings.size() != playerCount) {
            throw new IllegalArgumentException("Initial maps must contain entries for every player");
        }

        this.piles.putAll(initialPiles);
        this.grids.putAll(initialGrids);
        this.activationPatterns.putAll(patterns);
        this.scoringMethods.putAll(scorings);
    }

    private boolean invalidPlayer(int id) {
        return id < 0 || id >= playerCount;
    }


    private String buildPlayerState(int playerId) {
        JSONObject json = new JSONObject();
        json.put("grid", new JSONObject(grids.get(playerId).state()));
        json.put("pile_I", piles.get(playerId).get(Deck.LEVEL_I).state());
        json.put("pile_II", piles.get(playerId).get(Deck.LEVEL_II).state());
        json.put("reward", new JSONObject(selectReward.state()));
        return json.toString();
    }

    private void notifyPlayer(int playerId) {
        Map<Integer, String> states = new HashMap<>();
        states.put(playerId, buildPlayerState(playerId));
        observer.notifyAll(states);
    }

    @Override
    public boolean takeCard(int playerId, CardSource source, GridPosition destination) {
        if (invalidPlayer(playerId)) return false;
        if (source == null || destination == null) return false;

        Map<Deck, Pile> pilesOfPlayer = piles.get(playerId);
        Pile pile = pilesOfPlayer.get(source.getDeck());

        if (pile == null) return false;

        MoveCard mover = new MoveCard(source.getIndex());
        boolean ok = mover.moveCard(pile, destination, grids.get(playerId));

        if (ok) notifyPlayer(playerId);
        return ok;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        if (invalidPlayer(playerId) || deck == null) return false;

        Map<Deck, Pile> pilesOfPlayer = piles.get(playerId);
        Pile pile = pilesOfPlayer.get(deck);

        if (pile == null) return false;

        pile.removeLastCard();
        notifyPlayer(playerId);
        return true;
    }

    @Override
    public boolean activateCard(
            int playerId,
            GridPosition cardPos,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Integer otherPlayerId,
            GridPosition otherCardPosition
    ) {
        if (invalidPlayer(playerId) ||
                cardPos == null ||
                inputs == null ||
                outputs == null ||
                pollution == null)
            return false;

        Card card = grids.get(playerId).getCard(cardPos).orElse(null);
        if (card == null) return false;

        boolean ok;


        if (otherPlayerId != null && otherCardPosition != null) {
            if (invalidPlayer(otherPlayerId)) return false;
            if (Objects.equals(otherPlayerId, playerId)) return false;

            Card assistingCard = grids.get(otherPlayerId)
                    .getCard(otherCardPosition).orElse(null);

            if (assistingCard == null) return false;

            ok = processActionAssistance.activateCard(
                    card,
                    grids.get(playerId),
                    otherPlayerId,
                    assistingCard,
                    inputs,
                    outputs,
                    pollution
            );
        }

        else {
            ok = processAction.activateCard(
                    card,
                    grids.get(playerId),
                    inputs,
                    outputs,
                    pollution
            );
        }

        if (ok) notifyPlayer(playerId);
        return ok;
    }

    @Override
    public boolean selectReward(int playerId, Resource resource) {
        if (invalidPlayer(playerId) || resource == null) return false;

        try {
            selectReward.selectReward(resource);
            notifyPlayer(playerId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean turnFinished(int playerId) {
        if (invalidPlayer(playerId)) return false;

        grids.get(playerId).endTurn();
        notifyPlayer(playerId);
        return true;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int patternId) {
        if (invalidPlayer(playerId)) return false;

        List<ActivationPattern> list = activationPatterns.get(playerId);
        if (list == null || patternId < 0 || patternId >= list.size()) return false;

        try {
            list.get(patternId).select();
            notifyPlayer(playerId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean selectScoring(int playerId, int scoringId) {
        if (invalidPlayer(playerId)) return false;

        List<ScoringMethod> list = scoringMethods.get(playerId);
        if (list == null || scoringId < 0 || scoringId >= list.size()) return false;

        list.get(scoringId).selectThisMethodAndCalculate();
        notifyPlayer(playerId);
        return true;
    }
}
