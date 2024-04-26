package recipecalc;

import java.util.Arrays;
import java.util.Collections;

public class Util {
    public static <T> boolean arrayIsEmpty(T[] t) {
        return Arrays.asList(t).isEmpty();
    }
    
    public static String repeat(String str, int n) {
        return String.join("", Collections.nCopies(n, str));
    }
}