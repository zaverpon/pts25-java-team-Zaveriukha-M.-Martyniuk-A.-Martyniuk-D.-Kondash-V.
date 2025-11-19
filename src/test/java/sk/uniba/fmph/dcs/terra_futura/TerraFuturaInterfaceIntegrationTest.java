package sk.uniba.fmph.dcs.terra_futura;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TerraFuturaInterfaceIntegrationTest {

    /**
     * End-to-end integration test for 4 players.
     */
    @Test
    public void endToEndGameForFourPlayers() {

        GameObserver observer = new GameObserver(new HashMap<>());
        int players = 4;

        Map<Integer, Map<Deck, Pile>> piles = new HashMap<>();
        Map<Integer, Grid> grids = new HashMap<>();
        Map<Integer, List<ActivationPattern>> patterns = new HashMap<>();
        Map<Integer, List<ScoringMethod>> scorings = new HashMap<>();

        for (int p = 0; p < players; p++) {

            Map<Deck, Pile> playerPiles = new HashMap<>();
            playerPiles.put(Deck.LEVEL_I, new Pile(List.of(new Card(new Resource[]{}, 0))));
            playerPiles.put(Deck.LEVEL_II, new Pile(List.of(new Card(new Resource[]{}, 0))));
            piles.put(p, playerPiles);

            grids.put(p, new Grid());
            patterns.put(p, List.of(new ActivationPattern(null, List.of())));
            scorings.put(p, List.of(
                    new ScoringMethod(
                            List.of(Resource.Green),
                            new Points(1),
                            List.of(Resource.Green)
                    )
            ));
        }

        Game game = new Game(players, observer, piles, grids, patterns, scorings);
        TerraFuturaInterface api = game;

        for (int p = 0; p < players; p++) {

            assertEquals(p, game.getOnTurn(), "Player turn mismatch");
            assertEquals(GameState.TAKE_CARD_NO_CARD_DISCARDED, game.getState());

            boolean ok = api.takeCard(
                    p,
                    new CardSource(Deck.LEVEL_I, 0),
                    GridPosition.X0_Y0
            );

            assertTrue(ok);
            assertEquals(GameState.ACTIVATE_CARD, game.getState());

            ok = api.activateCard(
                    p,
                    GridPosition.X0_Y0,
                    List.of(),
                    List.of(),
                    List.of(),
                    null, null
            );

            assertTrue(ok);
            assertEquals(GameState.ACTIVATE_CARD, game.getState());

            ok = api.selectScoring(p, 0);
            assertTrue(ok);
            assertEquals(GameState.SELECT_SCORING_METHOD, game.getState());

            ok = api.selectScoring(p, 0);
            assertTrue(ok);
            assertEquals(GameState.FINISH, game.getState());

            int beforeTurnNumber = game.getTurnNumber();
            ok = api.turnFinished(p);
            assertTrue(ok);

            assertEquals(beforeTurnNumber + 1, game.getTurnNumber());
            assertEquals(GameState.TAKE_CARD_NO_CARD_DISCARDED, game.getState());
        }

        assertEquals(0, game.getOnTurn());
    }


    /**
     * End-to-end integration test for 3 players.
     */
    @Test
    public void fullTurnCycle_threePlayers_endToEnd() {

        GameObserver observer = new GameObserver(new HashMap<>());
        int players = 3;

        Map<Integer, Map<Deck, Pile>> piles = new HashMap<>();
        Map<Integer, Grid> grids = new HashMap<>();
        Map<Integer, List<ActivationPattern>> patterns = new HashMap<>();
        Map<Integer, List<ScoringMethod>> scorings = new HashMap<>();

        for (int p = 0; p < players; p++) {

            Map<Deck, Pile> pPiles = new HashMap<>();
            pPiles.put(Deck.LEVEL_I, new Pile(List.of(new Card(new Resource[]{}, 0))));
            pPiles.put(Deck.LEVEL_II, new Pile(List.of(new Card(new Resource[]{}, 0))));
            piles.put(p, pPiles);

            grids.put(p, new Grid());
            patterns.put(p, List.of(new ActivationPattern(null, List.of())));
            scorings.put(p, List.of(
                    new ScoringMethod(
                            List.of(Resource.Green),
                            new Points(1),
                            List.of(Resource.Green)
                    )
            ));
        }

        Game game = new Game(players, observer, piles, grids, patterns, scorings);
        TerraFuturaInterface api = game;

        for (int p = 0; p < players; p++) {

            assertEquals(p, game.getOnTurn(), "Wrong player turn");
            assertEquals(GameState.TAKE_CARD_NO_CARD_DISCARDED, game.getState());

            boolean ok = api.takeCard(
                    p,
                    new CardSource(Deck.LEVEL_I, 0),
                    GridPosition.X0_Y0
            );

            assertTrue(ok);
            assertEquals(GameState.ACTIVATE_CARD, game.getState());

            ok = api.activateCard(
                    p,
                    GridPosition.X0_Y0,
                    List.of(),
                    List.of(),
                    List.of(),
                    null, null
            );

            assertTrue(ok);
            assertEquals(GameState.ACTIVATE_CARD, game.getState());

            ok = api.selectScoring(p, 0);
            assertTrue(ok);
            assertEquals(GameState.SELECT_SCORING_METHOD, game.getState());

            ok = api.selectScoring(p, 0);
            assertTrue(ok);
            assertEquals(GameState.FINISH, game.getState());

            int beforeTurnNumber = game.getTurnNumber();
            ok = api.turnFinished(p);
            assertTrue(ok);

            assertEquals(beforeTurnNumber + 1, game.getTurnNumber());
            assertEquals(GameState.TAKE_CARD_NO_CARD_DISCARDED, game.getState());
        }

        assertEquals(0, game.getOnTurn(), "Turn should return to player 0");
    }
}