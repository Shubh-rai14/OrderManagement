import java.sql.*;
import java.util.Scanner;

public class Main {

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
				}
			}
		}
	}

	// PART B: SELECT ORDERS BY STATUS
	static void displayOrders(Connection con) throws SQLException {
		System.out.print("Enter Order Status: ");
		sc.nextLine();
		String status = sc.nextLine();

		String sql = "SELECT * FROM Orders "
				+ "WHERE status = ? AND quantity > 2 "
				+ "ORDER BY unit_price DESC";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status);

			try (ResultSet rs = ps.executeQuery()) {
				boolean found = false;

				while (rs.next()) {
					found = true;
					System.out.println("\nOrder ID: " + rs.getInt("order_id"));
					System.out.println("Customer: " + rs.getString("customer_name"));
					System.out.println("Product: " + rs.getString("product_name"));
					System.out.println("Quantity: " + rs.getInt("quantity"));
					System.out.println("Price: " + rs.getDouble("unit_price"));
					System.out.println("Status: " + rs.getString("status"));
				}

				if (!found) {
					System.out.println("No matching orders found.");
				}
			}
		}
	}

	// PART C: INCREASE QUANTITY
	static void increaseQuantity(Connection con) throws SQLException {
		System.out.print("Enter Order ID: ");
		int id = sc.nextInt();

		System.out.print("Enter Additional Quantity: ");
		int amount = sc.nextInt();

		if (amount <= 0) {
			System.out.println("Quantity must be positive.");
			return;
		}

		String sql = "UPDATE Orders SET quantity = quantity + ? "
				+ "WHERE order_id = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, amount);
			ps.setInt(2, id);

			int rows = ps.executeUpdate();
			if (rows > 0) {
				System.out.println("Quantity Updated Successfully!");
			} else {
				System.out.println("Order ID does not exist.");
			}
		}
	}

	// PART D: UPDATE ORDER STATUS
	static void updateStatus(Connection con) throws SQLException {
		System.out.print("Enter Order ID: ");
		int id = sc.nextInt();
		sc.nextLine();

		System.out.print("Enter New Status: ");
		String status = sc.nextLine();

		if (!(status.equalsIgnoreCase("Pending")
				|| status.equalsIgnoreCase("Shipped")
				|| status.equalsIgnoreCase("Delivered"))) {
			System.out.println("Invalid Status!");
			System.out.println("Use Pending, Shipped or Delivered.");
			return;
		}

		String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, id);

			int rows = ps.executeUpdate();
			if (rows > 0) {
				System.out.println("Status Updated Successfully!");

				String query = "SELECT * FROM Orders WHERE order_id = ?";
				try (PreparedStatement p = con.prepareStatement(query)) {
					p.setInt(1, id);

					try (ResultSet rs = p.executeQuery()) {
						if (rs.next()) {
							System.out.println("Updated Order Details:");
							System.out.println("Customer: " + rs.getString("customer_name"));
							System.out.println("Product: " + rs.getString("product_name"));
							System.out.println("Quantity: " + rs.getInt("quantity"));
							System.out.println("Status: " + rs.getString("status"));
						}
					}
				}
			} else {
				System.out.println("Order ID does not exist.");
			}
		}
	}

	// PART E: TOTAL VALUE AND DELETE CANCELLED ORDER
	static void totalAndDelete(Connection con) throws SQLException {
		String sql = "SELECT SUM(quantity * unit_price) AS total FROM Orders";

		try (PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				System.out.println("Total Order Value: " + rs.getDouble("total"));
			}
		}

		System.out.print("Enter Order ID to delete: ");
		int id = sc.nextInt();

		String check = "SELECT status FROM Orders WHERE order_id = ?";
		try (PreparedStatement ps = con.prepareStatement(check)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					System.out.println("Order does not exist.");
					return;
				}

				String status = rs.getString("status");
				if (!status.equalsIgnoreCase("Cancelled")) {
					System.out.println("Order cannot be deleted. Status is not Cancelled.");
					return;
				}
			}
		}

		String delete = "DELETE FROM Orders WHERE order_id = ?";
		try (PreparedStatement ps = con.prepareStatement(delete)) {
			ps.setInt(1, id);

			int rows = ps.executeUpdate();
			if (rows > 0) {
				System.out.println("Cancelled Order Deleted Successfully!");
			}
		}
	}
}
