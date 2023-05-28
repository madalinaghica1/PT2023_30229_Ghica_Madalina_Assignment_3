package Model;

/**
 * The Product class represents a product with its attributes such as ID, name, quantity, and price.
 */
public class Product {
    private int Id;
    private String name;
    private int quantity;
    private float price;

    /**
     * Constructs a new Product object with the specified ID, name, quantity, and price.
     *
     * @param Id    The ID of the product.
     * @param name  The name of the product.
     * @param nr    The quantity of the product.
     * @param price The price of the product.
     */
    public Product(int Id, String name, int nr, float price) {
        this.Id = Id;
        this.name = name;
        this.quantity = nr;
        this.price = price;
    }

    /**
     * Returns the ID of the product.
     *
     * @return The ID of the product.
     */
    public int getID() {
        return Id;
    }

    /**
     * Returns the name of the product.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the quantity of the product.
     *
     * @return The quantity of the product.
     */
    public int getNr() {
        return quantity;
    }

    /**
     * Returns the price of the product.
     *
     * @return The price of the product.
     */
    public float getPrice() {
        return price;
    }

    /**
     * Returns a string representation of the product.
     *
     * @return A string representation of the product.
     */
    public String toString() {
        return "Product [id=" + Id + ", name=" + name + ", nr=" + quantity + ", price=" + price + "]";
    }

    /**
     * Sets the name of the product.
     *
     * @param newName The new name to be set for the product.
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Sets the price of the product.
     *
     * @param newPrice The new price to be set for the product.
     */
    public void setPrice(double newPrice) {
        this.price = (float) newPrice;
    }


    public void setID(int Id) {
        this.Id = Id;
    }

    public void setNr(int newQuantity) {
        this.quantity=newQuantity;
    }


}
