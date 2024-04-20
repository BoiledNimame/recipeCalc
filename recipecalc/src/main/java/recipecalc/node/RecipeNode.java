package recipecalc.node;

public class RecipeNode {

    public final String[] parent;
    public final Node[] ingredientNodes;
    public final Node[] resutNodes;
    public final long requiredQuantity;

    public RecipeNode(String[] parent, Node[] ingredients, Node[] result, long quantity) {
        this.parent = parent;
        ingredientNodes = ingredients;
        resutNodes = result;
        requiredQuantity = quantity;
    }

    public boolean equals(RecipeNode node) {
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
}