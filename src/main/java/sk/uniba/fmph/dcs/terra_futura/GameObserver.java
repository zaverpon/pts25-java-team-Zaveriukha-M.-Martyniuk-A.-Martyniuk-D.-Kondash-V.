package sk.uniba.fmph.dcs.terra_futura;

import java.util.Map;

public final class GameObserver  {
    private final Map<Integer, TerraFuturaObserverInterface> observers;

    public GameObserver(final Map<Integer, TerraFuturaObserverInterface> observers) {
        if (observers == null) {
            throw new IllegalArgumentException("observers must not be null");
        }
        this.observers = observers;
    }

    public void notifyAll(Map <Integer, String> gameState)  {
        if (gameState == null)  return;

        gameState.forEach((PId,message) ->  {
            TerraFuturaObserverInterface obs = observers.get(PId);
            if (message == null) {
                return;
            }
            if (obs != null)  {
                obs.notify(message);
            }
        });
        }
    }

