package com.mask.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingUtils {

    // Enhanced regex patterns for better detection
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+\\d{1,3}[- ]?)?\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$|^\\d{10}$"
    );
    
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
        "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3[0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})$"
    );
    
    private static final Pattern AADHAAR_PATTERN = Pattern.compile(
        "^[0-9]{4}\\s?[0-9]{4}\\s?[0-9]{4}$"
    );
    
    private static final Pattern PAN_PATTERN = Pattern.compile(
        "^[A-Z]{5}[0-9]{4}[A-Z]{1}$"
    );
    
    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    private static final Pattern DATE_ISO_PATTERN = Pattern.compile(
        "^\\d{4}-\\d{2}-\\d{2}$"
    );
    
    private static final Pattern DATE_SLASH_PATTERN = Pattern.compile(
        "^\\d{2}/\\d{2}/\\d{4}$"
    );
    
    private static final Pattern DATE_DASH_PATTERN = Pattern.compile(
        "^\\d{2}-\\d{2}-\\d{4}$"
    );

    private static final Random RANDOM = new Random();

    // Enhanced fake data arrays
    private static final String[] FAKE_FIRST_NAMES = {
        "Amit", "Priya", "Rohit", "Neha", "Rajesh", "Sunita", "Vikram", "Kavya",
        "Arjun", "Divya", "Sanjay", "Meera", "Karthik", "Pooja", "Ravi", "Shreya"
    };
    
    private static final String[] FAKE_LAST_NAMES = {
        "Sharma", "Patel", "Singh", "Kumar", "Gupta", "Verma", "Agarwal", "Jain",
        "Shah", "Reddy", "Nair", "Iyer", "Rao", "Pandey", "Mishra", "Tiwari"
    };
    
    private static final String[] FAKE_EMAIL_DOMAINS = {
        "example.com", "sample.org", "demo.net", "test.com", "mock.in", "fake.co"
    };

    // Sensitive column keywords
    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
        "name", "email", "phone", "mobile", "address", "pan", "aadhaar", "aadhar",
        "card", "ssn", "dob", "birth", "password", "social", "tax", "account",
        "bank", "credit", "debit", "license", "id", "number", "salary", "income"
    );

    /**
     * Full masking - replaces all characters with asterisks
     */
    public static String fullMask(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        return "*".repeat(input.length());
    }

    /**
     * Partial masking - shows beginning and end, masks middle
     */
    public static String partialMask(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String trimmedInput = input.trim();
        
        // Special handling for email
        if (EMAIL_PATTERN.matcher(trimmedInput).matches()) {
            int atIndex = trimmedInput.indexOf("@");
            if (atIndex > 0) {
                String localPart = trimmedInput.substring(0, atIndex);
                String domain = trimmedInput.substring(atIndex);
                
                if (localPart.length() <= 2) {
                    return "*".repeat(localPart.length()) + domain;
                } else {
                    return localPart.charAt(0) + "*".repeat(localPart.length() - 2) + 
                           localPart.charAt(localPart.length() - 1) + domain;
                }
            }
        }
        
        // Special handling for phone numbers
        if (PHONE_PATTERN.matcher(trimmedInput.replaceAll("[^\\d]", "")).matches()) {
            String digitsOnly = trimmedInput.replaceAll("[^\\d]", "");
            if (digitsOnly.length() >= 10) {
                return "*".repeat(digitsOnly.length() - 4) + digitsOnly.substring(digitsOnly.length() - 4);
            }
        }
        
        // General partial masking
        int length = trimmedInput.length();
        if (length <= 3) {
            return "*".repeat(length);
        } else if (length <= 6) {
            return trimmedInput.charAt(0) + "*".repeat(length - 2) + trimmedInput.charAt(length - 1);
        } else {
            int showChars = Math.max(1, length / 4);
            String start = trimmedInput.substring(0, showChars);
            String end = trimmedInput.substring(length - showChars);
            String middle = "*".repeat(length - (2 * showChars));
            return start + middle + end;
        }
    }

    /**
     * Random replacement with contextual fake data
     */
    public static String randomReplace(String original, String columnHint) {
        if (original == null || original.trim().isEmpty()) {
            return original;
        }

        String trimmedOriginal = original.trim();
        String lowerHint = columnHint != null ? columnHint.toLowerCase() : "";
        
        // Email replacement
        if (EMAIL_PATTERN.matcher(trimmedOriginal).matches() || lowerHint.contains("email")) {
            String firstName = FAKE_FIRST_NAMES[RANDOM.nextInt(FAKE_FIRST_NAMES.length)].toLowerCase();
            String lastName = FAKE_LAST_NAMES[RANDOM.nextInt(FAKE_LAST_NAMES.length)].toLowerCase();
            String domain = FAKE_EMAIL_DOMAINS[RANDOM.nextInt(FAKE_EMAIL_DOMAINS.length)];
            return firstName + "." + lastName + "@" + domain;
        }
        
        // Phone number replacement
        if (PHONE_PATTERN.matcher(trimmedOriginal.replaceAll("[^\\d]", "")).matches() || 
            lowerHint.contains("phone") || lowerHint.contains("mobile")) {
            StringBuilder phone = new StringBuilder();
            // Indian mobile numbers typically start with 6, 7, 8, or 9
            phone.append(6 + RANDOM.nextInt(4));
            for (int i = 1; i < 10; i++) {
                phone.append(RANDOM.nextInt(10));
            }
            return phone.toString();
        }
        
        // Name replacement
        if (lowerHint.contains("name")) {
            if (lowerHint.contains("first") || lowerHint.contains("fname")) {
                return FAKE_FIRST_NAMES[RANDOM.nextInt(FAKE_FIRST_NAMES.length)];
            } else if (lowerHint.contains("last") || lowerHint.contains("surname") || lowerHint.contains("lname")) {
                return FAKE_LAST_NAMES[RANDOM.nextInt(FAKE_LAST_NAMES.length)];
            } else {
                return FAKE_FIRST_NAMES[RANDOM.nextInt(FAKE_FIRST_NAMES.length)] + " " + 
                       FAKE_LAST_NAMES[RANDOM.nextInt(FAKE_LAST_NAMES.length)];
            }
        }
        
        // Date replacement
        if (isDateValue(trimmedOriginal) || lowerHint.contains("date") || 
            lowerHint.contains("dob") || lowerHint.contains("birth")) {
            return generateRandomDate(trimmedOriginal);
        }
        
        // Credit card replacement
        if (CREDIT_CARD_PATTERN.matcher(trimmedOriginal.replaceAll("[^\\d]", "")).matches() || 
            lowerHint.contains("card")) {
            return generateFakeCreditCard();
        }
        
        // PAN replacement
        if (PAN_PATTERN.matcher(trimmedOriginal).matches() || lowerHint.contains("pan")) {
            return generateFakePAN();
        }
        
        // Aadhaar replacement
        if (AADHAAR_PATTERN.matcher(trimmedOriginal.replaceAll("\\s", "")).matches() || 
            lowerHint.contains("aadhaar") || lowerHint.contains("aadhar")) {
            return generateFakeAadhaar();
        }
        
        // Generic text replacement - shuffle characters but maintain structure
        return shuffleString(trimmedOriginal);
    }

    /**
     * Hash masking using SHA-256
     */
    public static String hashMask(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return "HASH_" + hexString.toString().substring(0, 8).toUpperCase();
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }

    /**
     * Date shifting - maintains format but shifts date by random days
     */
    public static String dateShift(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String trimmedInput = input.trim();
        
        try {
            LocalDate originalDate = null;
            DateTimeFormatter formatter = null;
            
            // Try different date formats
            if (DATE_ISO_PATTERN.matcher(trimmedInput).matches()) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                originalDate = LocalDate.parse(trimmedInput, formatter);
            } else if (DATE_SLASH_PATTERN.matcher(trimmedInput).matches()) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                originalDate = LocalDate.parse(trimmedInput, formatter);
            } else if (DATE_DASH_PATTERN.matcher(trimmedInput).matches()) {
                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                originalDate = LocalDate.parse(trimmedInput, formatter);
            }
            
            if (originalDate != null && formatter != null) {
                // Shift by ±180 days to maintain seasonal context
                int shiftDays = RANDOM.nextInt(361) - 180;
                LocalDate shiftedDate = originalDate.plusDays(shiftDays);
                return shiftedDate.format(formatter);
            }
        } catch (DateTimeParseException e) {
            // If parsing fails, return original
        }
        
        return trimmedInput;
    }

    /**
     * Main technique application method
     */
    public static String applyTechnique(String technique, String original, String columnHint) {
        if (original == null) {
            return null;
        }

        if (technique == null || technique.trim().isEmpty()) {
            return original;
        }

        switch (technique.toUpperCase().trim()) {
            case "FULL_MASK":
                return fullMask(original);
            case "PARTIAL_MASK":
                return partialMask(original);
            case "RANDOM_REPLACE":
                return randomReplace(original, columnHint);
            case "HASH_MASK":
                return hashMask(original);
            case "DATE_SHIFT":
                return dateShift(original);
            default:
                // Default to full mask for unknown techniques
                return fullMask(original);
        }
    }

    /**
     * Detect data type by analyzing the value
     */
    public static Optional<String> detectTypeByValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedValue = value.trim();
        
        // Email detection
        if (EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            return Optional.of("email");
        }
        
        // Phone detection
        String digitsOnly = trimmedValue.replaceAll("[^\\d]", "");
        if (PHONE_PATTERN.matcher(digitsOnly).matches() && digitsOnly.length() >= 10) {
            return Optional.of("phone");
        }
        
        // Credit card detection
        if (CREDIT_CARD_PATTERN.matcher(digitsOnly).matches()) {
            return Optional.of("card");
        }
        
        // Aadhaar detection
        if (AADHAAR_PATTERN.matcher(digitsOnly).matches() && digitsOnly.length() == 12) {
            return Optional.of("aadhaar");
        }
        
        // PAN detection
        if (PAN_PATTERN.matcher(trimmedValue.toUpperCase()).matches()) {
            return Optional.of("pan");
        }
        
        // IP address detection
        if (IPV4_PATTERN.matcher(trimmedValue).matches()) {
            return Optional.of("ip");
        }
        
        // Date detection
        if (isDateValue(trimmedValue)) {
            return Optional.of("date");
        }
        
        return Optional.empty();
    }

    /**
     * Check if column header indicates sensitive data
     */
    public static boolean headerIsSensitive(String header) {
        if (header == null || header.trim().isEmpty()) {
            return false;
        }

        String lowerHeader = header.toLowerCase().trim();
        
        return SENSITIVE_KEYWORDS.stream().anyMatch(lowerHeader::contains);
    }

    /**
     * Helper method to check if a string represents a date
     */
    private static boolean isDateValue(String value) {
        return DATE_ISO_PATTERN.matcher(value).matches() ||
               DATE_SLASH_PATTERN.matcher(value).matches() ||
               DATE_DASH_PATTERN.matcher(value).matches();
    }

    /**
     * Generate a random date in similar format to original
     */
    private static String generateRandomDate(String originalDate) {
        LocalDate baseDate = LocalDate.now().minusYears(RANDOM.nextInt(50));
        int randomDays = RANDOM.nextInt(365 * 10);
        LocalDate randomDate = baseDate.plusDays(randomDays);

        if (DATE_ISO_PATTERN.matcher(originalDate).matches()) {
            return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (DATE_SLASH_PATTERN.matcher(originalDate).matches()) {
            return randomDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else if (DATE_DASH_PATTERN.matcher(originalDate).matches()) {
            return randomDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
        
        return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Generate a fake credit card number
     */
    private static String generateFakeCreditCard() {
        StringBuilder card = new StringBuilder();
        // Start with 4 for Visa-like format
        card.append("4");
        for (int i = 1; i < 16; i++) {
            card.append(RANDOM.nextInt(10));
        }
        return card.toString();
    }

    /**
     * Generate a fake PAN number
     */
    private static String generateFakePAN() {
        StringBuilder pan = new StringBuilder();
        // 5 random uppercase letters
        for (int i = 0; i < 5; i++) {
            pan.append((char) ('A' + RANDOM.nextInt(26)));
        }
        // 4 random digits
        for (int i = 0; i < 4; i++) {
            pan.append(RANDOM.nextInt(10));
        }
        // 1 random uppercase letter
        pan.append((char) ('A' + RANDOM.nextInt(26)));
        return pan.toString();
    }

    /**
     * Generate a fake Aadhaar number
     */
    private static String generateFakeAadhaar() {
        StringBuilder aadhaar = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            aadhaar.append(RANDOM.nextInt(10));
            if (i == 3 || i == 7) {
                aadhaar.append(" ");
            }
        }
        return aadhaar.toString();
    }

    /**
     * Shuffle string characters while maintaining structure
     */
    private static String shuffleString(String input) {
        if (input.length() <= 1) {
            return input;
        }

        List<Character> chars = new ArrayList<>();
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                chars.add(c);
            }
        }

        Collections.shuffle(chars);

        StringBuilder result = new StringBuilder();
        int charIndex = 0;
        
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c) && charIndex < chars.size()) {
                result.append(chars.get(charIndex++));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}