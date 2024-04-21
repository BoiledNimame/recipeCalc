package recipecalc.node;

import java.util.Arrays;

public class NodeLinker {
    public static LinkedRecipeNode LinkNodeFromProblem(RecipeNode[] recipes, String targetName, int targetQuantity) {
        final RecipeNode initialRecipe = Arrays.stream(recipes).filter(f -> f.name.equals(targetName)).findFirst().orElseThrow(IllegalArgumentException::new);
        final LinkedRecipeNode origin = new LinkedRecipeNode(initialRecipe, targetQuantity, recipes);
        return origin;
    }

    static RecipeNode getRecipeNodeByName(RecipeNode[] recipes, String name) {
        return Arrays.stream(recipes)
                        .filter(f -> f.name.equals(name))
                        .findFirst().get();
    }
}