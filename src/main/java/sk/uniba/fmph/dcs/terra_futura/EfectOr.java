package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;

public class EfectOr implements Effect {
    private final List<Effect> effects;

    public EfectOr(List<Effect> effects) {
        if (effects == null || effects.isEmpty()){
            throw new IllegalArgumentException("effects is null or empty");
        }
        this.effects = List.copyOf(effects);
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return effects.stream().anyMatch(e -> e.check(input, output, pollution));
    }

    @Override
    public boolean hasAssistance() {
        return effects.stream().anyMatch(Effect::hasAssistance);
    }

    @Override
    public String state() {
        return "EfectOr(" + effects.stream().map(Effect::state).reduce((a,b) -> a + ", " + b).orElse("") + ")";
    }
}
