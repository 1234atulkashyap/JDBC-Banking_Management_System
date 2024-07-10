
package com.BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private final Connection connection;
    private final Scanner in;

    public User(Connection connection, Scanner in) {
        this.connection = connection;
        this.in = in;
    }

    public void register() {
        in.nextLine(); // Consume newline character left by previous input
        System.out.println("Enter full name:");
        String full_name = in.nextLine();
        System.out.println("Enter Email:");
        String email = in.nextLine();
        System.out.println("Enter password:");
        String password = in.nextLine();

        if (userExist(email)) {
            System.out.println("Email already exists");
        } else {
            String registerQuery = "INSERT INTO user (full_name, email, password) VALUES (?, ?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
                preparedStatement.setString(1, full_name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Registered Successfully");
                } else {
                    System.out.println("Registration Failed");
                }
            } catch (SQLException e) {
                System.out.println("Error during registration: " + e.getMessage());
            }
        }
    }

    private boolean userExist(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    public String login() {
        in.nextLine(); // Consume newline character left by previous input
        System.out.println("Enter your email:");
        String email = in.nextLine();
        System.out.println("Enter your password:");
        String password = in.nextLine();
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return email; // Return the email if login successful
            } else {
                System.out.println("Incorrect email or password");
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return null;
    }
}

