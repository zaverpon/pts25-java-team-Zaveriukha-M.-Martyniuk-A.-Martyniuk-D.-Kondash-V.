package sk.uniba.fmph.dcs.terra_futura;


import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface TerraFuturaInterface {
    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    boolean discardLastCardFromDeck(int playerId, Deck deck);

    boolean activateCard(
            int playerId,
            GridPosition card,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Integer otherPlayerId,
            GridPosition otherCard
    );

    boolean selectReward(int playerId, Resource resource);

    boolean turnFinished(int playerId);

    boolean selectActivationPattern(int playerId, int patternId);

    boolean selectScoring(int playerId, int scoringId);
}
