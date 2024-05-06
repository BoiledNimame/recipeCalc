package recipecalc.calc;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import recipecalc.Util;
import recipecalc.node.LinkedNode;
import recipecalc.node.ResourceType;
import recipecalc.node.LinkedNode.RecipePos;

public class Tree {
    public static void printAsTree(LinkedNode origin) {
        Tree tree = new Tree(origin);
        tree.branch();
        tree.printPrimaryResource();
        tree.printByProduct();
    }

    private Tree(LinkedNode origin) {
        this.origin = origin;
        primaryResource = new HashMap<>();
    }
    
    static final String LINE = "│";
    static final String BRANCH = "├";
    static final String END = "└";
    static final String SPACE = " ";
    static final String EMPTY = "";

    private final LinkedNode origin;
    private final Map<String, Long> primaryResource;

    /**
     * 展開開始
     */
    private void branch() {
        System.out.println("Trees:");
        branch(origin);
    }

    /**
     * 再帰的展開により自身の子を全て出力するメソッド
     * @param node {@code pos.equals(RecipePos.HEAD) -> true}である, 全ての親となるノード
     */
    private void branch(LinkedNode node) {
        System.out.println(buildLine(node));
        if (node.child.isEmpty()) {
            addPrimaryResource(node);
        } else {
            for (LinkedNode cNode : node.child) {
                branch(cNode);
            }
        }
    }

    /**
     * 出力するべきノードの内容から構成される文字列を返す
     * @param node 出力対象のノード
     * @return 適切な罫線が行頭に付与された, ノードの内容を表す文字列
     */
    private String buildLine(LinkedNode node) {
        String prefix = !node.pos.equals(RecipePos.HEAD)
                       ? isLastElement(node)
                        ? buildPrefix(node.parent).concat(END)
                        : buildPrefix(node.parent).concat(BRANCH)
                       : EMPTY;
        return prefix.concat(node.display);
    }

    /**
     * 親情報から罫線部分を生成する
     * @param parent 罫線を付与する対象の親となるノード
     * @return 罫線部分
     */
    private String buildPrefix(LinkedNode parent) {
        return !parent.pos.equals(RecipePos.HEAD)
             ? buildPrefix(parent.parent).concat(isLastElement(parent) ? SPACE : LINE)
             : EMPTY;
    }

    /**
     * @return 引数ノードが自身の属する子リストの最後尾であるかどうかを判断する
     * @throws NullPointerException 親を持たない, つまり{@code node.pos.equals(RecipePos.HEAD) -> true}ならエラー
     */
    private boolean isLastElement(LinkedNode node) {
        return Util.getLastElement(node.parent.child).equals(node);
    }

    /**
     * 最後に原料一覧を出力するためのリストへ一次リソースを追加する
     * @param node {@code pos.equals(RecipePos.HEAD) -> true}である, 全ての親となるノード
     */
    private void addPrimaryResource(LinkedNode node) {
        if (!node.pos.equals(RecipePos.TAIL)) {
            throw new IllegalArgumentException("Argument is not a primary resource.");
        }
        if (primaryResource.containsKey(node.name)) {
            primaryResource.put(node.name, node.craftCount + primaryResource.get(node.name));
        } else {
            primaryResource.put(node.name, node.craftCount);
        }
    }

    /**
     * 一次リソースの出力
     */
    private void printPrimaryResource() {
        System.out.println("Resource Requirements:");
        prettyPrinter(origin, sortByValue(primaryResource));
    }

    /**
     * 副産物の出力
     */
    private void printByProduct() {
        System.out.println("ByProducts:");
        prettyPrinter(origin, sortByValue(origin.consumableNode));
    }

    /**
     * 入力したMapの値をソートしたMapを返す
     * @param <T> キー(? extends Object)
     * @param map 値にLong値を持つMap
     * @return 昇順でソートされたLinkedHashMap
     */
    private <T> LinkedHashMap<T, Long> sortByValue(Map<T, Long> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
    }

    /**
     * Yamlなどでみられる配列表記のようにして, Mapを出力する
     * @param node {@code pos.equals(RecipePos.HEAD) -> true}である, 全ての親となるノード
     * @param map 出力したいリソースを{@code (名称, 個数)}でまとめたMap
     */
    private void prettyPrinter(LinkedNode node, Map<String, Long> map) {
        final long longestKeylength = map.entrySet().stream()
            .filter(f -> !f.getKey().equals(node.name))
            .map(m -> m.getKey().length())
            .max(Comparator.naturalOrder()).get();
        final long longestValuelength = map.entrySet().stream()
            .filter(f -> !f.getKey().equals(node.name))
            .map(m -> String.valueOf(m.getValue()).length())
            .max(Comparator.naturalOrder()).get();
        for (Entry<String, Long> entry : map.entrySet()) {
            if (!entry.getKey().equals(node.name) && entry.getValue()!=0) {
                System.out.println(
                    new StringBuilder()
                    .append(" - ")
                    .append(entry.getKey())
                    .append(Util.repeat(SPACE, Math.toIntExact(longestKeylength - entry.getKey().length())))
                    .append(node.getNodeByName(entry.getKey()).type.equals(ResourceType.Item) ? " x" : ":")
                    .append(
                        node.getNodeByName(entry.getKey()).type.equals(ResourceType.Liquid)
                         || node.getNodeByName(entry.getKey()).type.equals(ResourceType.Gas)
                         ? String.valueOf(entry.getValue()).concat("mb")
                         : Util.repeat(SPACE, Math.toIntExact(longestValuelength - String.valueOf(entry.getValue()).length()))
                           .concat(String.valueOf(entry.getValue())))
                    .toString()
                );
            }
        }
    }
}
