package com.mycompany.quickchatpoe;

/**
 *
 * @author Student
 */
public class QuickChatPOE {
    private String storedUsername;
    private String storedPassword;
    private String storedCellNumber;
    private String firstName;
    private String lastName;

    public boolean checkUserName(String username){ 
    boolean hasUnderScore = username.contains("_");
            boolean validLength = username.length() <=5;
                    return hasUnderScore && validLength;
    }
    public boolean checkPasswordComplexity(String password){
    boolean validLength = password.length() >= 8;
    boolean hasCapital = password.matches(".*[A-Z].*");
    boolean hasNumber = password.matches (".*[0-9].*");
    boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");
    return validLength && hasCapital && hasNumber && hasSpecialChar;
    }
    public boolean checkCellPhoneNumber(String cellNumber){
    boolean validFormat = cellNumber.matches("^\\+\\d{1,11}$");
    return validFormat;
    }
    public String registerUser(String username, String password, String cellNumber, 
            String firstName, String lastName){
        if (!checkUserName(username)){
            return "Username is not correctly formatted; Please make sure that your username contains an underscore and is not more than five characters in length.";
                    
        }
        if (!checkPasswordComplexity(password)){
        return "Password is not correctly formatted ; Please make sure that the password contains atleast eight characters, "
                + "a capital letter, a number, and a special character.";
        }
    if (!checkCellPhoneNumber(cellNumber)){
    return "Cellphone number incorrectly formatted or does not contain international code.";
    }
    this.storedUsername = username;
    this.storedPassword = password;
    this.storedCellNumber = cellNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    return "Username successfully captured. Password successfully. Cellphone number successfully added.";
    }
    public boolean loginUser(String username, String password){
    return username.equals(storedUsername) && password.equals(storedPassword);
    }
   public String returnLoginStatus(boolean loginSuccess){
   if (loginSuccess){
   return "Welcome" + firstName + "," + lastName + "It's great to see you again.";
  
   }else{
   return "Username or password incorrect, Please try again.";
   }
   }
    }
