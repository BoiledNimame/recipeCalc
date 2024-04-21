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
    public final LinkedRecipeNode[] resutNodes;
    public final RecipeNode[] allRecipe;
    public final long requiredQuantity;
    public final Node[] consumableIngredients;

    /**
     * make LinkedRecipeNode as HEAD
     */
    public LinkedRecipeNode(RecipeNode targetRecipe, long requiredQuantity) {
        depth = 0;
        name = targetRecipe.name;
        final Node targetNode = Arrays.stream(targetRecipe.resutNodes).filter(f -> f.id.equals(name)).findFirst().orElseThrow(IllegalArgumentException::new);
        switch (targetRecipe.resutNodes[0].type) {
            case Item:
                display = name.concat(" ").concat("x").concat(String.valueOf(requiredQuantity));
                break;
            case Liquid:
                display = name.concat(" ").concat(String.valueOf(requiredQuantity)).concat("mb");
                break;
            case Gas:
                display = name.concat(" ").concat(String.valueOf(requiredQuantity)).concat("mb");
                break;
            default:
                display = name.concat(" ").concat("x").concat(String.valueOf(requiredQuantity));
                break;
        }
        uuid = UUID.randomUUID();
    }

    public LinkedRecipeNode(int depth, String targetName, long requiredQuantity, LinkedRecipeNode parent) {
        uuid = UUID.randomUUID();
    }

    public boolean equals(LinkedRecipeNode node) {
        return this.uuid.equals(node.uuid);
    }

    public enum RecipePos {
        HEAD,BODY,TAIL;
        RecipePos() {}
    }
}
