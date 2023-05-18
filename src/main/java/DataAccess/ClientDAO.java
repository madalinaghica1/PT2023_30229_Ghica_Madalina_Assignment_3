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

/**
 * The ClientDAO class is responsible for database operations related to the Client entity.
 * It provides methods for finding a client by ID and inserting a new client into the database.
 *
 * @Author: Technical University of Cluj-Napoca, Romania Distributed Systems
 *          Research Laboratory, http://dsrl.coned.utcluj.ro/
 * @Since: Apr 03, 2017
 */
public class ClientDAO {

    protected static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());
    private static final String insertStatementString = "INSERT INTO client (clientID, name, address, email, age)"
            + " VALUES (?,?,?,?,?)";
    private final static String findStatementString = "SELECT * FROM client WHERE clientID = ?";

    /**
     * Retrieves a client from the database based on the given client ID.
     *
     * @param clientId The ID of the client to find.
     * @return The found client, or null if not found.
     */
    public static Client findById(int clientId) {
        Client toReturn = null;

        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        try {
            findStatement = dbConnection.prepareStatement(findStatementString);
            findStatement.setLong(1, clientId);
            rs = findStatement.executeQuery();
            rs.next();
            int clientID = rs.getInt("clientID");
            String name = rs.getString("name");
            String address = rs.getString("address");
            String email = rs.getString("email");
            int age = rs.getInt("age");
            toReturn = new Client(clientID, name, address, email, age);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ClientDAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(rs);
            ConnectionFactory.close(findStatement);
            ConnectionFactory.close(dbConnection);
        }
        return toReturn;
    }

    /**
     * Inserts a new client into the database.
     *
     * @param client The client to insert.
     * @return The ID of the inserted client.
     */
    public static int insert(Client client) {
        Connection dbConnection = ConnectionFactory.getConnection();

        PreparedStatement insertStatement = null;
        int insertedId = 0;
        try {
            insertStatement = dbConnection.prepareStatement(insertStatementString, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, client.getID());
            insertStatement.setString(2, client.getName());
            insertStatement.setString(3, client.getAddress());
            insertStatement.setString(4, client.getEmail());
            insertStatement.setInt(5, client.getAge());
            insertStatement.executeUpdate();

            ResultSet rs = insertStatement.getGeneratedKeys();
            if (rs.next()) {
                insertedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ClientDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(insertStatement);
            ConnectionFactory.close(dbConnection);
        }
        return insertedId;
    }

    public static List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();

        // Obține conexiunea la baza de date
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Creează instrucțiunea SQL pentru a obține toți clienții
            String sql = "SELECT * FROM client";
            statement = connection.prepareStatement(sql);

            // Execută interogarea
            resultSet = statement.executeQuery();

            // Parcurge rezultatele și construiește obiectele Client corespunzătoare
            while (resultSet.next()) {
                int id = resultSet.getInt("clientID");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String email = resultSet.getString("email");
                int age = resultSet.getInt("age");

                Client client = new Client(id, name, address, email, age);
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

    public static boolean delete(Client client) {
            String sql = "DELETE FROM client WHERE clientID = ?"; // Schimbați "clients" cu numele tabelului corespunzător în baza de date

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
}

