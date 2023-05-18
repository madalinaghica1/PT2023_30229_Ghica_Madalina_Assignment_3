package Presentation;
import DataAccess.ClientDAO;
import DataAccess.ProductDAO;
import DataAccess.OrderDAO;
import Model.Client;
import Model.Order;
import Model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * The GUIInterface class represents the graphical user interface for managing clients and products.
 * It provides functionality to add, edit, and delete clients and products, as well as create product orders.
 */
public class GUIInterface {
    private JFrame mainFrame;
    private JTable clientTable;
    private JTable productTable;

    private DefaultTableModel clientTableModel;
    private DefaultTableModel productTableModel;
    private List<Client> clientList;
    private List<Product> productList;
    private JTextField orderIdTextField;
    private JTextField clientNameTextField;
    private JTextField productNameTextField;
    private JTextField quantityTextField;
    private JButton createOrderButton;
    /**
     * Constructs a GUIInterface object and initializes the GUI components.
     */
    public GUIInterface() {
        initialize();
    }
    /**
     * Initializes the GUI components and sets up the main frame.
     */
    private void initialize() {
        mainFrame = new JFrame("GUI Interface");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create the client table
        clientTableModel = new DefaultTableModel();
        clientTable = new JTable(clientTableModel);
        JScrollPane clientScrollPane = new JScrollPane(clientTable);
        clientScrollPane.setPreferredSize(new Dimension(400, 200));
        // Create the product table
        productTableModel = new DefaultTableModel();
        productTable = new JTable(productTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setPreferredSize(new Dimension(400, 200));
        // Create buttons for client operations
        JButton addClientButton = new JButton("Add Client");
        JButton editClientButton = new JButton("Edit Client");
        JButton deleteClientButton = new JButton("Delete Client");
        // Create buttons for product operations
        JButton addProductButton = new JButton("Add Product");
        JButton editProductButton = new JButton("Edit Product");
        JButton deleteProductButton = new JButton("Delete Product");
        // Create the button for creating product orders
        createOrderButton = new JButton("Create Order");

        addClientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(JOptionPane.showInputDialog(mainFrame, "Enter client id:"));
                String name = JOptionPane.showInputDialog(mainFrame, "Enter client name:");
                String address = JOptionPane.showInputDialog(mainFrame, "Enter client address:");
                String email = JOptionPane.showInputDialog(mainFrame, "Enter client email:");
                int age = Integer.parseInt(JOptionPane.showInputDialog(mainFrame, "Enter client age:"));
                Client newClient = new Client(id, name, address, email, age);
                int insertedId = ClientDAO.insert(newClient);
                if (insertedId != 0) {
                    newClient.setClientID(insertedId);
                    clientList.add(newClient);
                    populateTable(clientList, clientTableModel);
                    JOptionPane.showMessageDialog(mainFrame, "Client added successfully!");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to add client!");
                }
            }
        });
        editClientButton.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow != -1) {
                Client client = clientList.get(selectedRow);
                openEditClientDialog(client);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select a client to edit!");
            }
        });
        deleteClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cod pentru ștergerea unui client
                int selectedRow = clientTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirmed = JOptionPane.showConfirmDialog(mainFrame,
                            "Are you sure you want to delete this client?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmed == JOptionPane.YES_OPTION) {
                        Client client = clientList.get(selectedRow);
                        boolean deleted = deleteClient(client); // Apelul metodei de ștergere a clientului
                        if (deleted) {
                            clientList.remove(client);
                            populateTable(clientList, clientTableModel);
                            JOptionPane.showMessageDialog(mainFrame, "Client deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Failed to delete client!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Please select a client to delete!");
                }
            }
        });
        addProductButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(JOptionPane.showInputDialog(mainFrame, "Enter Product id:"));
                String name = JOptionPane.showInputDialog(mainFrame, "Enter  name:");
                int nr = Integer.parseInt(JOptionPane.showInputDialog(mainFrame, "Enter nr:"));
                float price = Float.parseFloat(JOptionPane.showInputDialog(mainFrame, "Enter price:"));
                Product newProduct = new Product(id, name, nr, price);
                int insertedId = ProductDAO.insert(newProduct);
                if (insertedId != 0) {
                    newProduct.setID(insertedId);
                    productList.add(newProduct);
                    populateTable(productList, productTableModel);
                    JOptionPane.showMessageDialog(mainFrame, "Product added successfully!");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to add product!");
                }
            }
        });
        editProductButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                Product product = productList.get(selectedRow);
                openEditProductDialog(product);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select a product to edit!");
            }
        });
        deleteProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirmed = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete this product?", "Confirm Delete",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmed == JOptionPane.YES_OPTION) {
                        Product product = productList.get(selectedRow);
                        boolean deleted = deleteProduct(product); // Apelul metodei de ștergere a produsului
                        if (deleted) {
                            productList.remove(product);
                            populateTable(productList, productTableModel);
                            JOptionPane.showMessageDialog(mainFrame, "Product deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Failed to delete product!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Please select a product to delete!");
                }
            }
        });
        createOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obțineți comenzile din baza de date
                List<Order> orderList = OrderDAO.getAllOrders();

                // Crearea și configurarea ferestrei noi
                JFrame orderFrame = new JFrame("Orders");
                orderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Crearea modelului și tabelului pentru comenzile din baza de date
                DefaultTableModel orderTableModel = new DefaultTableModel();
                JTable orderTable = new JTable(orderTableModel);
                JScrollPane orderScrollPane = new JScrollPane(orderTable);
                orderScrollPane.setPreferredSize(new Dimension(400, 200));

                // Generați antetul tabelului pentru comenzile din baza de date
                generateTableHeader(Order.class, orderTableModel);

                // Populați tabelul cu datele comenzilor din baza de date
                populateTable(orderList, orderTableModel);

                // Adăugarea tabelului la fereastra nouă
                orderFrame.add(orderScrollPane);
                orderFrame.pack();
                orderFrame.setVisible(true);

                int orderId = Integer.parseInt(orderIdTextField.getText());
                String clientName = clientNameTextField.getText();
                String productName = productNameTextField.getText();
                int quantity = Integer.parseInt(quantityTextField.getText());

                // Display the order information
                JOptionPane.showMessageDialog(mainFrame, "Order ID: " + orderId +
                        "\nClient Name: " + clientName +
                        "\nProduct Name: " + productName +
                        "\nQuantity: " + quantity);

                // Clear the text fields
                orderIdTextField.setText("");
                clientNameTextField.setText("");
                productNameTextField.setText("");
                quantityTextField.setText("");

                // Add the order to the database
                Order newOrder = new Order(orderId, clientName, productName, quantity);
                int added = OrderDAO.insert(newOrder); // Assuming OrderDAO.insert() inserts the order into the database
                if (added!=0) {
                    // Add the order to the order list
                    orderList.add(newOrder);
                    // Refresh the order table
                    populateTable(orderList, orderTableModel);
                    // Show success message
                    JOptionPane.showMessageDialog(mainFrame, "Order created successfully!");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to create order!");
                }
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(clientScrollPane, BorderLayout.NORTH);
        mainPanel.add(productScrollPane, BorderLayout.CENTER);

        JPanel clientOperationsPanel = new JPanel();
        clientOperationsPanel.add(addClientButton);
        clientOperationsPanel.add(editClientButton);
        clientOperationsPanel.add(deleteClientButton);

        JPanel productOperationsPanel = new JPanel();
        productOperationsPanel.add(addProductButton);
        productOperationsPanel.add(editProductButton);
        productOperationsPanel.add(deleteProductButton);

        JPanel orderPanel = new JPanel();
        orderPanel.add(new JLabel("Order ID:"));
        orderIdTextField = new JTextField(10);
        orderPanel.add(orderIdTextField);
        orderPanel.add(new JLabel("Client Name:"));
        clientNameTextField = new JTextField(20);
        orderPanel.add(clientNameTextField);
        orderPanel.add(new JLabel("Product Name:"));
        productNameTextField = new JTextField(20);
        orderPanel.add(productNameTextField);
        orderPanel.add(new JLabel("Quantity:"));
        quantityTextField = new JTextField(10);
        orderPanel.add(quantityTextField);
        orderPanel.add(createOrderButton);

       // mainPanel.add(orderScrollPane, BorderLayout.CENTER);
        mainPanel.add(clientOperationsPanel, BorderLayout.WEST);
        mainPanel.add(productOperationsPanel, BorderLayout.EAST);
        mainPanel.add(orderPanel, BorderLayout.SOUTH);
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    /**
     * Opens a dialog for editing a client's information.
     *
     * @param client The client to be edited.
     */
        private void openEditClientDialog(Client client) {
            String newId = JOptionPane.showInputDialog(mainFrame, "Enter new Id:", client.getID());
            String newName = JOptionPane.showInputDialog(mainFrame, "Enter new name:", client.getName());
            String newAddress = JOptionPane.showInputDialog(mainFrame, "Enter new address:", client.getAddress());
            String newEmail = JOptionPane.showInputDialog(mainFrame, "Enter new email:", client.getEmail());
            String newAge = JOptionPane.showInputDialog(mainFrame, "Enter new age:", client.getAge());

                int confirmed = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to update this client?", "Confirm Update", JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    client.setClientID(Integer.parseInt(newId));
                    client.setName(newName);
                    client.setAddress(newAddress);
                    client.setEmail(newEmail);
                    client.setAge(Integer.parseInt(newAge));
                    populateTable(clientList, clientTableModel);
                    JOptionPane.showMessageDialog(mainFrame, "Client updated successfully!");
                }
            }

    private void openEditProductDialog(Product product) {
        String newName = JOptionPane.showInputDialog(mainFrame, "Enter new name:", product.getName());
        if (newName != null) {
            double newPrice = Double.parseDouble(JOptionPane.showInputDialog(mainFrame, "Enter new price:", product.getPrice()));
            int confirmed = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to update this product?", "Confirm Update",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                product.setName(newName);
                product.setPrice(newPrice);
                populateTable(productList, productTableModel);
                JOptionPane.showMessageDialog(mainFrame, "Product updated successfully!");
            }
        }
    }
    /**
     * Deletes a product from the system.
     *
     * @param product The product to be deleted.
     * @return {@code true} if the product was successfully deleted, {@code false} otherwise.
     */
    private boolean deleteProduct(Product product) {
        try {
            boolean deleted = ProductDAO.delete( product); // Utilizarea ClientDAO pentru a șterge clientul din baza de date
            return deleted;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // returnează false dacă a apărut o eroare în timpul ștergerii
        }
    }
    /**
     * Deletes a client from the system.
     *
     * @param client The client to be deleted.
     * @return {@code true} if the client was successfully deleted, {@code false} otherwise.
     */
    private boolean deleteClient(Client client) {
        try {
            boolean deleted = ClientDAO.delete(client); // Utilizarea ClientDAO pentru a șterge clientul din baza de date
            return deleted;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // returnează false dacă a apărut o eroare în timpul ștergerii
        }
    }
    /**
     * Generates the table header based on the fields of a class.
     *
     * @param clazz The class representing the table's data type.
     * @param model The table model.
     */
    private void generateTableHeader(Class<?> clazz, DefaultTableModel model) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            model.addColumn(field.getName());
        }
    }
    /**
     * Populates the table with data from a list.
     *
     * @param list  The list containing the data.
     * @param model The table model.
     */
    private void populateTable(List<?> list, DefaultTableModel model) {
        model.setRowCount(0);
        for (Object obj : list) {
            List<Object> rowData = new ArrayList<>();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    rowData.add(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            model.addRow(rowData.toArray());
        }
    }
    /**
     * The main entry point of the application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        GUIInterface gui = new GUIInterface();
        gui.clientList = ClientDAO.getAllClients(); // Obține lista de clienți din baza de date
        gui.productList = ProductDAO.getAllProducts(); //
        gui.generateTableHeader(Client.class, gui.clientTableModel);
        gui.generateTableHeader(Product.class, gui.productTableModel);
        gui.populateTable(gui.clientList, gui.clientTableModel);
        gui.populateTable(gui.productList, gui.productTableModel);
    }
}


