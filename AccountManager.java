

package com.BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private final Connection connection;
    private final Scanner in;

    public AccountManager(Connection connection, Scanner in) {
        this.connection = connection;
        this.in = in;
    }

    public void creditMoney(String accountNumber) {
        System.out.println("Enter amount:");
        double amount = in.nextDouble();
        in.nextLine(); // Consume the newline
        System.out.println("Enter security pin:");
        String securityPin = in.nextLine();

        try {
            connection.setAutoCommit(false);
             String Creditquery = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(Creditquery);
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(creditQuery);
                preparedStatement1.setDouble(1, amount);
                preparedStatement1.setString(2, accountNumber);
                int rowsAffected = preparedStatement1.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rs " + amount + " credited successfully");
                    connection.commit();
                } else {
                    System.out.println("Transaction failed");
                    connection.rollback();
                }
            } else {
                System.out.println("Invalid security pin");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException autoCommitEx) {
                System.out.println("Failed to set auto-commit: " + autoCommitEx.getMessage());
            }
        }
    }



    public void debitMoney(long account_number)  {
        System.out.print("Enter Amount: ");
        double amount = in.nextDouble();
        in.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = in.nextLine();
        try {
            connection.setAutoCommit(false);
            if(account_number!=0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs."+amount+" debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                        } else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Pin!");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void checkBalance(long accountNumber) {
        System.out.println("Enter security pin:");
        String securityPin = in.nextLine();
        String query = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    System.out.println("Your balance is: " + balance);
                } else {
                    System.out.println("Invalid account number or security pin.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking balance: " + e.getMessage());
        }
    }

    public void transferMoney(long sender_account_number) throws SQLException {
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = in.nextLong();
        System.out.print("Enter Amount: ");
        double amount = in.nextDouble();
        in.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = in.nextLine();
        try{
            connection.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ? ");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){

                        // Write debit and credit queries
                        String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        // Set Values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }else{
                System.out.println("Invalid account number");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        connection.setAutoCommit(true);
    }
}

