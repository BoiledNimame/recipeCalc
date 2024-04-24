package recipecalc.node;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;

public class NodeLinker {
    public static LinkedNode LinkNodeFromProblem(RecipeNode[] recipes, String targetName, int targetQuantity) {
        final RecipeNode initialRecipe = Arrays.stream(recipes).filter(f -> f.name.equals(targetName)).findFirst().orElseThrow(IllegalArgumentException::new);
        final LinkedNode origin = new LinkedNode(initialRecipe, targetQuantity, recipes);
        return origin;
    }

    static long calcCraftCount(Node target, long requiredQuantity) {
        return (long) Math.ceil((double)requiredQuantity/(double)target.quantity);
    }

    static Node[] defineResult(RecipeNode recipe, long craftCount) {
        return Arrays.stream(recipe.resultNodes)
                        .parallel()
                        .map(m -> new Node(m.id, m.type, m.quantity*craftCount))
                        .toList().toArray(new Node[recipe.resultNodes.length]);
    }

    static Node[] defineResult(Node[] resultNodes, long craftCount) {
        return Arrays.stream(resultNodes)
                        .parallel()
                        .map(m -> new Node(m.id, m.type, m.quantity*craftCount))
                        .toList().toArray(new Node[resultNodes.length]);
    }

    static Node[] getByProductFromResult(Node mainProduct, Node[] result) {
        return Arrays.stream(result).filter(r -> !r.id.equals(mainProduct.id)).toList().toArray(Node[]::new);
    }

    static void defineChild(LinkedNode parent) {
        // TODO produce(consumableNode, Node[] produced)
        // TODO consume(consumableNode, Node[] produced)
        // TODO getConsumable(consumableNode);
        // TODO コンストラクタ呼ぶ
        switch (parent.pos) {
            case HEAD:
                // parentの素材として要求してくるアイテムの内訳
                final Node[] parentsIngredient = defineResult(parent.getRecipeIOs().getKey(), parent.craftCount);
                for (int i = 0; i < parentsIngredient.length; i++) {
                    if (Arrays.asList(parent.getRecipeIOsFromProductName(parentsIngredient[i].id).getKey()).isEmpty()) {
                        // 内訳アイテムが末端(TAIL)になる場合(≒HEADの副産物として直接TAILが生成される場合)
                    }
                    parent.child.add(new LinkedNode(parent, null, 0));
                }
                break;

            case BODY:

                break;

            case TAIL:
                
                break;
        }
    }
}