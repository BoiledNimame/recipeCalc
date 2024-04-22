package recipecalc.node;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkedRecipeNode {
    public final int depth;
    public final String name;
    public final String display;
    public final UUID uuid;
    public final RecipePos pos;
    public final LinkedRecipeNode parent;
    public final LinkedRecipeNode[] child;
    public final Node[] resultNodes;
    public final long requiredQuantity;

    private static final Map<String, SimpleImmutableEntry<Node[], Node[]>> registedRecipes = new HashMap<>();

    /**
     * make LinkedRecipeNode as HEAD
     */
    public LinkedRecipeNode(RecipeNode targetRecipe, long requiredQuantity, RecipeNode[] allRecipe) {
        recipeRegister(allRecipe);
        depth = 0;
        name = targetRecipe.name;
        final Node targetNode = Arrays.stream(targetRecipe.resutNodes)
                                        .filter(f -> f.id.equals(name))
                                        .findFirst().orElseThrow(IllegalArgumentException::new);
        display = displayBuilder(name, targetRecipe.resutNodes[0], requiredQuantity);
        uuid = UUID.randomUUID();
        pos = RecipePos.HEAD;
        parent = null;
        child = targetRecipe.resutNodes; // TODO ここにコンストラクタ呼ぶ関数詰めて再帰的展開を行う
        resultNodes = null; // TODO いらないかも?
    }

    /**
     * Body and Tail's Constructor cannot call from other class 
     */
    private LinkedRecipeNode(int depth, String targetName, long requiredQuantity, LinkedRecipeNode parent) {
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

    public boolean equals(LinkedRecipeNode node) {
        return this.uuid.equals(node.uuid);
    }

    public enum RecipePos {
        HEAD,BODY,TAIL;
        RecipePos() {}
    }
}
