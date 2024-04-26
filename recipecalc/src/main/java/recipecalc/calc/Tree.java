package recipecalc.calc;

import recipecalc.Util;
import recipecalc.node.LinkedNode;

public class Tree {
    public static void printAsTree(LinkedNode origin) {
        (new Tree()).branch(origin);
    }

    private Tree() {}
    
    final String LINE = "│";
    final String BRANCH = "├";
    final String END = "└";
    final String SPACE = " ";
    final String EMPTY = "";

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
                        ? buildPrefix(node.parent).concat(BRANCH)
                        : buildPrefix(node.parent).concat(END)
                       : Util.repeat(LINE, node.depth)
                      : Util.repeat(LINE, node.depth);
        return prefix.concat(node.display);
    }

    private String buildPrefix(LinkedNode parent) {
        return parent.parent!=null
             ? buildPrefix(parent.parent).concat(
              !parent.parent.child.get(parent.parent.child.size()-1).equals(parent)
              ? LINE
              : SPACE)
             : EMPTY;
    }
}
