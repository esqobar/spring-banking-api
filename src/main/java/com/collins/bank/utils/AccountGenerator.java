package com.collins.bank.utils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Random;

public class AccountGenerator {

    public static String generateAccountNumber() {
        // Get the current year
        Year currentYear = Year.now();

        // Define the range for the random 6-digit number
        int min = 100000;
        int max = 999999;

        // Generate a random number between min and max (inclusive)
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1)) + min;

        // Convert the year and random number to strings, then concatenate them
        String year = String.valueOf(currentYear.getValue());
        String randomNumber = String.valueOf(randNumber);

        // Use StringBuilder for efficient string concatenation
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }

}
