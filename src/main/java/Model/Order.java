package Model;

/**
 * The Order class represents an order made by a client for a specific product.
 */
public class Order {

    /**
     * The ID of the order.
     */
    private int idOrder;

    /**
     * The name of the client who made the order.
     */
    private String nameC;

    /**
     * The name of the product being ordered.
     */
    private String nameP;

    /**
     * The quantity of the product being ordered.
     */
    private int q;

    /**
     * Constructs an Order object with the given details.
     *
     * @param idOrder The ID of the order.
     * @param nameC   The name of the client who made the order.
     * @param nameP   The name of the product being ordered.
     * @param q       The quantity of the product being ordered.
     */
    public Order(int idOrder, String nameC, String nameP, int q) {
        this.idOrder = idOrder;
        this.nameC = nameC;
        this.nameP = nameP;
        this.q = q;
    }

    /**
     * Returns the ID of the order.
     *
     * @return The ID of the order.
     */
    public int getIdOrder() {
        return idOrder;
    }

    /**
     * Returns the name of the client who made the order.
     *
     * @return The name of the client who made the order.
     */
    public String getNumeC() {
        return nameC;
    }

    /**
     * Returns the name of the product being ordered.
     *
     * @return The name of the product being ordered.
     */
    public String getNumeP() {
        return nameP;
    }

    /**
     * Returns the quantity of the product being ordered.
     *
     * @return The quantity of the product being ordered.
     */
    public int getCantitate() {
        return q;
    }

    /**
     * Returns a string representation of the Order object.
     *
     * @return A string representation of the Order object.
     */
    @Override
    public String toString() {
        return "Order [idOrder=" + idOrder + ", nameC=" + nameC + ", nameP=" + nameP + ", quantity=" + q + "]";
    }
}
