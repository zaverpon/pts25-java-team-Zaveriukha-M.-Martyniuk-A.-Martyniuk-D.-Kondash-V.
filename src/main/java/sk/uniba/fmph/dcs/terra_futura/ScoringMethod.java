package sk.uniba.fmph.dcs.terra_futura;

import org.json.*;
import java.util.*;

public final class ScoringMethod {
    private final List<Resource> resources;
    private final Points pointsPerCombination;
    private Points calculatedTotal;

    private final List<Resource> availableResources; // player resources (multiset)

    public ScoringMethod(final List<Resource> resources,
                         final Points pointsPerCombination,
                         final List<Resource> availableResources) {
        if (resources == null) {
            throw new IllegalArgumentException("resources is null");
        }
        if (pointsPerCombination == null) {
            throw new IllegalArgumentException("pointsPerCombination is null");
        }
        if (availableResources == null) {
            throw new IllegalArgumentException("availableResources is null");
        }

        this.resources = new ArrayList<>(resources);
        this.pointsPerCombination = pointsPerCombination;
        this.availableResources = new ArrayList<>(availableResources);
        this.calculatedTotal = null;
    }

    /**
     * Calculates the number of complete combinations of resources from the resources field
     * in the availableResources set, then multiplies it by pointsPerCombination.
     *
     * The result is stored in calculatedTotal. Repeated calls are idempotent,
     * since the input data does not change.
     */
    public void selectThisMethodAndCalculate() {
        Map<Resource, Integer> requiredCounts = new EnumMap<>(Resource.class);
        for(Resource r : resources){
            requiredCounts.put(r, requiredCounts.getOrDefault(r,0) + 1);
        }

        if(requiredCounts.isEmpty()){
            this.calculatedTotal = new Points(0);
            return;
        }

        // Calculation of the player's actual resources
        Map<Resource,Integer> availableCounts = new EnumMap<>(Resource.class);
        for(Resource r: availableResources){
            availableCounts.put(r, availableCounts.getOrDefault(r,0) + 1);
        }

        // Calculate the minimum for all resources
        int minCombinations = Integer.MAX_VALUE;
        for(Map.Entry<Resource,Integer> e : requiredCounts.entrySet()){
            Resource r = e.getKey();
            int req = e.getValue();
            int have = availableCounts.getOrDefault(r,0);
            int combosForResource = have / req;
            if(combosForResource < minCombinations){
                minCombinations = combosForResource;
            }
        }

        if(minCombinations == Integer.MAX_VALUE){
            minCombinations = 0;
        }

        int total = pointsPerCombination.getValue() * minCombinations;
        this.calculatedTotal = new Points(total);
    }

    public String state() {
        JSONObject json = new JSONObject();

        JSONArray resArr = new JSONArray();
        for (Resource r : resources) {
            resArr.put(r.name());
        }
        json.put("resources", resArr);
        json.put("pointsPerCombination", pointsPerCombination.getValue());

        if (calculatedTotal == null) {
            json.put("calculatedTotal", JSONObject.NULL);
        } else {
            json.put("calculatedTotal", calculatedTotal.getValue());
        }

        return json.toString();
    }
}
