package recipecalc.node;

public class Node {
    public final String name;
    public final ResourceType type;
    public final long quantity;
    
    public Node(String id, ResourceType type, long quantity) {
        this.name = id;
        this.type = type;
        this.quantity = quantity;
    }

    public boolean equals(Node node) {
        return this.name.equals(node.name)&&this.type==node.type;
    }
}
