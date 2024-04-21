package recipecalc;

import recipecalc.calc.Calculator;
import recipecalc.loader.YamlLoader;

public class Main {
    public static void main(String[] args) {
        Calculator.solveRecipe(YamlLoader.loadAsMap(), args);
    }
}