import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main2{
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Inventary";
    private static final String USER = "root";   
    private static final String PASS = "root";

    private Connection connection;
    private Scanner scanner;

    public Main2() {
        try {
            //load the JDBC driver
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
            scanner = new Scanner(System.in);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("Database connection failed. Check your DB_URL, USER, PASS, and ensure MySQL is running.");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println("JDBC Driver not found. Make sure mysql-connector-j-x.x.x.jar is in your classpath.");
        }
    }

    public void start() {
        if (connection == null) {
            System.err.println("Application cannot start without a database connection.");
            return;
        }

        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewAllProducts();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    System.out.println("Exiting Inventory Application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        } while (choice != 5);

        closeResources();
    }

    private void displayMenu() {
        System.out.println("--- Simple Inventory Application ---");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Update Product Quantity/Price");
        System.out.println("4. Delete Product");
        System.out.println("5. Exit");
    }
    //--CRUD OPERATIONS--
     //CREATE
    private void addProduct() {
        System.out.println("\n--- Add New Product ---");
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        String sql = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Product '" + name + "' added successfully.");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    //READ
    private void viewAllProducts() {
        System.out.println("\n--- All Products in Inventory ---");
        String sql = "SELECT id, name, quantity, price FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No products found in inventory.");
                return;
            }

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                System.out.printf("ID: %d, Name: %s, Quantity: %d, Price: %.2f%n", id, name, quantity, price);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
//UPDATE
    private void updateProduct() {
        System.out.println("\n--- Update Product ---");
        System.out.print("Enter product ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new price: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();

//FIX: the original code was missing the sql query and the try-with resources block. 
// It's been fixed to properly prepare and execute  the update statement.

        String sql = "UPDATE products SET quantity = ?, price = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setDouble(2, newPrice);
            pstmt.setInt(3, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Product with ID " + id + " updated successfully.");
            } else {
                System.out.println("No product found with ID " + id + " to update.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // DELETE
    private void deleteProduct() {
        System.out.println("\n--- Delete Product ---");
        System.out.print("Enter product ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Product with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No product found with ID " + id + " to delete.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // Method to close resources
    private void closeResources() {
        try {
            if (scanner != null) scanner.close();
            if (connection != null) connection.close();
            System.out.println("Resources closed successfully.");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main2 app = new Main2();
        app.start();
}
}
