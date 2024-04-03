import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class User {

    public String user_id;
    // private String username;
    public double balance;
    public String country;
    public boolean frozen;
    public double deposit_min;
    public double deposit_max;
    public double withdraw_min;
    public double withdraw_max;

    public User(String[] dataArray) {
        this.user_id = dataArray[0];
        // this.username = dataArray[1];
        this.balance = Double.parseDouble(dataArray[2]);
        this.country = dataArray[3];
        this.frozen = Integer.parseInt(dataArray[4]) == 1;
        this.deposit_min = Double.parseDouble(dataArray[5]);
        this.deposit_max = Double.parseDouble(dataArray[6]);
        this.withdraw_min = Double.parseDouble(dataArray[7]);
        this.withdraw_max = Double.parseDouble(dataArray[8]);
    }

    public static List<User> readUsers(final Path filePath) {
        List<User> data = new ArrayList<>();

        File file = filePath.toFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            int lineNumber = 1;
            String lineText;

            while ((lineText = reader.readLine()) != null) {
                lineNumber++;
                String[] dataArray = lineText.split(",");
                if (dataArray.length == 9) {
                    data.add(new User(dataArray));
                } else {
                    System.err.println("Invalid data at line " + lineNumber + " file path: " + filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String formatDouble(double value) {
        return String.format("%.2f", value);
    }

    private static String amountValidation(double max, double min, double amount, String transactionType,
            double balance) {
        if (amount < min || amount > max) {
            return "Amount " + formatDouble(amount) + " is "
                    + (amount < min ? "under the " + transactionType + " limit of " + formatDouble(min)
                            : "over the " + transactionType + " limit of " + formatDouble(max));
        }
        if (amount > balance && transactionType.equals("withdraw")) {
            return "Not enough balance to withdraw " + formatDouble(amount) + " - balance is too low at "
                    + formatDouble(balance);
        }
        return "";
    }

    public static String processUsers(Transaction transaction, final List<User> users,
            final List<BinMapping> binMappings, List<Deposit> deposits) {

        for (User user : users) {
            if (user.user_id.equals(transaction.user_id)) {
                if (user.frozen) {
                    return user.user_id + " - Account is frozen";
                }

                double max = transaction.type.equals("DEPOSIT") ? user.deposit_max : user.withdraw_max;
                double min = transaction.type.equals("DEPOSIT") ? user.deposit_min : user.withdraw_min;

                String message = amountValidation(max, min, transaction.amount, transaction.type.toLowerCase(), user.balance);
                if (!message.equals(""))
                    return message;

                message = Deposit.Validate(deposits, transaction);
                if (!message.equals(""))
                    return message;

                if (transaction.method.equals("TRANSFER"))
                    return Iban.Validate(transaction.account_number, user.country);

                return BinMapping.ValidateCard(transaction.account_number, binMappings, user);
            }
            if (users.getLast().equals(user)) {
                return "User " + transaction.user_id + " not found in Users";
            }
        }
        return "";
    }
}
