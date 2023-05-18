package BusinessLogic;

import Model.Client;

/**
 * The AgeValidator class is responsible for validating the age of a client.
 * It implements the Validator interface for the Client model.
 */
public class AgeValidator implements Validator<Client> {

    /**
     * The minimum age allowed for a client.
     */
    private static final int MIN_AGE = 7;

    /**
     * The maximum age allowed for a client.
     */
    private static final int MAX_AGE = 100;

    /**
     * Validates the age of the given client.
     *
     * @param t The client to be validated.
     * @throws IllegalArgumentException If the client's age is below the minimum age
     *                                  or above the maximum age.
     */
    public void validate(Client t) {
        if (t.getAge() < MIN_AGE || t.getAge() > MAX_AGE) {
            throw new IllegalArgumentException("The client age limit is not respected!");
        }
    }
}
