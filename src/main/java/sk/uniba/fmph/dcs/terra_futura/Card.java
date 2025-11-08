package sk.uniba.fmph.dcs.terra_futura;

import java.util.*;

public class Card {
    private final List<Resource> resources;
    private int pollutionSpaces;
    private final Effect upperEffect, lowerEffect;

    public Card(Resource[] resources, int pollutionSpaces) {
        this(resources, pollutionSpaces, null, null);
    }

    public Card(Resource[] resources, int pollutionSpaces, Effect upperEffect, Effect lowerEffect) {
        this.resources = new ArrayList<>(List.of(resources));
        this.pollutionSpaces = pollutionSpaces;
        this.upperEffect = upperEffect;
        this.lowerEffect = lowerEffect;
    }


    public boolean canGetResources(List<Resource> newResources) {
        return newResources.stream().noneMatch(r -> r == Resource.Polution);
//        for (Resource r : newResources) {
//            if (r == Resource.Polution) {
//                return false;
//            }
//        }
//        return true;
    }

    public void getResources(List<Resource> newResources) {
        if (canGetResources(newResources)){
            resources.addAll(newResources);
        }
    }

    public boolean canPutResources(List<Resource> outgoingResources) {
        for (Resource resource : outgoingResources) {
            if (!resources.contains(resource)) {
                return false;
            }
        }
        return true;
    }

    public void putResources(List<Resource> outgoingResources) {
        if (canPutResources(outgoingResources)) {
            resources.removeAll(outgoingResources);
        }
    }

    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return (lowerEffect == null || lowerEffect.check(input, output, pollution)) && (upperEffect == null || upperEffect.check(input, output, pollution));
    }

    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        if (lowerEffect == null) return true;
        return lowerEffect.check(input, output, pollution);
    }

    public boolean hasAssistance() {
        return (lowerEffect == null || lowerEffect.hasAssistance()) && (upperEffect == null || upperEffect.hasAssistance());
    }

    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"resources\": [");
        for (int i = 0; i < resources.size(); i++) {
            sb.append("\"").append(resources.get(i).name()).append("\"");
            if (i < resources.size() - 1) sb.append(", ");
        }
        sb.append("], ");
        sb.append("\"pollutionSpaces\": ").append(pollutionSpaces);
        if (upperEffect != null) {
            sb.append(", \"upperEffect\": ").append("\"").append(upperEffect.state()).append("\"");
        }
        if (lowerEffect != null) {
            sb.append(", \"lowerEffect\": ").append("\"").append(lowerEffect.state()).append("\"");
        }
        sb.append(" }");
        return sb.toString();
    }


    // Getters (useful for testing)
    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    public int getPollutionSpaces() {
        return pollutionSpaces;
    }

    public Effect getUpperEffect() {
        return upperEffect;
    }

    public Effect getLowerEffect() {
        return lowerEffect;
    }


}
