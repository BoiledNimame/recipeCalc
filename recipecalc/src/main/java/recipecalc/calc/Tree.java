package recipecalc.calc;

import recipecalc.Util;
import recipecalc.node.LinkedNode;

public class Tree {
    final static String[] lines = new String[]{"│", "├", "─", "└"};
    public static void printAsTree(LinkedNode origin) {
        (new Tree(origin)).branch(origin);
    }

    private LinkedNode before;
    private Tree(LinkedNode origin) {
        before = origin;
    }

    private void branch(LinkedNode node) {
        // TODO beforeのdepthあるいはそれを利用したツリー構造化
        String prefix = node.depth!=before.depth
                      ? !node.parent.child.isEmpty()
                       ? !node.parent.child.get(node.parent.child.size()-1).equals(node)
                        ? Util.repeat(lines[0], node.depth - 1).concat(lines[1])
                        : Util.repeat(lines[0], node.depth - 1).concat(lines[3])
                       : Util.repeat(lines[0], node.depth)
                      : Util.repeat(lines[0], node.depth);
        System.out.println(prefix.concat(node.display));
        before = node;
        if (node.child.isEmpty()) { return; }
        else {
            for (LinkedNode cNode : node.child) {
                branch(cNode);
            }
        }
    }
}
