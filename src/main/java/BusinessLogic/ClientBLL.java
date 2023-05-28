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
import DataAccess.OrderDAO;
import Model.Client;
import Model.Order;

/**
 * The ClientBLL class provides business logic operations related to clients.
 * It manages client validation and interacts with the ClientDAO for data access.
 */
public class ClientBLL {

    /**
     * The list of validators used to validate clients.
     */
    private static List<Validator<Client>> validators;

    /**
     * Initializes a new instance of the ClientBLL class.
     * It adds the necessary validators for client validation.
     */
    public ClientBLL() {
        validators = new ArrayList<Validator<Client>>();
        validators.add(new EmailValidator());
        validators.add(new AgeValidator());
    }

    /**
     * Finds a client by their ID.
     *
     * @param id The ID of the client to find.
     * @return The client with the specified ID.
     * @throws NoSuchElementException If no client with the given ID is found.
     */
    public static Client findClientById(int id) {
        Client st = ClientDAO.findById(id);
        if (st == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return st;
    }

    
    /**
     * Inserts a new client into the system.
     * The client is validated using the registered validators.
     *
     * @param client The client to be inserted.
     * @return The ID of the inserted client.
     */
    public static int insertClient(Client client) {
        if (validators != null) {
            for (Validator<Client> v : validators) {
                v.validate(client);
            }
        }
        return ClientDAO.insert(client);
    }
    /**
     * Sterge un client din baza de date.
     *
     * @param client Clientul care urmeaza sa fie sters.
     * @return true daca clientul a fost sters cu succes, false în caz contrar.
     */
    public static boolean delete(Client client) {
        String sql = "DELETE FROM client WHERE clientID = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, client.getID());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Returneaza o lista care contine toti clientii din baza de date.
     *
     * @return Lista de clienți (List<Client>) care contine toti clientii din baza de date.
     */
    public static List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();

        // Obține conexiunea la baza de date
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            String sql = "SELECT * FROM client";
            statement = connection.prepareStatement(sql);

            resultSet = statement.executeQuery();


            Field[] fields = Client.class.getDeclaredFields();
            List<String> fieldNames = new ArrayList<>();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }


            while (resultSet.next()) {
                int id = resultSet.getInt("clientID");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");
                int age = resultSet.getInt("age");

                Client client = new Client(id, name, address, email, age);


                for (String fieldName : fieldNames) {
                    try {
                        Field field = Client.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = resultSet.getObject(fieldName);
                        field.set(client, value);
                    } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
                        e.printStackTrace();
                    }
                }

                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Închide resursele
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return clients;
    }

}