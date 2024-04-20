package recipecalc.node;

public class Node {
    public final String id;
    public final ResourceType type;
    public final long quantity;
    
    public Node(String id, ResourceType type, long quantity) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
    }

    public boolean equals(Node node) {
        return this.id.equals(node.id)&&this.type==node.type;
    }
}
