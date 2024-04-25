package recipecalc.calc;

import recipecalc.Util;
import recipecalc.node.LinkedNode;

public class Tree {
    final static String[] lines = new String[]{"│", "├", "─", "└"};
    public static void printAsTree(LinkedNode origin) {
        (new Tree()).branch(origin);
    }

    private Tree() {
    }

    private void branch(LinkedNode node) {
        System.out.println(buildLine(node));
        if (node.child.isEmpty()) { return; }
        else {
            for (LinkedNode cNode : node.child) {
                branch(cNode);
            }
        }
    }

    private String buildLine(LinkedNode node) {
        String prefix = node.parent!=null
                      ? !node.parent.child.isEmpty()
                       ? !node.parent.child.get(node.parent.child.size()-1).equals(node)
                        ? buildPrefix(node.parent).concat(lines[1])
                        : buildPrefix(node.parent).concat(lines[3])
                       : Util.repeat(lines[0], node.depth)
                      : Util.repeat(lines[0], node.depth);
        return prefix.concat(node.display);
    }

    private String buildPrefix(LinkedNode parent) {
        return parent.parent!=null
             ? buildPrefix(parent.parent).concat(
              !parent.parent.child.get(parent.parent.child.size()-1).equals(parent)
              ? lines[0]
              : " ")
             : "";
    }
}
