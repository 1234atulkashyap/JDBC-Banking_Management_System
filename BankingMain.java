
package com.BankingManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingMain {
    public static final String url = "jdbc:mysql://localhost:3306/banking_system";
    public static final String username = "root";
    public static final String password = ""; // Enter your password

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Scanner in = new Scanner(System.in)) {
            User user = new User(connection, in);
            Accounts accounts = new Accounts(connection, in);
            AccountManager accountManager = new AccountManager(connection, in);

            String email;
            String accountNumber;

            while (true) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter your choice:");
                int choice = in.nextInt();
                in.nextLine(); // Consume the newline

                // Add break to prevent fall-through
                switch (choice) {
                    case 1 -> user.register();
                    case 2 -> {
                        email = user.login();
                        if (email != null) {
                            System.out.println("User logged in");
                            if (!accounts.accountExist(email)) {
                                System.out.println("1. Open a new bank account");
                                System.out.println("2. Exit");
                                if (in.nextInt() == 1) {
                                    accountNumber = accounts.openAccount(email);
                                    System.out.println("Account opened successfully");
                                    System.out.println("Your account number is: " + accountNumber);
                                } else {
                                    break;
                                }
                            }
                            accountNumber = accounts.getAccountNumber(email);
                            int choice2 = 0;
                            while (choice2 != 5) {
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Check Balance");
                                System.out.println("4. Transfer Money");
                                System.out.println("5. Log Out");
                                System.out.println("Enter your choice: ");
                                choice2 = in.nextInt();
                                in.nextLine(); // Consume the newline

                                switch (choice2) {
                                    case 1:
                                        accountManager.debitMoney(Long.parseLong(accountNumber));
                                        break;
                                    case 2:
                                        accountManager.creditMoney(accountNumber);
                                        break;
                                    case 3:
                                        accountManager.checkBalance(Long.parseLong(accountNumber));
                                        break;
                                    case 4:
                                        accountManager.transferMoney(Long.parseLong(accountNumber));
                                        break;
                                    case 5:
                                        System.out.println("Logged out");
                                        break;
                                    default:
                                        System.out.println("Invalid choice, try again");
                                        break;
                                }
                            }
                        } else {
                            System.out.println("Incorrect Email or Password");
                        }
                    }
                    case 3 -> {
                        System.out.println("Thank you for using Banking System");
                        System.out.println("Exiting System");
                        return;
                    }
                    default -> System.out.println("Invalid choice, try again");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
}

