/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchatpoe;

/**
 *
 * @author Student
 */


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuickChatPOETest {

    QuickChatPOE app = new QuickChatPOE();

    @Test
    public void testCheckUserName() {
        assertTrue(app.checkUserName("kyl_1"));
    }

    @Test
    public void testCheckUserNameInvalid() {
        assertFalse(app.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testCheckPasswordComplexity() {
        assertTrue(app.checkPasswordComplexity("Ch&&sec@ke99&"));
    }

    @Test
    public void testCheckPasswordComplexityInvalid() {
        assertFalse(app.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneNumber() {
        assertTrue(app.checkCellPhoneNumber("+27606127145"));
    }

    @Test
    public void testCheckCellPhoneNumberInvalid() {
        assertFalse(app.checkCellPhoneNumber("0606243568"));
    }
}
