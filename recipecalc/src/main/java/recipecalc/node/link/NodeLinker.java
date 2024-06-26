package recipecalc.node.link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import recipecalc.node.Node;
import recipecalc.node.RecipeNode;
import recipecalc.util.Util;

public class NodeLinker {
    public static LinkedNode LinkNodeFromProblem(RecipeNode[] recipes, String targetName, int targetQuantity) {
        final RecipeNode initialRecipe = Arrays.stream(recipes).filter(f -> f.name.equals(targetName)).findFirst().orElseThrow(NoSuchElementException::new);
        final LinkedNode origin = new LinkedNode(initialRecipe, targetQuantity, recipes);
        return origin;
    }

    static long calcCraftCount(Node target, long requiredQuantity) {
        return (long) Math.ceil((double)requiredQuantity/(double)target.quantity);
    }

    static Node getTargetNode(Node[] pool, String name) {
        return Arrays.stream(pool)
                .filter(f -> f.name.equals(name))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    static Node[] defineResult(RecipeNode recipe, long craftCount) {
        return Arrays.stream(recipe.resultNodes)
                        .parallel()
                        .map(m -> new Node(m.name, m.type, m.quantity*craftCount))
                        .toList().toArray(new Node[recipe.resultNodes.length]);
    }

    static Node[] defineResult(Node[] resultNodes, long craftCount) {
        return Arrays.stream(resultNodes)
                        .parallel()
                        .map(m -> new Node(m.name, m.type, m.quantity*craftCount))
                        .toList().toArray(new Node[resultNodes.length]);
    }

    static Node[] getByProductFromResult(Node mainProduct, Node[] result) {
        final Node redundant = Arrays.stream(result)
                                        .filter(r -> r.name.equals(mainProduct.name))
                                        .map(r -> new Node(
                                            mainProduct.name,
                                            mainProduct.type,
                                            r.quantity-mainProduct.quantity))
                                        .findFirst().orElseThrow(NoSuchElementException::new);
        if (0 <= redundant.quantity) {
            final List<Node> byProduct = new ArrayList<>();
            byProduct.addAll(Arrays.stream(result)
                                .filter(r -> !r.name.equals(mainProduct.name)).toList());
            byProduct.add(redundant);
            return byProduct.toArray(Node[]::new);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 自身の子となるノードを追加する
     * @param parent 親となるノード
     */
    static void defineChild(LinkedNode parent) {
        if (!parent.child.isEmpty()) { throw new UnsupportedOperationException(); } // 2回呼ばれないように
        if (parent.pos.equals(RecipePos.HEAD) | parent.pos.equals(RecipePos.BODY)) {
            // parentの素材として要求してくるアイテムの内訳
            final Node[] parentsIngredient = defineResult(parent.getRecipeIOs().getKey(), parent.craftCount);
            SOLVE : for (int i = 0; i < parentsIngredient.length; i++) {
                final Node subjectNode = parent.consume(parentsIngredient[i]);
                if (subjectNode.quantity==0) {
                    // もしconsumeしきったら次へ
                    continue SOLVE;
                }

                // 自身のidからレシピの再構築
                final RecipeNode target = new RecipeNode(
                    subjectNode.name,
                    parent.getRecipeIOsFromProductName(subjectNode.name).getKey(),
                    parent.getRecipeIOsFromProductName(subjectNode.name).getValue());
                // 作成数計算(一次リソース(≒クラフト負荷の消費)なら作成数=要求数)
                final long count = Util.arrayIsEmpty(target.ingredientNodes)
                                 ? subjectNode.quantity
                                 : calcCraftCount(
                                  getTargetNode(target.resultNodes, subjectNode.name),
                                  subjectNode.quantity);
                // 余剰登録
                @SuppressWarnings("unchecked")
                final List<Node> byProduct = target.resultNodes.length < 1
                                           ? Arrays.stream(target.resultNodes)
                                                    .filter(p -> !p.name.equals(subjectNode.name))
                                                    .toList()
                                           : Collections.EMPTY_LIST;
                if (!byProduct.isEmpty()) {
                    for (Node node : byProduct) {
                        final long byProductQuantity = node.quantity*count;
                        if (parent.consumableNode.containsKey(node.name)) {
                            parent.consumableNode.put(node.name, byProductQuantity + parent.consumableNode.get(node.name));
                        } else {
                            parent.consumableNode.put(node.name, byProductQuantity);
                        }
                    }
                }
                if (count!=subjectNode.quantity) {
                    final long diff = getTargetNode(target.resultNodes, subjectNode.name).quantity*count - subjectNode.quantity;
                    if (parent.consumableNode.containsKey(subjectNode.name)) {
                        parent.consumableNode.put(subjectNode.name, diff + parent.consumableNode.get(subjectNode.name));
                    } else {
                        parent.consumableNode.put(subjectNode.name, diff);
                    }
                }

                // 再帰
                new LinkedNode(parent, target, count);
            }
        } else {
            // 呼ばれてはいけない
            throw new UnsupportedOperationException();
        }
    }
}