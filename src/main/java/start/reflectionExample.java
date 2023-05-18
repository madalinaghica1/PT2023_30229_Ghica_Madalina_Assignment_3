package start;

import java.lang.reflect.Field;

/**
 * The reflectionExample class provides utility methods for retrieving properties
 * of an object using reflection.
 */
public class reflectionExample {

    /**
     * Retrieves the properties of an object using reflection and prints them.
     *
     * @param object The object whose properties need to be retrieved.
     */
    public static void retrieveProperties(Object object) {

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // set modifier to public
            Object value;
            try {
                value = field.get(object);
                System.out.println(field.getName() + "=" + value);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }
}
