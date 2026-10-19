package com.mycompany.quickchatpoe;

import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {

        // ---- Requirement 1: only logged-in users may send messages (Part 1 code) ----
        QuickChatPOE user = new QuickChatPOE();
        if (!registerAndLogin(user)) {
            return; // user cancelled
        }

        // ---- Requirement 2: welcome message ----
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

        // ---- Requirement 5: user decides how many messages they will enter ----
        int maxMessages = askForPositiveInt("How many messages do you wish to enter?");
        if (maxMessages < 0) {
            return; // user cancelled
        }
        int entered = 0;

        // ---- Requirements 3 & 4: numeric menu that runs until the user quits ----
        boolean running = true;
        while (running) {
            String choice = JOptionPane.showInputDialog(null,
                    "Choose an option:\n1) Send Messages\n2) Show recently sent messages\n3) Quit",
                    "QuickChat", JOptionPane.QUESTION_MESSAGE);

            if (choice == null) {
                choice = "3"; // closing the dialog = quit
            }

            switch (choice.trim()) {
                case "1":
                    if (entered >= maxMessages) {
                        JOptionPane.showMessageDialog(null,
                                "You have already entered all " + maxMessages + " messages.");
                        break;
                    }
                    while (entered < maxMessages) {
                        if (enterOneMessage(entered + 1, maxMessages)) {
                            entered++;
                        } else {
                            break; // user cancelled out of the message dialogs
                        }
                    }
                    break;
                case "2":
                    JOptionPane.showMessageDialog(null, "Coming Soon.");
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Please enter 1, 2 or 3.");
            }
        }

        // ---- Requirement 6 (second): total number of messages sent ----
        JOptionPane.showMessageDialog(null,
                "Total messages sent: " + Message.returnTotalMessages());
    }

    /** Collects, validates and processes a single message. Returns false if the user cancelled. */
    private static boolean enterOneMessage(int number, int max) {
        String title = "Message " + number + " of " + max;

        // Recipient
        String recipient;
        while (true) {
            recipient = JOptionPane.showInputDialog(null,
                    "Enter the recipient cell number (include the international code, e.g. +27718693002):",
                    title, JOptionPane.QUESTION_MESSAGE);
            if (recipient == null) {
                return false;
            }
            recipient = recipient.trim();
            String result = Message.checkRecipientCell(recipient);
            JOptionPane.showMessageDialog(null, result);
            if (result.startsWith("Cell phone number successfully")) {
                break;
            }
        }

        // Message text (max 250 characters)
        String text;
        while (true) {
            text = JOptionPane.showInputDialog(null, "Enter your message (max 250 characters):",
                    title, JOptionPane.QUESTION_MESSAGE);
            if (text == null) {
                return false;
            }
            if (text.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "The message cannot be empty.");
                continue;
            }
            if (text.length() > Message.MAX_LENGTH) {
                JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.\n"
                        + Message.checkMessageLength(text));
                continue;
            }
            JOptionPane.showMessageDialog(null, "Message sent");
            break;
        }

        Message message = new Message(recipient, text);

        // Send / Disregard / Store
        String[] options = {"Send Message", "Disregard Message", "Store Message to send later"};
        int pick = JOptionPane.showOptionDialog(null, "What would you like to do with this message?",
                title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (pick < 0) {
            pick = 1; // closing the dialog = disregard
        }

        String result = message.sentMessage(pick + 1);
        JOptionPane.showMessageDialog(null, result);

        if (pick == 1) { // disregard: user presses 0 to delete
            String zero = JOptionPane.showInputDialog(null, "Press 0 to delete the message.");
            if ("0".equals(zero == null ? "" : zero.trim())) {
                JOptionPane.showMessageDialog(null, "Message deleted.");
            }
        }

        // Requirement 7: show full details after sending
        if (pick == 0) {
            JOptionPane.showMessageDialog(null, message.getDetails(), "Message details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    /** Registers the user with the Part 1 class, then logs in. Returns false if cancelled. */
    private static boolean registerAndLogin(QuickChatPOE user) {
        // --- Registration (repeat until all details are valid) ---
        while (true) {
            String first = JOptionPane.showInputDialog(null, "Register - first name:");
            if (first == null) return false;
            String last = JOptionPane.showInputDialog(null, "Register - last name:");
            if (last == null) return false;
            String username = JOptionPane.showInputDialog(null, "Register - username (underscore, max 5 characters):");
            if (username == null) return false;
            String password = JOptionPane.showInputDialog(null,
                    "Register - password (8+ characters, capital, number, special character):");
            if (password == null) return false;
            String cell = JOptionPane.showInputDialog(null, "Register - cell number (e.g. +27718693002):");
            if (cell == null) return false;

            String result = user.registerUser(username, password, cell, first, last);
            JOptionPane.showMessageDialog(null, result);
            if (result.startsWith("Username successfully captured")) {
                break;
            }
        }

        // --- Login (repeat until correct) ---
        while (true) {
            String u = JOptionPane.showInputDialog(null, "Login - username:");
            if (u == null) return false;
            String p = JOptionPane.showInputDialog(null, "Login - password:");
            if (p == null) return false;

            boolean ok = user.loginUser(u, p);
            JOptionPane.showMessageDialog(null, user.returnLoginStatus(ok));
            if (ok) {
                return true;
            }
        }
    }

    /** Returns a positive int, or -1 if the user cancels. */
    private static int askForPositiveInt(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, prompt);
            if (input == null) {
                return -1;
            }
            try {
                int value = Integer.parseInt(input.trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            JOptionPane.showMessageDialog(null, "Please enter a whole number greater than 0.");
        }
    }
}
