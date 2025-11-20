package sk.uniba.fmph.dcs.terra_futura;

import java.util.*;

/**
 * Represents a single card on the player's grid in Terra Futura.
 *
 * A card stores:
 *  - its current resources,
 *  - the amount of accumulated pollution,
 *  - two possible effects: upper (main) and lower (secondary).
 *
 * The card does NOT activate effects by itself — instead, it exposes
 * methods for checking whether effects can be triggered and for manipulating
 * its stored resources.
 *
 * This class follows the UML model strictly and stores no additional gameplay logic.
 */
public class Card {

    /** List of resources currently located on the card. */
    private final List<Resource> resources;

    /** Number of pollution tokens currently stored on the card. */
    private int pollutionSpaces;

    /** Upper effect of this card (may be null if the card has no upper effect). */
    private final Effect upperEffect;

    /** Lower effect of this card (may be null if the card has no lower effect). */
    private final Effect lowerEffect;


    /**
     * Constructs a card without any effects.
     *
     * @param resources initial resource list
     * @param pollutionSpaces initial pollution level
     */
    public Card(Resource[] resources, int pollutionSpaces) {
        this(resources, pollutionSpaces, null, null);
    }

    /**
     * Constructs a fully-defined card.
     *
     * @param resources        initial resources
     * @param pollutionSpaces  initial pollution
     * @param upperEffect      optional upper effect
     * @param lowerEffect      optional lower effect
     */
    public Card(Resource[] resources, int pollutionSpaces, Effect upperEffect, Effect lowerEffect) {
        this.resources = new ArrayList<>(List.of(resources));
        this.pollutionSpaces = pollutionSpaces;
        this.upperEffect = upperEffect;
        this.lowerEffect = lowerEffect;
    }


    /**
     * Determines whether the card can accept new resources.
     * In this implementation, a card cannot receive Pollution as a resource,
     * since pollution must only come from effect activation.
     *
     * @param newResources resources intended to be added
     * @return true if the resources can be added, false otherwise
     */
    public boolean canGetResources(List<Resource> newResources) {
        return newResources.stream().noneMatch(r -> r == Resource.Polution);
    }

    /**
     * Adds resources to the card if allowed (based on canGetResources).
     *
     * @param newResources resources to add
     */
    public void getResources(List<Resource> newResources) {
        if (canGetResources(newResources)) {
            resources.addAll(newResources);
        }
    }

    /**
     * Checks whether the card contains all resources that are intended
     * to be removed.
     *
     * @param outgoingResources resources to remove
     * @return true if all resources are present on the card
     */
    public boolean canPutResources(List<Resource> outgoingResources) {
        for (Resource resource : outgoingResources) {
            if (!resources.contains(resource)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes resources from the card if allowed.
     *
     * @param outgoingResources resources to remove
     */
    public void putResources(List<Resource> outgoingResources) {
        if (canPutResources(outgoingResources)) {
            resources.removeAll(outgoingResources);
        }
    }

    /**
     * Checks whether the card’s effects allow an activation with the specified parameters.
     * Both effects (upper and lower) must allow the activation.
     *
     * @param input      resources to consume
     * @param output     resources to produce
     * @param pollution  pollution produced
     * @return true if activation is allowed by both effects
     */
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return (lowerEffect == null || lowerEffect.check(input, output, pollution)) &&
                (upperEffect == null || upperEffect.check(input, output, pollution));
    }

    /**
     * Checks only the lower effect.
     * Useful for special rules involving partial card activation.
     *
     * @return true if the lower effect allows activation
     */
    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        if (lowerEffect == null) return true;
        return lowerEffect.check(input, output, pollution);
    }

    /**
     * Returns true if either upper or lower effect requires player assistance.
     *
     * @return true if any effect has assistance mechanics
     */
    public boolean hasAssistance() {
        return (lowerEffect != null && lowerEffect.hasAssistance()) ||
                (upperEffect != null && upperEffect.hasAssistance());
    }

    /**
     * Serializes the card into a simple JSON-like string representation.
     * Used for debugging and integration tests.
     *
     * @return string representing card state
     */
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


    /** @return copy of resources for safe external access */
    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    /** @return current pollution amount on this card */
    public int getPollutionSpaces() {
        return pollutionSpaces;
    }

    /** @return the upper effect (may be null) */
    public Effect getUpperEffect() {
        return upperEffect;
    }

    /** @return the lower effect (may be null) */
    public Effect getLowerEffect() {
        return lowerEffect;
    }
}
