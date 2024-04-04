//package com.playtech.assignment;

public class Iban {
    private static final long modulo = 97;
    private static final long maxDigits = 999999999;
    private static final int ibanMinSize = 15;
    private static final int ibanMaxSize = 34;

    public static String Validate(String iban, String userCountry) {
        String ibanCountry = iban.substring(0, 2);
        if (!userCountry.equals(ibanCountry))
            return "Invalid account country " + ibanCountry + "; expected " + userCountry;

        // Iban cannot be samaller than 15 and bigger than 34 digits
        if (iban.length() < ibanMinSize || iban.length() > ibanMaxSize)
            return "Invalid iban " + iban;

        String reformattedIban = iban.substring(4) + iban.substring(0, 4);

        long total = 0;
        for (int i = 0; i < reformattedIban.length(); i++) {
            int charValue = Character.digit(reformattedIban.charAt(i), 36);

            // Iban has invalid characters in it
            if (charValue < 0 || charValue > 35)
                return "Invalid iban " + iban;

            total = (charValue > 9 ? total * 100 : total * 10) + charValue;
            if (total > maxDigits)
                total = total % modulo;
        }

        return (int) (total % modulo) == 1 ? "" : "Invalid iban " + iban;
    }
}
