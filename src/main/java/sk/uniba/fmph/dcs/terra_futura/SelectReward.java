package sk.uniba.fmph.dcs.terra_futura;

import org.json.*;
import java.util.*;

public class SelectReward {
    private Optional<Integer> player;
    private List<Resource> selelction;

    private Card targetCard;

    public SelectReward(){
        this.player = Optional.empty();
        this.selelction = Collections.emptyList();
        this.targetCard = null;
    }

    public void setReward(int player, Card card, Resource[] reward){
        if(this.player.isPresent()){
            throw new IllegalArgumentException("Reward selelction in progress");
        }
        if(card == null){
            throw new IllegalArgumentException("card is null");
        }
        if(reward == null){
            throw new IllegalArgumentException("reward is null");
        }
        this.player = Optional.of(player);
        this.targetCard = card;
        this.selelction = new ArrayList<>(Arrays.asList(reward));
    }

    public boolean canSelectReward(Resource resource){
        if(resource == null || targetCard == null || player.isEmpty()){
            return false;
        }
        if (!this.selelction.contains(resource)){
            return false;
        }

        return targetCard.canGetResources(List.of(resource));
    }

    public void selectReward(Resource resource){
        if(!canSelectReward(resource)){
            throw new IllegalArgumentException("Cannot select the given resource");
        }

        targetCard.getResources(List.of(resource));

        this.player = Optional.empty();
        this.selelction = Collections.emptyList();
        this.targetCard = null;
    }

    public String state(){
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
