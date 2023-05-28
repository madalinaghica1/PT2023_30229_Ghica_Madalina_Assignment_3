package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Connection.ConnectionFactory;

/**
 * @Author: Technical University of Cluj-Napoca, Romania Distributed Systems
 *          Research Laboratory, http://dsrl.coned.utcluj.ro/
 * @Since: Apr 03, 2017
 * @Source http://www.java-blog.com/mapping-javaobjects-database-reflection-generics
 */
/**
 * The AbstractDAO class provides a generic implementation for performing common database operations
 * (such as select, insert, update, and delete) on a specific entity type.
 *
 * @param <T> the entity type
 */
public class AbstractDAO<T> {

    /**
     * The logger for logging messages and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    /**
     * The class object representing the entity type.
     */
    private final Class<T> type;

    /**
     * Constructs an AbstractDAO object. This constructor uses reflection to determine the entity type
     * based on the actual type parameter of the subclass.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Creates a SELECT query for retrieving all entities from the corresponding database table.
     *
     * @return the SELECT query string
     */
    private String createSelectQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(type.getSimpleName());
        return sb.toString();
    }

    /**
     * Creates a SELECT query for retrieving entities from the corresponding database table based on a specific field value.
     *
     * @param field the field name
     * @return the SELECT query string
     */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " = ?");
        return sb.toString();
    }

    /**
     * Retrieves all entities from the corresponding database table.
     *
     * @return a list of entities
     */
    public List<T> findAll() {
        // Database connection, statement, and result set
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = createSelectQuery();

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }

    /**
     * Retrieves an entity from the corresponding database table based on its ID.
     *
     * @param id the ID of the entity
     * @return the retrieved entity, or null if not found
     */
    public T findById(int id) {
        // Database connection, statement, and result set
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = createSelectQuery("id");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }

    /**
     * Creates entity objects from the given result set.
     *
     * @param resultSet the result set
     * @return a list of entity objects
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        Constructor<?> ctor = null;

        // Find the constructor with no parameters
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }

        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T) ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | SQLException |
                 IntrospectionException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Inserts an entity into the corresponding database table.
     *
     * @param t the entity to be inserted
     * @return the inserted entity, or null if insertion fails
     */
    public T insert(T t) {
        // Database connection, statement, and result set
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = createInsertQuery();

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setInsertValues(statement, t);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting object failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    setGeneratedId(t, generatedId);
                } else {
                    throw new SQLException("Inserting object failed, no ID obtained.");
                }
            }

            return t;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }

    /**
     * Creates the INSERT query for inserting an entity into the corresponding database table.
     *
     * @return the INSERT query string
     */
    private String createInsertQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(type.getSimpleName());
        sb.append(" (");

        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            sb.append(fields[i].getName());
            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(") VALUES (");

        for (int i = 0; i < fields.length; i++) {
            sb.append("?");
            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Sets the values of the INSERT statement for inserting an entity.
     *
     * @param statement the prepared statement
     * @param t         the entity object
     * @throws SQLException if a database access error occurs
     */
    private void setInsertValues(PreparedStatement statement, T t) throws SQLException {
        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value;
            try {
                value = fields[i].get(t);
                statement.setObject(i + 1, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the generated ID of the inserted entity object.
     *
     * @param t           the entity object
     * @param generatedId the generated ID value
     */
    private void setGeneratedId(T t, int generatedId) {
        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(t, generatedId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an entity in the corresponding database table.
     *
     * @param t the entity to be updated
     * @return the updated entity, or null if update fails
     */
    public T update(T t) {
        // Database connection and statement
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createUpdateQuery();

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            setUpdateValues(statement, t);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating object failed, no rows affected.");
            }

            return t;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }

    /**
     * Creates the UPDATE query for updating an entity in the corresponding database table.
     *
     * @return the UPDATE query string
     */
    private String createUpdateQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(type.getSimpleName());
        sb.append(" SET ");

        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("id")) {
                continue; // Skip the ID field
            }

            sb.append(fields[i].getName());
            sb.append(" = ?");
            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(" WHERE id = ?");

        return sb.toString();
    }

    /**
     * Sets the values of the UPDATE statement for updating an entity.
     *
     * @param statement the prepared statement
     * @param t         the entity object
     * @throws SQLException if a database access error occurs
     */
    private void setUpdateValues(PreparedStatement statement, T t) throws SQLException {
        Field[] fields = type.getDeclaredFields();
        int parameterIndex = 1;
        Object idValue = null;

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getName().equals("id")) {
                try {
                    idValue = field.get(t);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Object value;
                try {
                    value = field.get(t);
                    statement.setObject(parameterIndex, value);
                    parameterIndex++;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        statement.setObject(parameterIndex, idValue);
    }

    /**
     * Deletes an entity from the corresponding database table by its ID.
     *
     * @param id the ID of the entity to be deleted
     */
    public void deleteById(int id) {
        // Database connection and statement
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createDeleteQuery();

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:deleteById " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Creates the DELETE query for deleting an entity from the corresponding database table.
     *
     * @return the DELETE query string
     */
    private String createDeleteQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE id = ?");
        return sb.toString();
    }
}
