package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Which player can choose what rewards. Performs the action.
 * Structure (per design.pdf):
 *  - player: Optional[int]
 *  - selelction: List[Resource]
 *  - setReward(player: int,  card: Card, reward: Resource[])
 *  - canSelectReward(resource: Resource): bool
 *  - selectReward(resource: Resource)
 *  - state(): string
 */
public final class SelectReward {
    private Optional<Integer> player;
    private List<Resource> selelction;
    private Card targetCard;

    public SelectReward() {
        this.player = Optional.empty();
        this.selelction = Collections.emptyList();
        this.targetCard = null;
    }

    public void setReward(final int player, final Card card, final Resource[] reward) {
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

    public boolean canSelectReward(final Resource resource) {
        if (resource == null || targetCard == null || player.isEmpty()) {
            return false;
        }
        if (!this.selelction.contains(resource)) {
            return false;
        }
        // Card API: canGetResources verifies the resource(s) are acceptable to add
        return targetCard.canGetResources(Collections.singletonList(resource));
    }

    public void selectReward(final Resource resource) {
        if (!canSelectReward(resource)) {
            throw new IllegalArgumentException("Cannot select given reward");
        }
        // Place the rewarded resource on the copied/target card
        targetCard.getResources(Collections.singletonList(resource));

        // clear context
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
