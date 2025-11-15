package sk.uniba.fmph.dcs.terra_futura;

public enum GameState  {
    TAKE_CARD_NO_CARD_DISCARDED("Take card (no discard)", false),
    TAKE_CARD_CARD_DISCARDED("Take card ( discard)", false),
    ACTIVATE_CARD("Activate card", false),
    SELECT_REWARD("Select reward", false),
    SELECT_ACTIVATION_PATTERN("Select activation pattern", false),
    SELECT_SCORING_METHOD("Select scoring method", false),
    FINISH("Finish", true);

    private final String state;
    private final boolean isFinish;
    GameState(String state, boolean isFinish)  {
        this.state = state;
        this.isFinish = isFinish;
    }


    public String getState()  {
        return state;
    }

    public boolean isFinish()  {
        return isFinish;
    }

    @Override
    public String toString()  {
        return "State of the game:" + state;
    }

}
