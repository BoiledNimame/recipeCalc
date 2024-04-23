package recipecalc.node;

public class RecipeNode {

    public final String name;
    public final Node[] ingredientNodes;
    public final Node[] resultNodes;

    public RecipeNode(String name, Node[] ingredients, Node[] result) {
        this.name = name;
        ingredientNodes = ingredients;
        resultNodes = result;
    }
}