package recipecalc.recipe;

public class Item {
    public final String id;
    public final long value;
    
    public Item(String id, long value) {
        this.id = id;
        this.value = value;
    }
    
    public boolean equals(Item item) {
        return this.id.equals(item.id);
    }
}
