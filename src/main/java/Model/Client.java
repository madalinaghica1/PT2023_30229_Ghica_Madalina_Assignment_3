package Model;

/**
 * The Client class represents a client with various attributes.
 */
public class Client {

    /**
     * The unique ID of the client.
     */
    private int clientID;

    /**
     * The name of the client.
     */
    private String name;

    /**
     * The address of the client.
     */
    private String address;

    /**
     * The email of the client.
     */
    private String email;

    /**
     * The age of the client.
     */
    private int age;

    /**
     * Constructs a Client object with the specified attributes.
     *
     * @param clientID The unique ID of the client.
     * @param name     The name of the client.
     * @param address  The address of the client.
     * @param email    The email of the client.
     * @param age      The age of the client.
     */
    public Client(int clientID, String name, String address, String email, int age) {
        this.clientID = clientID;
        this.name = name;
        this.address = address;
        this.email = email;
        this.age = age;
    }

    /**
     * Returns the ID of the client.
     *
     * @return The ID of the client.
     */
    public int getID() {
        return clientID;
    }

    /**
     * Returns the name of the client.
     *
     * @return The name of the client.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the address of the client.
     *
     * @return The address of the client.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the email of the client.
     *
     * @return The email of the client.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the age of the client.
     *
     * @return The age of the client.
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns a string representation of the Client object.
     *
     * @return A string representation of the Client object.
     */
    public String toString() {
        return "Client [id=" + clientID + ", name=" + name + ", address=" + address + ", email=" + email + ", age=" + age
                + "]";
    }

    /**
     * Sets the ID of the client.
     *
     * @param id The ID of the client.
     */
    public void setClientID(int id) {
        this.clientID = id;
    }

    /**
     * Sets the name of the client.
     *
     * @param name The name of the client.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the address of the client.
     *
     * @param address The address of the client.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the email of the client.
     *
     * @param email The email of the client.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the age of the client.
     *
     * @param age The age of the client.
     */
    public void setAge(int age) {
        this.age = age;
    }


}
