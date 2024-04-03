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

                if (dataArray.length == 6) {
                    data.add(new Transaction(dataArray));
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

        List<Event> events = new ArrayList<>();
        List<String> transaction_id_List = new ArrayList<>();
        List<Deposit> deposits = new ArrayList<>();

        for (Transaction transaction : transactions) {
            Event approved = new Event(transaction.transaction_id, Event.STATUS_APPROVED, "OK");
            Event declined = new Event(transaction.transaction_id, Event.STATUS_DECLINED, "");

            if (transaction_id_List.contains(transaction.transaction_id)) {
                declined.message = "Transaction " + transaction.transaction_id + " already processed (id non-unique)";
                events.add(declined);
                continue;
            }
            transaction_id_List.add(transaction.transaction_id);

            if (transaction.amount < 0) {
                declined.message = "transaction amount cannot be negative; got: " + transaction.amount;
                events.add(declined);
                continue;
            }

            if (transaction.method.equals("TRANSFER")) {
                if (!Iban.Validate(transaction.account_number)) {
                    declined.message = "Invalid iban " + transaction.account_number;
                    events.add(declined);
                    continue;
                }
            } else if (transaction.type.equals("DEPOSIT")) {
                declined.message = BinMapping.ValidateCard(transaction.account_number, binMappings);
                if (!declined.message.equals("")) {
                    events.add(declined);
                    continue;
                }
            }
            events = User.processUsers(transaction, events, users);

            // See katki 
            if (!deposits.contains(new Deposit(transaction.account_number, transaction.user_id))) {
                if (transaction.type.equals("WITHDRAW")) {
                declined.message = "Cannot withdraw with a new account " + transaction.account_number;
                events.add(declined);
                continue;
                } else if (deposits.contains(transaction.account_number)) {
                    declined.message = "Account has already been used by another user.";
                    events.add(declined);
                    continue;
                }
            }
            // See ka
            if (transaction.type.equals("DEPOSIT") && events.getLast().message.equals("OK")) {
                deposits.add(new Deposit(transaction.account_number, transaction.user_id));
                System.out.println("Added deposit: " + deposits.getLast().account_number);
            }
        }
        return events;
    }
}
