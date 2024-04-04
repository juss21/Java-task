//package com.playtech.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    public String transaction_id;
    public String user_id;
    public String type;
    public double amount;
    public String method;
    public String account_number;

    public Transaction(String[] dataArray) {
        this.transaction_id = dataArray[0];
        this.user_id = dataArray[1];
        this.type = dataArray[2];
        this.amount = Double.parseDouble(dataArray[3]);
        this.method = dataArray[4];
        this.account_number = dataArray[5];
    }

    public static List<Transaction> readTransactions(final Path filePath) {
        List<Transaction> data = new ArrayList<>();

        File file = filePath.toFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            int lineNumber = 1;
            String lineText;

            while ((lineText = reader.readLine()) != null) {
                lineNumber++;
                String[] dataArray = lineText.split(",");

                // 6 = length of Transaction class and expected output from the file,
                // could also be an argument variable if file reading is done with one function
                if (dataArray.length == 6) {
                    try {
                        data.add(new Transaction(dataArray));
                    } catch (NumberFormatException e) {
                        System.err.println("Error formatting data to number - file path: " + filePath + " at line " + lineNumber + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Invalid data at line " + lineNumber + " file path: " + filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<Event> processTransactions(final List<User> users, final List<Transaction> transactions,
            final List<BinMapping> binMappings) {
        
        List<Deposit> deposits = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        List<String> transaction_id_List = new ArrayList<>();

        for (Transaction transaction : transactions) {

            Event approved = new Event(transaction.transaction_id, Event.STATUS_APPROVED, "OK");
            Event declined = new Event(transaction.transaction_id, Event.STATUS_DECLINED, ""); 
            // declined.message: "" = STATUS OK

            if (!transaction.type.equals("DEPOSIT") && !transaction.type.equals("WITHDRAW")) {
                declined.message = "Invalid transaction type; got " + transaction.type;
                events.add(declined);
                continue;
            } else if (transaction.amount <= 0) {
                // Cannot be 0 because that would make the transaction useless.
                declined.message = "Transaction amount cannot be negative or 0; got: "
                        + User.formatDouble(transaction.amount);
                events.add(declined);
                continue;
            } else if (transaction_id_List.contains(transaction.transaction_id)) {
                declined.message = "Transaction " + transaction.transaction_id + " already processed (id non-unique)";
                events.add(declined);
                continue;
            }
            transaction_id_List.add(transaction.transaction_id);

            // Validate user legitimacy, Iban/card info and compare transaction amount to user balance/limitations
            declined.message = User.processUsers(transaction, users, binMappings, deposits);
            if (declined.message.equals("")) {
                
                // Add successful deposit to the list
                if (transaction.type.equals("DEPOSIT"))
                    deposits.add(new Deposit(transaction.account_number, transaction.user_id));

                // Calculates new user balance.
                double amount = transaction.type.equals("DEPOSIT") ? transaction.amount : -transaction.amount;
                users.forEach(user -> { 
                    if (user.user_id.equals(transaction.user_id)) 
                        user.balance += amount;
                });
                events.add(approved);
            } else
                events.add(declined);
        }
        return events;
    }
}
