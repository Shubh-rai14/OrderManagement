import java.sql.*;
import java.util.Scanner;

public class Main{

    static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    static final String USER = "root";
    // Replace this placeholder with your own MySQL password.
    static final String PASSWORD = "YOUR_MYSQL_PASSWORD";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Database Connected Successfully!");

            while (true) {
                System.out.println("\n===== ORDER MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert Orders");
                System.out.println("2. Display Orders by Status");
                System.out.println("3. Increase Order Quantity");
                System.out.println("4. Update Order Status");
                System.out.println("5. Display Total Value and Delete Order");
                System.out.println("6. Exit");

                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        insertOrders(con);
                        break;
                    case 2:
                        displayOrders(con);
                        break;
                    case 3:
                        increaseQuantity(con);
                        break;
                    case 4:
                        updateStatus(con);
                        break;
                    case 5:
                        totalAndDelete(con);
                        break;
                    case 6:
                        System.out.println("Thank You!");
                        return;
                    default:
                        System.out.println("Invalid Choice!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // PART A: INSERT THREE ORDERS
    static void insertOrders(Connection con) throws SQLException {
        String sql = "INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) {
                System.out.println("\nEnter Order " + i + " Details");

                System.out.print("Order ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Customer Name: ");
                String customer = sc.nextLine();

                System.out.print("Product Name: ");
                String product = sc.nextLine();

                System.out.print("Quantity: ");
                int quantity = sc.nextInt();

                System.out.print("Unit Price: ");
                double price = sc.nextDouble();
                sc.nextLine();

                System.out.print("Status: ");
                String status = sc.nextLine();

                ps.setInt(1, id);
                ps.setString(2, customer);
                ps.setString(3, product);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);
                ps.setString(6, status);

                try {
                    ps.executeUpdate();
                    System.out.println("Order Inserted Successfully!");
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate Order ID! Try another ID.");