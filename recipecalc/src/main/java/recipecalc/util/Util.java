package recipecalc.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Util {
    public static <T> boolean arrayIsEmpty(T[] t) {
        return Arrays.asList(t).isEmpty();
    }
    
    public static String repeat(String str, int n) {
        return String.join("", Collections.nCopies(n, str));
    }

    public static <E> E getLastElement(List<E> list) {
        return list.get(list.size()-1);
    }
}
