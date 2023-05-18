package BusinessLogic;

import DataAccess.OrderDAO;
import Model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The OrderBLL class handles business logic operations related to orders.
 */
public class OrderBLL {

    /**
     * The list of validators used to validate orders.
     */
    private List<Validator<Order>> validators;

    /**
     * Constructs a new instance of the OrderBLL class.
     * Initializes the validators list as an empty ArrayList.
     */
    public OrderBLL() {
        validators = new ArrayList<Validator<Order>>();
    }

    /**
     * Finds an order by its ID.
     *
     * @param id The ID of the order to find.
     * @return The found order.
     * @throws NoSuchElementException If the order with the specified ID is not found.
     */
    public static Order findOrderById(int id) {
        Order p = OrderDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The order with id =" + id + " was not found!");
        }
        return p;
    }

    /**
     * Inserts a new order.
     *
     * @param order The order to be inserted.
     * @return The ID of the inserted order.
     * @throws IllegalArgumentException If the order fails validation.
     */
    public int insertProduct(Order order) {
        for (Validator<Order> v : validators) {
            v.validate(order);
        }
        return OrderDAO.insert(order);
    }
}
