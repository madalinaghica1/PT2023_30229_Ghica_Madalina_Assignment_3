package Model;
import Connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**Clasa  pentru factura**/
public record Bill(int pName, int q) {
    private String createInsertQuery() {
        StringBuilder rez = new StringBuilder();
        rez.append("INSERT INTO Bill (productname, amount, clientid) VALUES (?, ?, ?)");
        return rez.toString();
    }

    private void setInsertValues(PreparedStatement statement) throws SQLException {
        statement.setInt(1, pName);
        statement.setInt(2, q);
    }
    public void insert() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            String query = createInsertQuery();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                setInsertValues(statement);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}