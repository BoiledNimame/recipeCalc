package recipecalc.recipe;

public class Item {
    public final String id;
    public final long quantity;

    public Item(String id, long quantity) {
        this.id = id;
        this.quantity = quantity;
    }
    
    public boolean equals(Item item) {
        return this.id.equals(item.id);
    }
}
