//package com.playtech.assignment;

import java.util.List;

public class Deposit {
    public String account_number;
    public String account_owner;

    public Deposit(String number, String owner) {
        this.account_number = number;
        this.account_owner = owner;
    }

    public static String Validate(final List<Deposit> deposits, Transaction transaction) {
        for (Deposit deposit : deposits) {
            if (deposit.account_number.equals(transaction.account_number) &&
                    deposit.account_owner.equals(transaction.user_id))
                return "";

            if (deposit.equals(deposits.getLast()) && !deposit.account_number.equals(transaction.account_number)
                    && transaction.type.equals("WITHDRAW"))
                return "Cannot withdraw with a new account " + transaction.account_number;

            if (deposit.account_number.equals(transaction.account_number)
                    && !deposit.account_owner.equals(transaction.user_id))
                return "Account " + transaction.account_number + " is in use by other user";
        }
        return "";
    }
}
