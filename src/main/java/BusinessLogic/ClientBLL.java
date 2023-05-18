package BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import DataAccess.ClientDAO;
import Model.Client;

/**
 * The ClientBLL class provides business logic operations related to clients.
 * It manages client validation and interacts with the ClientDAO for data access.
 */
public class ClientBLL {

    /**
     * The list of validators used to validate clients.
     */
    private List<Validator<Client>> validators;

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
    public int insertClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        return ClientDAO.insert(client);
    }
}
