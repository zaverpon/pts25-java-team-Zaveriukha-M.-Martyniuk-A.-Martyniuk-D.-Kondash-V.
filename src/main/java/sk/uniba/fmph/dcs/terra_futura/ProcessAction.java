package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import java.util.*;

public final class ProcessAction {

    public boolean activateCard(
            final Card card,
            final Grid grid,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution
    ) {
        if (card == null || grid == null || inputs == null || outputs == null || pollution == null) {
            return false;
        }

        // 1) Group resources by GridPosition and also collect flat lists for card.check(...)
        Map<GridPosition, List<Resource>> inByPos = groupByPosition(inputs);
        Map<GridPosition, List<Resource>> outByPos = groupByPosition(outputs);
        List<Resource> flatInputs = flatten(inputs);
        List<Resource> flatOutputs = flatten(outputs);

        // 2) Validate that referenced positions exist on the grid
        if (!allPositionsExist(grid, inByPos) || !allPositionsExist(grid, outByPos)) {
            return false;
        }

        // 3) Validate effect against the activated card (uses counts only; pollution size is taken)
        int pollutionCount = pollution.size();
        if (!card.check(flatInputs, flatOutputs, pollutionCount)) {
            return false;
        }

        // 4) Validate that each source card can pay its listed inputs
        for (Map.Entry<GridPosition, List<Resource>> e : inByPos.entrySet()) {
            Optional<Card> c = grid.getCard(e.getKey());
            if (!c.isPresent() || !c.get().canPutResources(e.getValue())) {
                return false;
            }
        }

        // 5) Validate that each destination card can accept its listed outputs
        for (Map.Entry<GridPosition, List<Resource>> e : outByPos.entrySet()) {
            Optional<Card> c = grid.getCard(e.getKey());
            if (!c.isPresent() || !c.get().canGetResources(e.getValue())) {
                return false;
            }
        }

        // All checks passed -> apply changes atomically

        // 6) Pay inputs (remove resources from specified cards)
        for (Map.Entry<GridPosition, List<Resource>> e : inByPos.entrySet()) {
            grid.getCard(e.getKey()).get().putResources(e.getValue());
        }

        // 7) Grant outputs (add resources to specified cards)
        for (Map.Entry<GridPosition, List<Resource>> e : outByPos.entrySet()) {
            grid.getCard(e.getKey()).get().getResources(e.getValue());
        }

        return true;
    }

    //helpers
    private static Map<GridPosition, List<Resource>> groupByPosition(
            final List<Pair<Resource, GridPosition>> pairs
    ) {
        Map<GridPosition, List<Resource>> map = new HashMap<>();
        for (Pair<Resource, GridPosition> p : pairs) {
            GridPosition pos = p.getValue();
            Resource res = p.getKey();
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
            list.add(p.getKey());
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
