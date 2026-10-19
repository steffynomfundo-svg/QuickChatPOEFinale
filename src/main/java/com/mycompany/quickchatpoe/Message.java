package com.mycompany.quickchatpoe;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one QuickChat message and holds the session-wide state
 * (sent messages, totals) used by the menu.
 */
public class Message {

    public static final int MAX_LENGTH = 250;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalSent = 0;        // messages actually sent
    private static int messageCounter = 0;   // messages created (used as the message number in the hash)
    private static Path storeFile = Paths.get("messages.json");

    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

    /** Normal constructor: generates a random ten-digit ID. */
    public Message(String recipient, String text) {
        this(generateMessageId(), recipient, text);
    }

    /** Constructor that accepts an ID (makes unit testing predictable). */
    public Message(String messageId, String recipient, String text) {
        this.messageId = messageId;
        this.recipient = recipient;
        this.text = text;
        this.messageNumber = messageCounter++;
        this.messageHash = createMessageHash();
    }

    // ---------------------------------------------------------------- checks

    /** Generates a random ten-digit number (leading zeros allowed, e.g. 0012345678). */
    public static String generateMessageId() {
        return String.format("%010d", RANDOM.nextLong(10_000_000_000L));
    }

    /** Ensures the message ID is no more than ten characters. */
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    /** Reuses the Part 1 cell number rule (checkCellPhoneNumber) from QuickChatPOE. */
    public static String checkRecipientCell(String cell) {
        if (cell != null && new QuickChatPOE().checkCellPhoneNumber(cell)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    /** Ensures the message is not more than 250 characters. */
    public static String checkMessageLength(String text) {
        int length = text == null ? 0 : text.length();
        if (length <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_LENGTH) + "; please reduce the size.";
    }

    // ------------------------------------------------------------------ hash

    /** Format -> first two digits of ID : message number : FIRSTWORD + LASTWORD (all caps). */
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String first = clean(words[0]);
        String last = clean(words[words.length - 1]);
        String prefix = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // --------------------------------------------------------- send / store

    /**
     * @param choice 1 = send, 2 = disregard, 3 = store
     * @return the confirmation message for the chosen option
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                try {
                    storeMessage();
                    return "Message successfully stored.";
                } catch (IOException e) {
                    return "Message could not be stored: " + e.getMessage();
                }
            default:
                return "Invalid option.";
        }
    }

    /** Appends this message to the JSON file (messages.json by default). */
    public void storeMessage() throws IOException {
        JsonArray array = new JsonArray();
        if (Files.exists(storeFile)) {
            try (Reader reader = Files.newBufferedReader(storeFile)) {
                JsonElement existing = JsonParser.parseReader(reader);
                if (existing != null && existing.isJsonArray()) {
                    array = existing.getAsJsonArray();
                }
            }
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("messageID", messageId);
        obj.addProperty("messageHash", messageHash);
        obj.addProperty("recipient", recipient);
        obj.addProperty("message", text);
        array.add(obj);

        try (Writer writer = Files.newBufferedWriter(storeFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
        }
    }

    // --------------------------------------------------------------- output

    /** Full details in the required order: ID, Hash, Recipient, Message. */
    public String getDetails() {
        return "Message ID: " + messageId
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + text;
    }

    /** All messages sent while the program has been running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetails()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalSent;
    }

    // ------------------------------------------------------------- helpers

    public String getMessageId() { return messageId; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }

    /** Used by tests to change where messages are stored. */
    public static void setStoreFile(Path path) { storeFile = path; }

    /** Used by tests to start each test from a clean state. */
    public static void resetState() {
        sentMessages.clear();
        totalSent = 0;
        messageCounter = 0;
    }
}
