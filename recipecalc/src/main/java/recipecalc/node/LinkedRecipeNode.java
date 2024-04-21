package recipecalc.node;

import java.util.Arrays;
import java.util.UUID;

public class LinkedRecipeNode {
    public final int depth;
    public final String name;
    public final String display;
    public final UUID uuid;
    public final RecipePos pos;
    public final LinkedRecipeNode parent;
    public final LinkedRecipeNode[] child;
    public final LinkedRecipeNode[] resultNodes;
    public final RecipeNode[] allRecipe;
    public final long requiredQuantity;
    public final Node[] consumableIngredients;

    /**
     * make LinkedRecipeNode as HEAD
     */
    public LinkedRecipeNode(RecipeNode targetRecipe, long requiredQuantity, RecipeNode[] allRecipe) {
        depth = 0;
        name = targetRecipe.name;
        final Node targetNode = Arrays.stream(targetRecipe.resutNodes).filter(f -> f.id.equals(name)).findFirst().orElseThrow(IllegalArgumentException::new);
        display = displayBuilder(name, targetRecipe.resutNodes[0], requiredQuantity);
        uuid = UUID.randomUUID();
        pos = RecipePos.HEAD;
        parent = null;
        child = targetRecipe.resutNodes; // TODO ここにコンストラクタ呼ぶ関数詰めて再帰的展開を行う
        resultNodes = null; // TODO いらないかも?
        consumableIngredients = null; // TODO ここでは余剰を取り扱う予定
        this.allRecipe = allRecipe;
    }

    public LinkedRecipeNode(int depth, String targetName, long requiredQuantity, LinkedRecipeNode parent) {
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

    public boolean equals(LinkedRecipeNode node) {
        return this.uuid.equals(node.uuid);
    }

    public enum RecipePos {
        HEAD,BODY,TAIL;
        RecipePos() {}
    }
}
