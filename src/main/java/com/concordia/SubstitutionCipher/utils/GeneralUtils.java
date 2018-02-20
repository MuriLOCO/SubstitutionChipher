package com.concordia.SubstitutionCipher.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GeneralUtils {

  /**
   * Gets the user response according with the message input
   * @param systemMessage - Message chose to show as System Message
   * @return Response of user in String format
   */
  public static String getUserResponse(final String systemMessage) {

    try {
      System.out.print(systemMessage);

      final InputStreamReader streamReadeText = new InputStreamReader(System.in);
      final BufferedReader bufferedReaderText = new BufferedReader(streamReadeText);
      return bufferedReaderText.readLine();

    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  /**
   * Validade the key and the text
   * @param text - Plain text
   * @param key - Chosen key
   */
  public static void validate(final String text, final String key) {
    //This is the English Alphabet defined in a Z26 (Upper case)
    final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";    
    
     if (key.length() != 26)
      throw new RuntimeException("The key must have 26 characters");
    
    //This checks if the key has repeatable characters
    for (int i = 0; i < alphabet.length(); i++) {
      int count = 0;
      for (int j = 0; j < key.length(); j++) {
        if (alphabet.charAt(i) == key.charAt(j))
          count++;
      }
      if (count > 1) {
        throw new RuntimeException("The key must not have repeateble characters.");
      }
    }
  }
  
  /**
   * Deletes numbers, special characters, spaces and makes upper case of a String
   * @param string
   * @return clean String
   */
  public static String adaptString(final String string) {
	  return string.replaceAll("[^a-zA-Z]+","").trim().toUpperCase();
  }  
  
}
