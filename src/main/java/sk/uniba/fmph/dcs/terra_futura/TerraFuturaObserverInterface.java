package sk.uniba.fmph.dcs.terra_futura;

@FunctionalInterface
public interface TerraFuturaObserverInterface  {
    void notify(String gameState);
}
