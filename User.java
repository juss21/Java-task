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

    private static String amountValidation(double max, double min, double amount, String transactionType, double balance) {
        if (amount < min || amount > max) {
            return "Amount " + amount + " is " + (amount < min ? "under the " + transactionType + " limit of " + min
                    : "over the " + transactionType + " limit of " + max);
        }
        if (amount > balance && transactionType.equals("withdraw")) {
            return "Not enough balance to withdraw " + amount + " - balance is too low at " + balance;
        }
        return "";
    }

    public static List<Event> processUsers(Transaction transaction, final List<Event> events, final List<User> users) {
        Event approved = new Event(transaction.transaction_id, Event.STATUS_APPROVED, "OK");
        Event declined = new Event(transaction.transaction_id, Event.STATUS_DECLINED, "");

        for (User user : users) {
            if (user.user_id.equals(transaction.user_id)) {
                if (user.frozen) {
                    declined.message = user.user_id + " - Account is frozen";
                    events.add(declined);
                    break;
                }

                double max = transaction.type.equals("DEPOSIT") ? user.deposit_max : user.withdraw_max;
                double min = transaction.type.equals("DEPOSIT") ? user.deposit_min : user.withdraw_min;

                declined.message = amountValidation(max, min, transaction.amount, transaction.type.toLowerCase(),
                        user.balance);
                if (!declined.message.equals("")) {
                    events.add(declined);
                    break;
                }

                if (transaction.method.equals("TRANSFER")) {
                    String ibanCountry = transaction.account_number.substring(0, 2);
                    if (!user.country.equals(ibanCountry)) {
                        declined.message = "Invalid account country " + ibanCountry + "; expected " + user.country;
                        events.add(declined);
                        break;
                    }
                }
                
                events.add(approved);
                break;
            }
            if (users.get(users.size() - 1) == user) {
                declined.message = transaction.user_id + " - Account doesn't exist";
                events.add(declined);
            }
        }
        return events;
    }
}
