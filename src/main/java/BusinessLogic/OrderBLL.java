package BusinessLogic;

import DataAccess.OrderDAO;
import Model.Order;
import Connection.ConnectionFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private static List<Validator<Order>> validators;

    /**
     * Constructs a new instance of the OrderBLL class.
     * Initializes the validators list as an empty ArrayList.
     */
    public OrderBLL() {
        validators = new ArrayList<Validator<Order>>();
    }

    /**
     * Inserts a new order.
     *
     * @param order The order to be inserted.
     * @return The ID of the inserted order.
     * @throws IllegalArgumentException If the order fails validation.
     */
    public static int insertOrder(Order order) {
        if (validators != null) {
            for (Validator<Order> v : validators) {
                v.validate(order);
            }
        }
        return OrderDAO.insert(order);
    }
    /**
     * Returneaza o lista care contine toate comenzile din baza de date.
     *
     * @return Lista de comenzi (List<Order>) care contine toate comenzile din baza de date.
     */

    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM tp.order";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Obține metodele pentru setarea valorilor câmpurilor
            Field[] fields = Order.class.getDeclaredFields();
            List<String> fieldNames = new ArrayList<>();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }

            while (resultSet.next()) {
                int idOrder = resultSet.getInt("idOrder");
                String nameC = resultSet.getString("nameC");
                String nameP = resultSet.getString("nameP");
                    int q = resultSet.getInt("quantity");
                Order order = new Order(idOrder, nameC, nameP, q);

                for (String fieldName : fieldNames) {
                    try {
                        Field field = Order.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = resultSet.getObject(fieldName);
                        field.set(order, value);
                    } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
                        e.printStackTrace();
                    }
                }

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return orders;
    }
}
