package recipecalc.node;

import recipecalc.util.ImmutablePair;
import recipecalc.util.Util;

import java.util.ArrayList;
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
    public final Map<String, Long> consumableNode;
    public final long craftCount;

    private final Map<String, ImmutablePair<Node[], Node[]>> registedRecipes;

    /**
     * make LinkedNode as HEAD
     */
    public LinkedNode(RecipeNode targetRecipe, long requiredQuantity, RecipeNode[] allRecipe) {
        registedRecipes = recipeMapper(allRecipe);
        depth = 0;
        name = targetRecipe.name;
        final Node targetNode = NodeLinker.getTargetNode(targetRecipe.resultNodes, name);
        display = displayBuilder(name, targetNode.type, requiredQuantity);
        uuid = UUID.randomUUID();
        pos = RecipePos.HEAD;
        craftCount = NodeLinker.calcCraftCount(targetNode, requiredQuantity);
        parent = null;
        resultNodes = NodeLinker.defineResult(targetRecipe, craftCount);
        consumableNode = new HashMap<>();
        final Node[] consumableByProduct = NodeLinker.getByProductFromResult(targetNode, resultNodes);
        for (int i = 0; i < consumableByProduct.length; i++) {
            consumableNode.put(consumableByProduct[i].id, consumableByProduct[i].quantity);
        }
        child = new ArrayList<>();
        NodeLinker.defineChild(this);
    }

    LinkedNode(LinkedNode parent, RecipeNode targetRecipe, long craftCount) {
        registedRecipes = parent.registedRecipes;
        parent.child.add(this);
        depth = parent.depth+1;
        name = targetRecipe.name;
        loopDetector(this);
        display = displayBuilder(name, getNodeByName(name).type,
                    Util.arrayIsEmpty(targetRecipe.ingredientNodes)
                    ? craftCount
                    : craftCount*NodeLinker.getTargetNode(targetRecipe.resultNodes, name).quantity);
        uuid = UUID.randomUUID();
        final boolean isTail = Util.arrayIsEmpty(targetRecipe.ingredientNodes) && Util.arrayIsEmpty(targetRecipe.resultNodes);
        if (isTail) {
            pos = RecipePos.TAIL;
            resultNodes = new Node[]{};
        } else {
            pos = RecipePos.BODY;
            resultNodes = NodeLinker.defineResult(targetRecipe, craftCount);
        }
        this.craftCount = craftCount;
        this.parent = parent;
        consumableNode = parent.consumableNode;
        child = new ArrayList<>();
        if (this.pos!=RecipePos.TAIL) {
            NodeLinker.defineChild(this);
        }
    }

    private void loopDetector(LinkedNode self) {
        if (self.parent!=null) {
            if (self.parent.name.equals(self.name)) {
                throw new IllegalArgumentException("target has Invalid recipe, that recipe may causes infinity-loops. recipe name:".concat(self.name));
            } else {
                loopDetector(self.parent);
            }
        }
    }

    private static String displayBuilder(String baseName, ResourceType type, long quantity) {
        String targetDisplay;
        switch (type) {
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

    public Node getNodeByName(String name) {
        return registedRecipes.entrySet().stream()
                                .map(f -> Arrays.stream(f.getValue().getKey())
                                    .filter(g -> g.id.equals(name))
                                    .findFirst().orElse(null))
                                .filter(p -> p!=null)
                                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Map<String, ImmutablePair<Node[], Node[]>> recipeMapper(RecipeNode[] recipes) {
        final Map<String, ImmutablePair<Node[], Node[]>> mappedRecipe = new HashMap<>();
        for (int i = 0; i < recipes.length; i++) {
            mappedRecipe.put(recipes[i].name, new ImmutablePair<Node[],Node[]>(recipes[i].ingredientNodes, recipes[i].resultNodes));
        }
        return mappedRecipe;
    }

    public ImmutablePair<Node[], Node[]> getRecipeIOs() {
        return getRecipeIOsFromProductName(this.name);
    }

    public ImmutablePair<Node[], Node[]> getRecipeIOsFromProductName(String name) {
        return registedRecipes.containsKey(name) ? registedRecipes.get(name) : new ImmutablePair<>(new Node[0], new Node[0]);
    }

    public Node consume(Node node) {
        if (this.consumableNode.containsKey(node.id)) {
            final long consumableQuantity = this.consumableNode.get(node.id).longValue();
            if (consumableQuantity < node.quantity) {
                this.consumableNode.remove(node.id);
                return new Node(node.id, node.type, node.quantity - consumableQuantity);
            } else if (consumableQuantity == node.quantity) {
                this.consumableNode.remove(node.id);
                return new Node(node.id, node.type, 0);
            } else if (consumableQuantity > node.quantity) {
                this.consumableNode.put(node.id, consumableQuantity - node.quantity);
                return new Node(node.id, node.type, 0);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            return node;
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
