package recipecalc.node;

import java.util.Arrays;

public class LinkedRecipeNode {
    public final String name;
    public final RecipePos pos;
    public final String[] parent;
    public final Node[] ingredientNodes;
    public final Node[] resutNodes;
    public final long requiredQuantity;

    public LinkedRecipeNode(String name, String[] parent, Node[] ingredients, Node[] result, long quantity) {
        this.name = name;
        if (Arrays.asList(result).isEmpty()) {
            pos = RecipePos.TAIL;
        } else if (Arrays.asList(parent).isEmpty()) {
            pos = RecipePos.HEAD;
        } else {
            pos = RecipePos.BODY;
        }
        this.parent = parent;
        ingredientNodes = ingredients;
        resutNodes = result;
        requiredQuantity = quantity;
    }

    public boolean equals(LinkedRecipeNode node) {
        final int selfLength = this.parent.length;
        final int targetLength = node.parent.length;
        if (selfLength==targetLength && selfLength!=0 ) {
            int match = 0;
            for (int i = 0; i < selfLength; i++) {
                if (this.parent[i].equals(node.parent[i])) {
                    match++;
                    if (match==selfLength) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public enum RecipePos {
        HEAD,BODY,TAIL;
        RecipePos() {}
    }
}
