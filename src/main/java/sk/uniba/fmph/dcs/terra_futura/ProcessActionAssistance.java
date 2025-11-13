package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import java.util.*;

public final class ProcessActionAssistance {
    private final SelectReward selectReward;
    public ProcessActionAssistance(SelectReward selectReward){
        if (selectReward == null) {
            throw new IllegalArgumentException("selectReward is null");
        }
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
        if (card == null || grid == null || assisingCard == null
                || inputs == null || outputs == null || pollution == null) {
            return false;
        }
        // The activated card MUST have Assistance.
        if (!card.hasAssistance()) {
            return false;
        }

        // group resources by position and compile “flat” lists to verify the effect
        Map<GridPosition, List<Resource>> inByPos = groupByPosition(inputs);
        Map<GridPosition, List<Resource>> outByPos = groupByPosition(outputs);
        List<Resource> flatInputs = flatten(inputs);
        List<Resource> flatOutputs = flatten(outputs);

        // check that all specified positions exist on the grid
        if (!allPositionsExist(grid, inByPos) || !allPositionsExist(grid, outByPos)) {
            return false;
        }

        // checking the LOWER effect of another player's card (assisingCard)
        int pollutionCount = pollution.size();
        if (!assisingCard.checkLower(flatInputs, flatOutputs, pollutionCount)) {
            return false;
        }

        // verification that each source card can pay its resources
        for (Map.Entry<GridPosition, List<Resource>> e : inByPos.entrySet()) {
            Optional<Card> c = grid.getCard(e.getKey());
            if (!c.isPresent() || !c.get().canPutResources(e.getValue())) {
                return false;
            }
        }

        // verification that each receiver card can receive its resources
        for (Map.Entry<GridPosition, List<Resource>> e : outByPos.entrySet()) {
            Optional<Card> c = grid.getCard(e.getKey());
            if (!c.isPresent() || !c.get().canGetResources(e.getValue())) {
                return false;
            }
        }

        // All checks passed -> apply changes atomically
        // 1) write off resources (payments) from cards
        for (Map.Entry<GridPosition, List<Resource>> e : inByPos.entrySet()) {
            grid.getCard(e.getKey()).get().putResources(e.getValue());
        }

        // 2) output the results to the corresponding cards
        for (Map.Entry<GridPosition, List<Resource>> e : outByPos.entrySet()) {
            grid.getCard(e.getKey()).get().getResources(e.getValue());
        }

        // 3) Set up SelectReward for another player:
        //    The reward is a list of resource types that were paid (flatInputs)
        Resource[] paidResources = flatInputs.toArray(new Resource[0]);
        selectReward.setReward(assisingPlayer, assisingCard, paidResources);

        return true;
    }

    //helpers
    private static Map<GridPosition, List<Resource>> groupByPosition(
            final List<Pair<Resource, GridPosition>> pairs
    ) {
        Map<GridPosition, List<Resource>> map = new HashMap<>();
        for (Pair<Resource, GridPosition> p : pairs) {
            GridPosition pos = p.getRight(); // або getValue()
            Resource res = p.getLeft();      // або getKey()
            List<Resource> list = map.get(pos);
            if (list == null) {
                list = new ArrayList<>();
                map.put(pos, list);
            }
            list.add(res);
        }
        return map;
    }
    private static List<Resource> flatten(final List<Pair<Resource, GridPosition>> pairs) {
        List<Resource> list = new ArrayList<>();
        for (Pair<Resource, GridPosition> p : pairs) {
            list.add(p.getLeft());
        }
        return list;
    }
    private static boolean allPositionsExist(final Grid grid, final Map<GridPosition, List<Resource>> byPos) {
        for (GridPosition pos : byPos.keySet()) {
            if (!grid.getCard(pos).isPresent()) {
                return false;
            }
        }
        return true;
    }
}
