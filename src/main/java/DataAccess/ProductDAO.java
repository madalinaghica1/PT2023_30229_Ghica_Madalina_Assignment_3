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
import Model.Client;
import Model.Product;

/**
 * The ProductDAO class is responsible for performing CRUD operations on the product table in the database.
 * It provides methods to find a product by ID and insert a new product.
 */
public class ProductDAO {

    /**
     * The logger for logging messages.
     */
    protected static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    /**
     * The SQL statement for inserting a new product into the database.
     */
    private static final String insertStatementString = "INSERT INTO product (Id, name, quantity, price) VALUES (?,?,?,?)";

    /**
     * The SQL statement for finding a product by ID in the database.
     */
    private final static String findStatementString = "SELECT * FROM product WHERE Id = ?";

    /**
     * Finds a product by its ID.
     *
     * @param pId The ID of the product to find.
     * @return The product with the given ID if found, or null if not found.
     */
    public static Product findById(int pId) {
        Product toReturn = null;
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        try {
            findStatement = dbConnection.prepareStatement(findStatementString);
            findStatement.setLong(1, pId);
            rs = findStatement.executeQuery();
            if (rs.next()) {
                int Id = rs.getInt("Id");
                String name = rs.getString("name");
                int nr = rs.getInt("quantity");
                float price = rs.getFloat("price");
                toReturn = new Product(Id, name, nr, price);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ProductDAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(rs);
            ConnectionFactory.close(findStatement);
            ConnectionFactory.close(dbConnection);
        }
        return toReturn;
    }

    /**
     * Inserts a new product into the database.
     *
     * @param p The product to insert.
     * @return The auto-generated ID of the inserted product.
     */
    public static int insert(Product p) {
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement insertStatement = null;
        int insertedId = 0;
        try {
            insertStatement = dbConnection.prepareStatement(insertStatementString, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, p.getID());
            insertStatement.setString(2, p.getName());
            insertStatement.setInt(3, p.getNr());
            insertStatement.setFloat(4, p.getPrice());

            insertStatement.executeUpdate();

            ResultSet rs = insertStatement.getGeneratedKeys();
            if (rs.next()) {
                insertedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ProductDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(insertStatement);
            ConnectionFactory.close(dbConnection);
        }
        return insertedId;
    }


    public static Product findByName(String productName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Product product = null;

        try {
            connection = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM product WHERE name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, productName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int quantity = resultSet.getInt("quantity");
                float price = resultSet.getFloat("price");
                // Assuming the column names and data types in the result set match the Product model
                product = new Product(id, productName, quantity, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return product;
    }
}