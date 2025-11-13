package sk.uniba.fmph.dcs.terra_futura;

import java.util.Map;
import java.util.HashMap;

public class GameObserver  {
    private final Map<Integer, TerraFuturaObserverInterface> obss = new HashMap<>();

    public void register (int PId, TerraFuturaObserverInterface obs )  {
        if (obs == null) {
            throw new IllegalArgumentException("observer is null");
        }

        obss.put(PId, obs);
    }

    public void unregister (int PId)  {
        obss.remove(PId);
    }

    public int size ()  {
        return obss.size();
    }

    public void notifyAll(Map <Integer, String> gameState)  {
        if (gameState == null)  return;

        gameState.forEach((PId,message) ->  {
            TerraFuturaObserverInterface obs = obss.get(PId);
            if (message == null) {
                return;
            }
            if (obs != null)  {
                obs.notify(message);
            }
        });
        }
    }

