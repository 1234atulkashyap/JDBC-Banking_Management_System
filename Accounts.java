

package com.BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private final Connection connection;
    private final Scanner in;

    public Accounts(Connection connection, Scanner in) {
        this.connection = connection;
        this.in = in;
    }

    public boolean accountExist(String email) {
        String query = "SELECT account_number FROM accounts WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking account existence: " + e.getMessage());
        }
        return false;
    }

    public String getAccountNumber(String email) {
        String query = "SELECT account_number FROM accounts WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("account_number");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account number: " + e.getMessage());
        }
        throw new RuntimeException("Account does not exist");
    }

    public String openAccount(String email) {
        if (!accountExist(email)) {
            String query = "INSERT INTO accounts (account_number, full_name, email, balance, security_pin) VALUES (?, ?, ?, ?, ?)";
            in.nextLine(); // Consume the newline character left by previous input
            System.out.println("Enter full name:");
            String name = in.nextLine();
            System.out.println("Enter amount you want to credit:");
            double amount = in.nextDouble();
            in.nextLine(); // Consume the newline character left by nextDouble()
            System.out.println("Enter security pin:");
            String securityPin = in.nextLine();

            try {
                long accountNumber = generateAccountNumber();
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setLong(1, accountNumber);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, email);
                    preparedStatement.setDouble(4, amount);
                    preparedStatement.setString(5, securityPin);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Account successfully created");
                        return String.valueOf(accountNumber);
                    } else {
                        System.out.println("Account creation failed");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error creating account: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Account already exists");
        }
        return null;
    }

    private long generateAccountNumber() {
        String query = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong("account_number") + 1;
            } else {
                return 100000100;
            }
        } catch (SQLException e) {
            System.out.println("Error generating account number: " + e.getMessage());
        }
        return 100000100;
    }
}

