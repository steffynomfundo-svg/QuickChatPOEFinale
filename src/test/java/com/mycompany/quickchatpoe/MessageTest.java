package com.mycompany.quickchatpoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    // Test data from the brief
    private static final String ID_1 = "0012345678";
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    private static final String ID_2 = "0112345678";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    private Path storePath;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Message.resetState();
        storePath = tempDir.resolve("messages.json");
        Message.setStoreFile(storePath);
    }

    // ---------------------------------------------------- message length

    @Test
    void messageLength_success() {
        assertEquals("Message ready to send.", Message.checkMessageLength(TEXT_1));
    }

    @Test
    void messageLength_failure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    // ------------------------------------------------------- recipient

    @Test
    void recipient_success() {
        assertEquals("Cell phone number successfully captured.", Message.checkRecipientCell(RECIPIENT_1));
    }

    @Test
    void recipient_failure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", Message.checkRecipientCell(RECIPIENT_2));
    }

    // ------------------------------------------------------------ hash

    @Test
    void messageHash_testCase1() {
        Message m = new Message(ID_1, RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    void messageHash_allTestData_loop() {
        String[][] data = {
                // id, recipient, text, expected hash
                {ID_1, RECIPIENT_1, TEXT_1, "00:0:HITONIGHT"},
                {ID_2, RECIPIENT_2, TEXT_2, "01:1:HIPAYMENT"}
        };
        for (String[] row : data) {
            Message m = new Message(row[0], row[1], row[2]);
            assertEquals(row[3], m.createMessageHash());
        }
    }

    // ------------------------------------------------------ message ID

    @Test
    void messageId_isCreated() {
        Message m = new Message(RECIPIENT_1, TEXT_1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageId().length());
        assertTrue(m.getMessageId().matches("\\d{10}"));
        System.out.println("Message ID generated: " + m.getMessageId());
    }

    // ------------------------------------------------------ MessageSent

    @Test
    void messageSent_send() {
        Message m = new Message(ID_1, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    void messageSent_disregard() {
        Message m = new Message(ID_2, RECIPIENT_2, TEXT_2);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    void messageSent_store() throws Exception {
        Message m = new Message(ID_1, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", m.sentMessage(3));
        assertTrue(Files.exists(storePath));
        String json = Files.readString(storePath);
        assertTrue(json.contains(ID_1));
        assertTrue(json.contains(TEXT_1));
    }

    // -------------------------------------------------- totals & printing

    @Test
    void totalMessagesSent_countsOnlySentMessages() {
        new Message(ID_1, RECIPIENT_1, TEXT_1).sentMessage(1); // sent
        new Message(ID_2, RECIPIENT_2, TEXT_2).sentMessage(2); // discarded
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessages_showsDetailsInCorrectOrder() {
        new Message(ID_1, RECIPIENT_1, TEXT_1).sentMessage(1);
        String output = Message.printMessages();
        int id = output.indexOf("Message ID");
        int hash = output.indexOf("Message Hash");
        int rec = output.indexOf("Recipient");
        int msg = output.indexOf("Message: ");
        assertTrue(id < hash && hash < rec && rec < msg);
    }

    // ------------------------------------- Part 1 rules reused by Part 2

    @Test
    void part1_cellNumberRule() {
        QuickChatPOE login = new QuickChatPOE();
        assertTrue(login.checkCellPhoneNumber(RECIPIENT_1));
        assertFalse(login.checkCellPhoneNumber(RECIPIENT_2));
    }
}
