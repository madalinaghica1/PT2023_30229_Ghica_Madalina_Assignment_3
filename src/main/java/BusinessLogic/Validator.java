package BusinessLogic;

/**
 * The Validator interface represents a generic validator.
 * It defines a single method to validate an object of type T.
 *
 * @param <T> The type of object to be validated.
 */
public interface Validator<T> {

    /**
     * Validates the given object.
     *
     * @param t The object to be validated.
     */
    public void validate(T t);
}
