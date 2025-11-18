package sk.uniba.fmph.dcs.terra_futura;

import java.util.Map;

public class GameObserver  {
    private final Map<Integer, TerraFuturaObserverInterface> observers;

    public GameObserver(Map<Integer, TerraFuturaObserverInterface> observers)  {
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

