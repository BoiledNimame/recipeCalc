package recipecalc.loader;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import recipecalc.node.Node;
import recipecalc.node.RecipeNode;
import recipecalc.node.ResourceType;

public class YamlParser {
    private final static String display = "display";
    private final static String recipe = "recipe";
    private final static String result = "result";
    private final static String type = "type";

    public static RecipeNode[] parseToRecipeNode(Map<String, Object> map) {
        final RecipeNode[] nodes = new RecipeNode[map.size()];
        final String[] keys = map.keySet().toArray(new String[map.size()]);
        for (int i = 0; i < nodes.length; i++) {
            final String currentKey = keys[i];

            @SuppressWarnings("unchecked")
            final Map<String, Object> currentMap= (Map<String, Object>)map.get(currentKey);

            final String name = getName(currentMap, currentKey);

            final Node[] recipeNodes = getRecipe(currentMap);

            final Node[] resultNodes = getResult(map, currentMap);

            nodes[i] = new RecipeNode(name, recipeNodes, resultNodes);
        }
        return nodes;
    }

    private static String getName(Map<String, Object> map, String elseIf) {
        return map.containsKey(display) ? map.get("display").toString() : elseIf;
    }

    private static Node[] getRecipe(Map<String, Object> map) {
        if (map.containsKey(recipe)) {

            @SuppressWarnings("unchecked")
            final List<List<String>> recipes = (List<List<String>>) map.get(recipe);
            final Map<String, Integer> requireItems = new HashMap<>();
            
            for (int i = 0; i < recipes.size(); i++) {
                final int currentLineLength = recipes.get(i).size();
                for (int j = 0; j < currentLineLength; j++) {
                    final String currentItem = recipes.get(i).get(j);
                    if (requireItems.containsKey(currentItem)) {
                        requireItems.put(currentItem, requireItems.get(currentItem)+1);
                    } else {
                        requireItems.put(currentItem, 1);
                    }
                }
            }

            final int recipeItemSize = requireItems.size();
            final String[] keys = new String[recipeItemSize];
            final int[] values = new int[recipeItemSize];
            final Node[] nodes = new Node[recipeItemSize];

            // おいここなんもしてねえじゃねえか！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

            for (int i = 0; i < recipeItemSize; i++) {
                nodes[i] = nodeBuilder(map, keys[i], values[i]);
            }

            return nodes;
        } else {
            return new Node[]{};
        }
    }

    private static Node[] getResult(Map<String, Object> parentMap, Map<String, Object> map) {
        if (map.containsKey(result)) {

            @SuppressWarnings("unchecked")
            final List<Map<String, Integer>> results = (List<Map<String, Integer>>) map.get(result);

            final int resultSize = results.size();
            final Node[] resultNodes = new Node[resultSize];
            for (int i = 0; i < resultSize; i++) {
                final Map<String, Integer> currentItem = results.get(i);
                final String currentItemKey = currentItem.keySet().toArray(new String[]{})[0];
                @SuppressWarnings("unchecked")
                final Map<String, Object> targetItemMap = ((Map<String, Object>)parentMap.get(currentItemKey));
                String currentItemKeyFirst = targetItemMap.containsKey(display)
                                           ? targetItemMap.get(display).toString()
                                           : currentItemKey;
                resultNodes[i] = nodeBuilder(map, currentItemKeyFirst, currentItem.get(currentItemKey));
            }
            return resultNodes;
        } else {
            return new Node[]{};
        }
    }

    private static Node nodeBuilder(Map<String, Object> map, String key, int value) {
        ResourceType rType;
        if (map.containsKey(type)) {
            switch ((String) map.get(type)) {
                case "item":
                    rType = ResourceType.Item;
                    break;
                case "liquid":
                    rType = ResourceType.Liquid;
                    break;
                case "gas":
                    rType = ResourceType.Gas;
                    break;
                default:
                    rType = ResourceType.Item;
                    break;
            }
        } else {
            rType = ResourceType.Item;
        }
        return new Node(key, rType, value);
    }

    public static String getDisplayFromKey(Map<String, Object> map, String key) {
        @SuppressWarnings("unchecked")
        final String result = ((Map<String, Object>) map.get(key)).get("display").toString();
        return result;
    }
}
