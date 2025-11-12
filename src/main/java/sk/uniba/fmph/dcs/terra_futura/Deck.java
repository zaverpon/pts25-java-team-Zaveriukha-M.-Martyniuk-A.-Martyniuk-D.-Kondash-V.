package sk.uniba.fmph.dcs.terra_futura;

public enum Deck {
    LEVEL_I(1),
    LEVEL_II(2);

    private final int level;
    Deck(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Level " + level;
    }
}
