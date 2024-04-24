package recipecalc.calc;

import recipecalc.node.LinkedNode;
import java.util.Collections;

public class Tree {
    final static String[] lines = new String[]{"│", "├", "─", "└"};
    public static void printAsTree(LinkedNode origin) {
        // TODO ツリー状に展開+最終リソースの要求数
        branch(origin);
    }

    private static void branch(LinkedNode node) {
        System.out.println(repeat(lines[0], node.depth).concat(node.display));
        if (node.child.isEmpty()) { return; }
        else {
            for (LinkedNode cNode : node.child) {
                branch(cNode);
            }
        }
    }
    
    private static String repeat(String str, int n) {
        return String.join("", Collections.nCopies(n, str));
    }
}
