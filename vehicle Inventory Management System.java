import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class Vehicle {
    private String make;
    private String model;
    private int year;
    private double price;

    public Vehicle(String make, String model, int year, double price) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return year + " " + make + " " + model + " - $" + price;
    }
}

class InventoryManager {
    private Connection connection;

    public InventoryManager() {
        try {
            // Create a connection to the SQLite database (you need to adjust the URL based on your database location)
            connection = DriverManager.getConnection("jdbc:sqlite:/path/to/your/database.db");

            // Create a table for the vehicles if it doesn't exist
            String createTableQuery = "CREATE TABLE IF NOT EXISTS vehicles (make TEXT, model TEXT, year INTEGER, price REAL)";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVehicle(Vehicle vehicle) {
        try {
            // Insert the vehicle into the database
            String insertQuery = "INSERT INTO vehicles (make, model, year, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, vehicle.getMake());
                preparedStatement.setString(2, vehicle.getModel());
                preparedStatement.setInt(3, vehicle.getYear());
                preparedStatement.setDouble(4, vehicle.getPrice());
                preparedStatement.executeUpdate();
            }

            System.out.println("Vehicle added to inventory.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayInventory() {
        try {
            // Retrieve and display vehicles from the database
            String selectQuery = "SELECT * FROM vehicles";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {

                System.out.println("Inventory:");
                while (resultSet.next()) {
                    String make = resultSet.getString("make");
                    String model = resultSet.getString("model");
                    int year = resultSet.getInt("year");
                    double price = resultSet.getDouble("price");

                    Vehicle vehicle = new Vehicle(make, model, year, price);
                    System.out.println(vehicle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeVehicle(String make, String model) {
        try {
            // Delete the vehicle from the database
            String deleteQuery = "DELETE FROM vehicles WHERE make = ? AND model = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, make);
                preparedStatement.setString(2, model);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Vehicle removed from inventory.");
                } else {
                    System.out.println("Vehicle not found in inventory.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            // Close the database connection
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class VehicleInventorySystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryManager inventoryManager = new InventoryManager();

        while (true) {
            System.out.println("\n1. Add Vehicle");
            System.out.println("2. Display Inventory");
            System.out.println("3. Remove Vehicle");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();
                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();

                    Vehicle newVehicle = new Vehicle(make, model, year, price);
                    inventoryManager.addVehicle(newVehicle);
                    break;

                case 2:
                    inventoryManager.displayInventory();
                    break;

                case 3:
                    System.out.print("Enter make of the vehicle to remove: ");
                    String makeToRemove = scanner.nextLine();
                    System.out.print("Enter model of the vehicle to remove: ");
                    String modelToRemove = scanner.nextLine();

                    inventoryManager.removeVehicle(makeToRemove, modelToRemove);
                    break;

                case 4:
                    System.out.println("Exiting the program.");
                    inventoryManager.closeConnection(); // Close the database connection before exiting
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
