package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;

public class TransformationFixed implements Effect {
    private List<Resource> from;
    private List<Resource> to;
    private int pollution;

    public TransformationFixed(List<Resource> from, List<Resource> to, int pollution) {
        this.from = from;
        this.to = to;
        this.pollution = pollution;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return input.equals(from) && output.equals(to) && this.pollution == pollution;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "TransformationFixed: " + from + " -> " + to + ", pollution=" + pollution;
    }
}

