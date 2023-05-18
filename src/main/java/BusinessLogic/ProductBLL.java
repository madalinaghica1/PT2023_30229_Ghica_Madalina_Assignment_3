package BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import DataAccess.ProductDAO;
import Model.Product;

/**
 * The ProductBLL class provides business logic operations for the Product model.
 */
public class ProductBLL {
    /**
     * The list of validators for Product objects.
     */
    private List<Validator<Product>> validators;

    /**
     * Constructs a new instance of the ProductBLL class.
     * Initializes the validators list as an empty ArrayList.
     */
    public ProductBLL() {
        validators = new ArrayList<Validator<Product>>();
    }

    /**
     * Finds a product by its ID.
     *
     * @param id The ID of the product to find.
     * @return The found product.
     * @throws NoSuchElementException If the product with the given ID is not found.
     */
    public static Product findProductById(int id) {
        Product p = ProductDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with ID = " + id + " was not found!");
        }
        return p;
    }

    /**
     * Inserts a new product.
     * Validates the product using registered validators before insertion.
     *
     * @param produs The product to insert.
     * @return The ID of the inserted product.
     */
    public int insertProduct(Product produs) {
        for (Validator<Product> v : validators) {
            v.validate(produs);
        }
        return ProductDAO.insert(produs);
    }
}
