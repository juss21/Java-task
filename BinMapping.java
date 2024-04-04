//package com.playtech.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BinMapping {
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

    public static List<BinMapping> readBinMappings(final Path filePath) {
        List<BinMapping> data = new ArrayList<>();

        File file = filePath.toFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            int lineNumber = 1;
            String lineText;

            while ((lineText = reader.readLine()) != null) {
                lineNumber++;
                String[] dataArray = lineText.split(",");
                if (dataArray.length == 5) {
                    data.add(new BinMapping(dataArray));
                } else {
                    System.err.println("Invalid data at line " + lineNumber + " file path: " + filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String ValidateCard(String cardNumber, List<BinMapping> bins, User user) {
        Long firstTenDigits = Long.parseLong(cardNumber.substring(0, 10));

        for (BinMapping bin : bins) {
            if (firstTenDigits >= bin.range_from && firstTenDigits <= bin.range_to) {

                String userCountry = Locale.of("en", user.country).getISO3Country();
                if (!userCountry.equals(bin.country))
                    return "Invalid country " + bin.country + "; expected " + user.country + " (" + userCountry + ")";

                return bin.type.equals("DC") ? "" : "Only DC cards allowed; got " + bin.type;
            }
        }
        return "Invalid card info " + cardNumber;
    }
}
