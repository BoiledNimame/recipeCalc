package recipecalc.node;

import java.util.Arrays;

public class Util {
    public static <T> boolean arrayIsEmpty(T[] t) {
        return Arrays.asList(t).isEmpty();
    }
}
