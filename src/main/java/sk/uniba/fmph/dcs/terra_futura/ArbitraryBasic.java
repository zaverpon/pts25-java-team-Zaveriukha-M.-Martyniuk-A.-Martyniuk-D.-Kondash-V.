package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;

public class ArbitraryBasic implements Effect {
    private int from;
    private List<Resource> to;
    private int pollution;

    public ArbitraryBasic(int from, List<Resource> to, int pollution){
        this.from = from;
        this.to = to;
        this.pollution = pollution;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution){
        return input.size() == from && output.equals(to) && this.pollution == pollution;
    }

    @Override
    public boolean hasAssistance(){
        return true;
    }

    @Override
    public String state() {
        return "ArbitraryBasic [from=" + from + ", to=" + to + ", pollution=" + pollution + "]";
    }
}
