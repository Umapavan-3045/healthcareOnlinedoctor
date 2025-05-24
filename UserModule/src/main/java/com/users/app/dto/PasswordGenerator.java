package com.users.app.dto;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String ALPHANUMERIC_CHARS = UPPERCASE_CHARS + LOWERCASE_CHARS;
    private static final Random random = new Random();

    public String generateRandomPassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be a positive integer.");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one uppercase and one lowercase character
        password.append(UPPERCASE_CHARS.charAt(random.nextInt(UPPERCASE_CHARS.length())));
        password.append(LOWERCASE_CHARS.charAt(random.nextInt(LOWERCASE_CHARS.length())));

        // Fill the remaining length with random alphanumeric characters
        for (int i = 2; i < length; i++) {
            password.append(ALPHANUMERIC_CHARS.charAt(random.nextInt(ALPHANUMERIC_CHARS.length())));
        }

        // Shuffle the password to mix the uppercase and lowercase characters
        for (int i = password.length() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }
}
