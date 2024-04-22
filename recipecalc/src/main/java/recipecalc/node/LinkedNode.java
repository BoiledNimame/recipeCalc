package recipecalc.node;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkedNode {
    public final int depth;
    public final String name;
    public final String display;
    public final UUID uuid;
    public final RecipePos pos;
    public final LinkedNode parent;
    public final List<LinkedNode> child;
    public final Node[] resultNodes;
    public final Map<String, Integer> consumableNode;
    public final long craftCount;

    private static final Map<String, SimpleImmutableEntry<Node[], Node[]>> registedRecipes = new HashMap<>();

    /**
     * make LinkedNode as HEAD
     */
    public LinkedNode(RecipeNode targetRecipe, long requiredQuantity, RecipeNode[] allRecipe) {
        recipeRegister(allRecipe);
        depth = 0;
        name = targetRecipe.name;
        final Node targetNode = Arrays.stream(targetRecipe.resutNodes)
                                        .filter(f -> f.id.equals(name))
                                        .findFirst().orElseThrow(IllegalArgumentException::new);
        display = displayBuilder(name, targetRecipe.resutNodes[0], requiredQuantity);
        uuid = UUID.randomUUID();
        pos = RecipePos.HEAD;
        craftCount = NodeLinker.calcCraftCount(targetNode, requiredQuantity);
        parent = null;
        resultNodes = NodeLinker.calcResult(targetRecipe, craftCount);
        consumableNode = new HashMap<>();
        child = NodeLinker.defineChild(this);
    }

    LinkedNode(RecipeNode targetRecipe, LinkedNode parent) {
        depth = parent.depth+1;
        name = targetRecipe.name;
        uuid = UUID.randomUUID();
        pos = RecipePos.BODY;
    }

    private static String displayBuilder(String baseName, Node target, long quantity) {
        String targetDisplay;
        switch (target.type) {
            case Item:
                targetDisplay = baseName.concat(" ").concat("x").concat(String.valueOf(quantity));
                break;
            case Liquid:
                targetDisplay = baseName.concat(" ").concat(String.valueOf(quantity)).concat("mb");
                break;
            case Gas:
                targetDisplay = baseName.concat(" ").concat(String.valueOf(quantity)).concat("mb");
                break;
            default:
                targetDisplay = baseName.concat(" ").concat("x").concat(String.valueOf(quantity));
                break;
        }
        return targetDisplay;
    }

    private static void recipeRegister(RecipeNode[] recipes) {
        for (int i = 0; i < recipes.length; i++) {
            registedRecipes.put(recipes[i].name, new SimpleImmutableEntry<Node[],Node[]>(recipes[i].ingredientNodes, recipes[i].resutNodes));
        }
    }

    public boolean equals(LinkedNode node) {
        return this.uuid.equals(node.uuid);
    }

    public enum RecipePos {
        HEAD,BODY,TAIL;
        RecipePos() {}
    }
}
