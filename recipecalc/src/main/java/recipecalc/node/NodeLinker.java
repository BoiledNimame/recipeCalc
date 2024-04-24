package recipecalc.node;

import java.util.Arrays;
import java.util.List;

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
        final List<Node> byProduct = Arrays.stream(result)
                                        .filter(r -> !r.id.equals(mainProduct.id))
                                        .toList();
        final Node redundant = Arrays.stream(result)
                                        .filter(r -> r.id.equals(mainProduct.id))
                                        .map(r -> new Node(mainProduct.id, mainProduct.type, r.quantity-mainProduct.quantity))
                                        .findFirst().orElseThrow(IllegalStateException::new);
        if (0 <= redundant.quantity) {
            byProduct.add(redundant);
        } else {
            throw new IllegalArgumentException();
        }
        return byProduct.toArray(Node[]::new);
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
                    if (Util.arrayIsEmpty(parent.getRecipeIOsFromProductName(parentsIngredient[i].id).getKey())) {
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