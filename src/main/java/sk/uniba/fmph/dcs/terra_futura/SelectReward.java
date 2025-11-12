package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

public final class SelectReward {
    private Optional<Integer> player;
    private List<Resource> selelction;
    private Card targetCard;

    public SelectReward() {
        this.player = Optional.empty();
        this.selelction = Collections.emptyList();
        this.targetCard = null;
    }

    /**
     * Initializes a reward selection for given player/card with the allowed rewards.
     * Throws if a selection is already in progress.
     */
    public void setReward(final int player, final Card card, final Resource[] reward) {
        // robust check for "already in progress"
        if (this.player.isPresent() || this.targetCard != null || !this.selelction.isEmpty()) {
            throw new IllegalStateException("Reward selection already in progress");
        }
        if (card == null) {
            throw new IllegalArgumentException("card is null");
        }
        if (reward == null) {
            throw new IllegalArgumentException("reward is null");
        }
        this.player = Optional.of(player);
        this.targetCard = card;
        this.selelction = new ArrayList<>(Arrays.asList(reward));
    }

    /**
     * Returns true if there is an active chooser and the resource is allowed
     * AND the target card can accept it (per rules, no pollution allowed).
     */
    public boolean canSelectReward(final Resource resource) {
        if (resource == null || targetCard == null || player.isEmpty()) {
            return false;
        }
        if (!this.selelction.contains(resource)) {
            return false;
        }
        return targetCard.canGetResources(Collections.singletonList(resource));
    }

    /**
     * Applies the chosen reward to the target card and clears the selection context.
     */
    public void selectReward(final Resource resource) {
        if (!canSelectReward(resource)) {
            throw new IllegalArgumentException("Cannot select given reward");
        }
        targetCard.getResources(Collections.singletonList(resource));

        this.player = Optional.empty();
        this.selelction = Collections.emptyList();
        this.targetCard = null;
    }

    public String state() {
        JSONObject obj = new JSONObject();
        obj.put("player", this.player.isPresent() ? this.player.get() : JSONObject.NULL);

        JSONArray arr = new JSONArray();
        for (Resource r : this.selelction) {
            arr.put(r.name());
        }
        obj.put("selection", arr);
        return obj.toString();
    }
}
