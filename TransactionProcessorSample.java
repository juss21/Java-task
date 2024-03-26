//package com.playtech.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// This template shows input parameters format.
// It is otherwise not mandatory to use, you can write everything from scratch if you wish.
public class TransactionProcessorSample {
    public static void main(final String[] args) throws IOException {
        List<User> users = TransactionProcessorSample.readFile(Paths.get(args[0]), User::new, 9);
        List<Transaction> transactions = TransactionProcessorSample.readFile(Paths.get(args[1]), Transaction::new, 6);
        List<BinMapping> binMappings = TransactionProcessorSample.readFile(Paths.get(args[2]), BinMapping::new, 5);

        List<Event> events = TransactionProcessorSample.processTransactions(users, transactions, binMappings);

        //TransactionProcessorSample.writeBalances(Paths.get(args[3]), users);
        //TransactionProcessorSample.writeEvents(Paths.get(args[4]), events);
    }

    private static <T>List<T> readFile(final Path filePath, Function<String[], T> constructor, int classLength) {
        List<T> data = new ArrayList<>();

        File file = filePath.toFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            int lineNumber = 1;
            String lineText;

            while ((lineText = reader.readLine()) != null) {
                lineNumber++;
                String[] dataArray = lineText.split(",");
                if (dataArray.length == classLength) {
                    data.add(constructor.apply(dataArray));
                } else {
                    System.err.println("Invalid data at line " + lineNumber + ": " + lineText);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static List<Event> processTransactions(final List<User> users, final List<Transaction> transactions,
            final List<BinMapping> binMappings) {
        // ToDo Implementation
        return null;
    }

    private static void writeBalances(final Path filePath, final List<User> users) {
        // ToDo Implementation
    }

    private static void writeEvents(final Path filePath, final List<Event> events) throws IOException {
        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("transaction_id,status,message\n");
            for (final var event : events) {
                writer.append(event.transaction_id).append(",").append(event.status).append(",").append(event.message)
                        .append("\n");
            }
        }
    }
}

class User {
    public String user_id;
    //private String username;
    public double balance;
    public String country;
    public boolean frozen;
    public double deposit_min;
    public double deposit_max;
    public double withdraw_min;
    public double withdraw_max;
     
    public User(String[] dataArray) {
        this.user_id = dataArray[0];
        //this.username = dataArray[1];
        this.balance = Double.parseDouble(dataArray[2]);
        this.country = dataArray[3];
        this.frozen = Integer.parseInt(dataArray[4]) == 1;
        this.deposit_min = Double.parseDouble(dataArray[5]);
        this.deposit_max = Double.parseDouble(dataArray[6]);
        this.withdraw_min = Double.parseDouble(dataArray[7]);
        this.withdraw_max = Double.parseDouble(dataArray[8]);
    }
}

class Transaction {

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
}

class BinMapping {
    public String name;
    public Long range_from;
    public Long range_to;
    public String type;
    public String country;

    public BinMapping(String[] dataArray) {
        this.name = dataArray[0];
        this.range_from = Long.parseLong(dataArray[1]);
        this.range_to = Long.parseLong(dataArray[2]);
        this.type = dataArray[3];
        this.country = dataArray[4];
    }
}

class Event {
    public static final String STATUS_DECLINED = "DECLINED";
    public static final String STATUS_APPROVED = "APPROVED";

    public String transaction_id;
    public String status;
    public String message;
}
