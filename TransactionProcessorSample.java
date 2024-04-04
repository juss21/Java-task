//package com.playtech.assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TransactionProcessorSample {
    public static void main(final String[] args) throws IOException {

        /*
         * I wanted to read all the files using one function,
         * because they're basically using the same code.
         * But I couldn't find a way to utilize or have an
         * unknown type variable as an argument in the function.
         */

        List<User> users = User.readUsers(Paths.get(args[0]));
        List<Transaction> transactions = Transaction.readTransactions(Paths.get(args[1]));
        List<BinMapping> binMappings = BinMapping.readBinMappings(Paths.get(args[2]));

        List<Event> events = Transaction.processTransactions(users, transactions, binMappings);

        TransactionProcessorSample.writeBalances(Paths.get(args[3]), users);
        TransactionProcessorSample.writeEvents(Paths.get(args[4]), events);
    }

    private static void createDir(final Path filePath) throws IOException {
        Path directoryPath = filePath.getParent();
        if (directoryPath != null && !Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }

    private static void writeBalances(final Path filePath, final List<User> users) throws IOException {
        createDir(filePath);
        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("USER_ID,BALANCE\n");
            for (User user : users) {
                writer.append(user.user_id).append(",").append(User.formatDouble(user.balance));
                if (!user.equals(users.getLast()))
                    writer.append("\n");
            }
        }
    }

    private static void writeEvents(final Path filePath, final List<Event> events) throws IOException {
        createDir(filePath);
        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("TRANSACTION_ID,STATUS,MESSAGE\n");
            for (final var event : events) {
                writer.append(event.transaction_id).append(",").append(event.status).append(",").append(event.message);
                if (!event.equals(events.getLast()))
                    writer.append("\n");
            }
        }
    }
}
