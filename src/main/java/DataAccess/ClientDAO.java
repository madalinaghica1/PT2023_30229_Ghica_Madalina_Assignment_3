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


public class ClientDAO {

    protected static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());
    private static final String insertStatementString = "INSERT INTO client (clientID, name, address, email, age)"
            + " VALUES (?,?,?,?,?)";
    private final static String findStatementString = "SELECT * FROM client WHERE clientID = ?";


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


}
