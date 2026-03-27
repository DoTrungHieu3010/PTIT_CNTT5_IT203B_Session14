package entity;

public class OrderItem {
    public int productId;
    public int quantity;

    public OrderItem(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
