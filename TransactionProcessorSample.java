//package com.playtech.assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
 * This is my first ever java project. I didn't use any AI, to showcase what I can do. 
 * As I'm new to java, I know there are probably a lot of ways to improve the code.
 * I'm applying for an internship program, so hopefully I'll learn more java with you guys!
 */

public class TransactionProcessorSample {
    public static void main(final String[] args) throws IOException {

        /*
         * I wanted to read all the files using one function, because they're basically using the same code.
         * But due to time restrictions, I couldn't find a way to utilize or have an
         * unknown type variable as an argument in the function.
         */
        List<User> users = User.readUsers(Paths.get(args[0]));
        List<Transaction> transactions = Transaction.readTransactions(Paths.get(args[1]));
        List<BinMapping> binMappings = BinMapping.readBinMappings(Paths.get(args[2]));

        List<Event> events = Transaction.processTransactions(users, transactions, binMappings);

        TransactionProcessorSample.writeBalances(events);
        // TransactionProcessorSample.writeEvents(Paths.get(args[4]), events);
    }

    private static void writeBalances(final List<Event> events) {
        for (Event event : events) {
            System.out.println("transaction ID: " + event.transaction_id + " Status: " + event.status + " Message: "
                    + event.message);
        }
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

