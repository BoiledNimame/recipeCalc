package recipecalc.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import recipecalc.node.LinkedNode.RecipePos;

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
        final Node redundant = Arrays.stream(result)
                                        .filter(r -> r.id.equals(mainProduct.id))
                                        .map(r -> new Node(
                                            mainProduct.id,
                                            mainProduct.type,
                                            r.quantity-mainProduct.quantity))
                                        .findFirst().orElseThrow(IllegalStateException::new);
        if (0 <= redundant.quantity) {
            final List<Node> byProduct = new ArrayList<>();
            byProduct.addAll(Arrays.stream(result)
                                .filter(r -> !r.id.equals(mainProduct.id)).toList());
            byProduct.add(redundant);
            return byProduct.toArray(Node[]::new);
        } else {
            throw new IllegalArgumentException();
        }
    }

    static void defineChild(LinkedNode parent) { // FIXME 課題::数量がなんだかおかしい
        if (parent.pos.equals(RecipePos.HEAD) | parent.pos.equals(RecipePos.BODY)) {
            // parentの素材として要求してくるアイテムの内訳
            final Node[] parentsIngredient = defineResult(parent.getRecipeIOs().getKey(), parent.craftCount);
            SOLVE : for (int i = 0; i < parentsIngredient.length; i++) {
                final Node subjectNode = parent.consume(parentsIngredient[i]);
                if (subjectNode.quantity==0) {
                    // もしconsumeしきったら次へ
                    // またはdisplayに"消費"とした子Nodeを出すようにしても良い
                    continue SOLVE;
                }
                final RecipeNode target = new RecipeNode(
                    subjectNode.id,
                    parent.getRecipeIOsFromProductName(subjectNode.id).getKey(),
                    parent.getRecipeIOsFromProductName(subjectNode.id).getValue());

                new LinkedNode(parent, target, subjectNode.quantity);
            }
        } else {
            // 呼ばれてはいけない
            throw new UnsupportedOperationException();
        }
    }
}