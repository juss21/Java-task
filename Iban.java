public class Iban {
    private static final long modulo = 97;
    private static final long maxDigits = 999999999;
    private static final int ibanMinSize = 15;
    private static final int ibanMaxSize = 34;

    public static boolean Validate(String iban) {
        if (iban.length() < ibanMinSize || iban.length() > ibanMaxSize) return false;

        String reformattedIban = iban.substring(4) + iban.substring(0, 4);

        long total = 0;
        for (int i = 0; i < reformattedIban.length(); i++) {
            int charValue = Character.digit(reformattedIban.charAt(i), 36);

            if (charValue < 0 || charValue > 35) return false;

            total = (charValue > 9 ? total * 100 : total * 10) + charValue;
            if (total > maxDigits) {
                total = total % modulo;
              }
        }

        return (int)(total % modulo) == 1;
    }
}
