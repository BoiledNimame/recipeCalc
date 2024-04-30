package recipecalc.calc;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import recipecalc.Util;
import recipecalc.node.LinkedNode;
import recipecalc.node.ResourceType;

public class Tree {
    public static void printAsTree(LinkedNode origin) {
        Tree tree = new Tree(origin);
        tree.branch();
        tree.printPrimaryResource();
        tree.printByProduct();
    }

    private Tree(LinkedNode origin) {
        this.origin = origin;
        primaryResource = new HashMap<>();
    }
    
    final String LINE = "│";
    final String BRANCH = "├";
    final String END = "└";
    final String SPACE = " ";
    final String EMPTY = "";

    private final LinkedNode origin;
    private final Map<String, Long> primaryResource;

    private void branch() {
        System.out.println("Trees:");
        branch(origin);
    }

    private void branch(LinkedNode node) {
        System.out.println(buildLine(node));
        if (node.child.isEmpty()) {
            addPrimaryResource(node);
        } else {
            for (LinkedNode cNode : node.child) {
                branch(cNode);
            }
        }
    }

    private String buildLine(LinkedNode node) {
        String prefix = node.parent!=null || !node.parent.child.isEmpty()
                      ? !node.parent.child.get(node.parent.child.size()-1).equals(node)
                       ? buildPrefix(node.parent).concat(BRANCH)
                       : buildPrefix(node.parent).concat(END)
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

    private void addPrimaryResource(LinkedNode node) {
        if (primaryResource.containsKey(node.name)) {
            primaryResource.put(node.name, node.craftCount + primaryResource.get(node.name));
        } else {
            primaryResource.put(node.name, node.craftCount);
        }
    }

    private void printPrimaryResource() {
        System.out.println("Resource Requirements:");
        prettyPrinter(origin, primaryResource);
    }

    private void printByProduct() {
        System.out.println("ByProducts:");
        prettyPrinter(origin, origin.consumableNode);
    }

    private static void prettyPrinter(LinkedNode node, Map<String, Long> map) {
        final long longestKeylength = map.entrySet().stream()
            .filter(f -> !f.getKey().equals(node.name))
            .map(m -> m.getKey().length())
            .max(Comparator.naturalOrder()).get();
        final long longestValuelength = map.entrySet().stream()
            .filter(f -> !f.getKey().equals(node.name))
            .map(m -> String.valueOf(m.getValue()).length())
            .max(Comparator.naturalOrder()).get();
        for (Entry<String, Long> entry : map.entrySet()) {
            if (!entry.getKey().equals(node.name) && entry.getValue()!=0) {
                System.out.println(
                    " - "
                    .concat(entry.getKey())
                    .concat(Util.repeat(" ", Math.toIntExact(longestKeylength - entry.getKey().length())))
                    .concat(
                        node.getNodeByName(entry.getKey()).type.equals(ResourceType.Item)
                         ? " x"
                         : ":")
                    .concat(
                        node.getNodeByName(entry.getKey()).type.equals(ResourceType.Liquid)
                         || node.getNodeByName(entry.getKey()).type.equals(ResourceType.Gas)
                         ? String.valueOf(entry.getValue()).concat("mb")
                         : Util.repeat(" ", Math.toIntExact(longestValuelength - String.valueOf(entry.getValue()).length()))
                           .concat(String.valueOf(entry.getValue())))
                );
            }
        }
    }
}
