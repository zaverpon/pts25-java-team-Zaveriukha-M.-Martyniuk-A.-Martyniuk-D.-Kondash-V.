package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ProcessActionAssistance {
    private SelectReward selectReward;
    public ProcessActionAssistance(SelectReward selectReward){
        this.selectReward = selectReward;
    }

    public boolean activateCard(
            final Card card,
            final Grid grid,
            final int assisingPlayer,
            final Card assisingCard,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution
    ){

        return true;
    }
}
