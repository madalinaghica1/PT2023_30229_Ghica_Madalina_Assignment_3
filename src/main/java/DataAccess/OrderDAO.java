package DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Connection.ConnectionFactory;
import Model.Order;
import Model.Product;

/**
 * The OrderDAO class is responsible for accessing and manipulating order data in the database.
 * It provides methods to find orders by ID and insert new orders.
 */
public class OrderDAO {

    /**
     * The logger for logging messages and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    /**
     * The SQL statement for inserting an order into the database.
     */
    private static final String insertStatementString = "INSERT INTO tp.order (idOrder,nameC,nameP,quantity) VALUES (?,?,?,?)";

    /**
     * The SQL statement for finding an order by ID in the database.
     */
    private final static String findStatementString = "SELECT * FROM tp.order where idOrder = ?";

    /**
     * Inserts a new order into the database.
     *
     * @param ord The order to insert.
     * @return The ID of the inserted order.
     */
    public static int insert(Order ord) {
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement insertStatement = null;
        int insertedId = 0;
        try {
            insertStatement = dbConnection.prepareStatement(insertStatementString, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, ord.getIdOrder());
            insertStatement.setString(2, ord.getNumeC());
            insertStatement.setString(3, ord.getNumeP());
            insertStatement.setInt(4, ord.getCantitate());

            insertStatement.executeUpdate();

            ResultSet rs = insertStatement.getGeneratedKeys();
            if (rs.next()) {
                insertedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "OrderDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(insertStatement);
            ConnectionFactory.close(dbConnection);
        }
        return insertedId;
    }


}