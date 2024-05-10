package recipecalc.node.link;

import recipecalc.node.Node;
import recipecalc.node.RecipeNode;
import recipecalc.node.ResourceType;
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
            consumableNode.put(consumableByProduct[i].name, consumableByProduct[i].quantity);
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
        StringBuilder builder = new StringBuilder(baseName);
        builder.append(" ");
        switch (type) {
            case Item:
                builder.append("x").append(String.valueOf(quantity));
                break;
            case Liquid:
                builder.append(String.valueOf(quantity)).append("mb");
                break;
            case Gas:
                builder.append(String.valueOf(quantity)).append("mb");
                break;
            default:
                builder.append("x").append(String.valueOf(quantity));
                break;
        }
        return builder.toString();
    }

    public Node getNodeByName(String name) {
        return registedRecipes.entrySet().stream()
                                .map(f -> Arrays.stream(f.getValue().getKey())
                                    .filter(g -> g.name.equals(name))
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
        if (this.consumableNode.containsKey(node.name)) {
            final long consumableQuantity = this.consumableNode.get(node.name).longValue();
            if (consumableQuantity < node.quantity) {
                this.consumableNode.remove(node.name);
                return new Node(node.name, node.type, node.quantity - consumableQuantity);
            } else if (consumableQuantity == node.quantity) {
                this.consumableNode.remove(node.name);
                return new Node(node.name, node.type, 0);
            } else if (consumableQuantity > node.quantity) {
                this.consumableNode.put(node.name, consumableQuantity - node.quantity);
                return new Node(node.name, node.type, 0);
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
}
