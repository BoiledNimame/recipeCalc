package recipecalc.calc;

import java.util.Map;

import recipecalc.loader.YamlParser;
import recipecalc.node.LinkedNode;
import recipecalc.node.NodeLinker;
import recipecalc.node.RecipeNode;

public class Calculator {
    public static void solveRecipe(Map<String, Object> map, String[] problem) {

        String problemName = null;
        int problemQuantity = 0;
        if (problem.length!=0) {
            for (int i = 0; i < problem.length; i++) {
                if (problem[i].split(":")[0].equals("-calc")) {
                    problemName = YamlParser.getDisplayFromKey(map, problem[i].split(":")[1]);
                    problemQuantity = Integer.valueOf(problem[i].split(":")[2]);
                } else {
                    throw new IllegalArgumentException("The jvm startup argument does not contain the calculation target.");
                }
            }
        } else {
            throw new IllegalArgumentException("The jvm startup argument does not contain the calculation target.");
        }

        RecipeNode[] recipes = YamlParser.parseToRecipeNode(map);
        LinkedNode headNode = NodeLinker.LinkNodeFromProblem(recipes, problemName, problemQuantity);
        Tree.printAsTree(headNode);
    }
}
