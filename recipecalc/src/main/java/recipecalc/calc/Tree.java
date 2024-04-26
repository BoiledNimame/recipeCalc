package recipecalc.calc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import recipecalc.Util;
import recipecalc.node.LinkedNode;

public class Tree {
    public static void printAsTree(LinkedNode origin) {
        Tree tree = new Tree();
        tree.branch(origin);
        tree.printFirstResource();
        tree.printByProduct(origin);
        // TODO 一次リソースの集計+print
    }

    private Tree() { firstResource = new HashMap<>();}
    
    final String LINE = "│";
    final String BRANCH = "├";
    final String END = "└";
    final String SPACE = " ";
    final String EMPTY = "";

    private final Map<String, Long> firstResource;

    private void branch(LinkedNode node) {
        System.out.println(buildLine(node));
        if (node.child.isEmpty()) {
            addFirstResource(node);
        } else {
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

    private void addFirstResource(LinkedNode node) {
        if (firstResource.containsKey(node.name)) {
            firstResource.put(node.name, node.craftCount + firstResource.get(node.name));
        } else {
            firstResource.put(node.name, node.craftCount);
        }
    }

    private void printFirstResource() {
        System.out.println("Resource Requirements:");
        for (Entry<String, Long> entry : firstResource.entrySet()) {
            System.out.println(" - ".concat(entry.getKey()).concat(":").concat(String.valueOf(entry.getValue())));
        }
    }

    private void printByProduct(LinkedNode origin) {
        System.out.println("ByProducts:");
        for (Entry<String, Long> entry : origin.consumableNode.entrySet()) {
            if (!entry.getKey().equals(origin.name) && entry.getValue()!=0) {
                System.out.println(" - ".concat(entry.getKey()).concat(":").concat(String.valueOf(entry.getValue())));
            }
        }
    }
}
