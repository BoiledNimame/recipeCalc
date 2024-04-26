package recipecalc.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import recipecalc.Util;
import recipecalc.node.LinkedNode.RecipePos;

public class NodeLinker {
    public static LinkedNode LinkNodeFromProblem(RecipeNode[] recipes, String targetName, int targetQuantity) {
        final RecipeNode initialRecipe = Arrays.stream(recipes).filter(f -> f.name.equals(targetName)).findFirst().orElseThrow(NoSuchElementException::new);
        final LinkedNode origin = new LinkedNode(initialRecipe, targetQuantity, recipes);
        return origin;
    }

    static long calcCraftCount(Node target, long requiredQuantity) {
        return (long) Math.ceil((double)requiredQuantity/(double)target.quantity);
    }

    static Node getTargetNode(Node[] pool, String id) {
        return Arrays.stream(pool)
                .filter(f -> f.id.equals(id))
                .findFirst().orElseThrow(NoSuchElementException::new);
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
                                        .findFirst().orElseThrow(NoSuchElementException::new);
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

    static void defineChild(LinkedNode parent) {
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
                    subjectNode.id,
                    parent.getRecipeIOsFromProductName(subjectNode.id).getKey(),
                    parent.getRecipeIOsFromProductName(subjectNode.id).getValue());
                // 作成数計算(一次リソース(≒クラフト負荷の消費)なら作成数=要求数)
                final long count = Util.arrayIsEmpty(target.ingredientNodes)
                                 ? subjectNode.quantity
                                 : calcCraftCount(
                                  getTargetNode(target.resultNodes, subjectNode.id),
                                  subjectNode.quantity);
                // 余剰登録
                @SuppressWarnings("unchecked")
                final List<Node> byProduct = target.resultNodes.length < 1
                                           ? Arrays.stream(target.resultNodes)
                                                    .filter(p -> !p.id.equals(subjectNode.id))
                                                    .toList()
                                           : Collections.EMPTY_LIST;
                if (!byProduct.isEmpty()) {
                    for (Node node : byProduct) {
                        final long byProductQuantity = node.quantity*count;
                        if (parent.consumableNode.containsKey(node.id)) {
                            parent.consumableNode.put(node.id, byProductQuantity + parent.consumableNode.get(node.id));
                        } else {
                            parent.consumableNode.put(node.id, byProductQuantity);
                        }
                    }
                }
                if (count!=subjectNode.quantity) {
                    final long diff = getTargetNode(target.resultNodes, subjectNode.id).quantity*count - subjectNode.quantity;
                    if (parent.consumableNode.containsKey(subjectNode.id)) {
                        parent.consumableNode.put(subjectNode.id, diff + parent.consumableNode.get(subjectNode.id));
                    } else {
                        parent.consumableNode.put(subjectNode.id, diff);
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