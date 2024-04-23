package recipecalc.node;

import java.util.AbstractMap.SimpleImmutableEntry;
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

    private final Map<String, SimpleImmutableEntry<Node[], Node[]>> registedRecipes;

    /**
     * make LinkedNode as HEAD
     */
    public LinkedNode(RecipeNode targetRecipe, long requiredQuantity, RecipeNode[] allRecipe) {
        registedRecipes = recipeMapper(allRecipe);
        depth = 0;
        name = targetRecipe.name;
        final Node targetNode = Arrays.stream(targetRecipe.resutNodes)
                                        .filter(f -> f.id.equals(name))
                                        .findFirst().orElseThrow(IllegalArgumentException::new);
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
        display = displayBuilder(name, getNodeByName(name).type, craftCount);
        uuid = UUID.randomUUID();
        final boolean isTail = Util.arrayIsEmpty(targetRecipe.ingredientNodes) && Util.arrayIsEmpty(targetRecipe.resutNodes);
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
        NodeLinker.defineChild(this);
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
                                .map(f -> Arrays.stream(f.getValue().getValue())
                                    .filter(g -> g.id.equals(name))
                                    .findFirst().orElseThrow(IllegalArgumentException::new))
                                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Map<String, SimpleImmutableEntry<Node[], Node[]>> recipeMapper(RecipeNode[] recipes) {
        final Map<String, SimpleImmutableEntry<Node[], Node[]>> mappedRecipe = new HashMap<>();
        for (int i = 0; i < recipes.length; i++) {
            mappedRecipe.put(recipes[i].name, new SimpleImmutableEntry<Node[],Node[]>(recipes[i].ingredientNodes, recipes[i].resutNodes));
        }
        return mappedRecipe;
    }

    public SimpleImmutableEntry<Node[], Node[]> getRecipeIOsFromProductName(String name) {
        return registedRecipes.containsKey(name) ? registedRecipes.get(name) : new SimpleImmutableEntry<>(new Node[0], new Node[0]);
    }

    public boolean equals(LinkedNode node) {
        return this.uuid.equals(node.uuid);
    }

    public enum RecipePos {
        HEAD,BODY,TAIL;
        RecipePos() {}
    }
}
