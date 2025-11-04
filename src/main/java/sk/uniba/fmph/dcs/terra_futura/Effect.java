package sk.uniba.fmph.dcs.terra_futura;

import java.util.*;

public interface Effect {
    boolean check(List<Resource> input, List<Resource> output, int pollution);
    boolean hasAssistance();
    String state();
    //Andrii Martyniuk
}
