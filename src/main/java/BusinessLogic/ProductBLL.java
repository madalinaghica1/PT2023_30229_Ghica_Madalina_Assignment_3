package BusinessLogic;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import Connection.ConnectionFactory;
import DataAccess.ClientDAO;
import DataAccess.ProductDAO;
import Model.Client;
import Model.Product;

/**
 * The ProductBLL class provides business logic operations for the Product model.
 */
public class ProductBLL {
    /**
     * The list of validators for Product objects.
     */
    private static List<Validator<Product>> validators;

    /**
     * Constructs a new instance of the ProductBLL class.
     * Initializes the validators list as an empty ArrayList.
     */
    public ProductBLL() {
        validators = new ArrayList<Validator<Product>>();
    }


    /**
     * Inserts a new product.
     * Validates the product using registered validators before insertion.
     *
     * @param produs The product to insert.
     * @return The ID of the inserted product.
     */
    public static int insertProduct(Product produs) {
        if (validators != null) {
            for (Validator<Product> v : validators) {
                v.validate(produs);
            }
        }
        return ProductDAO.insert(produs);
    }
    /**
     * Returneaza o lista de toate produsele din baza de date.
     *
     * @return Lista de produse
     */
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM product";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Obține metodele pentru setarea valorilor câmpurilor
            Field[] fields = Product.class.getDeclaredFields();
            List<String> fieldNames = new ArrayList<>();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }

            while (resultSet.next()) {
                int Id = resultSet.getInt("Id");
                String name = resultSet.getString("name");
                int nr = resultSet.getInt("quantity");
                float price = resultSet.getFloat("price");
                Product product = new Product(Id, name, nr, price);


                for (String fieldName : fieldNames) {
                    try {
                        Field field = Product.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = resultSet.getObject(fieldName);
                        field.set(product, value);
                    } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
                        e.printStackTrace();
                    }
                }

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return products;
    }
    /**
     * Sterge un produs din baza de date.
     *
     * @param product Produsul de șters
     * @return `true` daca produsul a fost sters cu succes, altfel `false`
     */
    public static boolean delete(Product product) {
        String sql = "DELETE FROM product WHERE Id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, product.getID());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizeaza cantitatea unui produs în baza de date.
     *
     * @param product Produsul de actualizat
     * @return `true` daca actualizarea a fost realizata cu succes, altfel `false`
     */
    public static boolean updateProduct(Product product) {
        String sql = "UPDATE product SET quantity = ? WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, product.getNr());
            statement.setInt(2, product.getID());
           // statement.setString(3, product.getNr());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Gaseste un produs în baza de date dupa nume.
     *
     * @param productName Numele produsului de cautat
     * @return Produsul gasit sau `null` daca produsul nu a fost gasit
     */
    public static Product findProductByName(String productName) {
        return ProductDAO.findByName(productName);
    }
}
