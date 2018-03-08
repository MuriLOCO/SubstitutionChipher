package com.concordia.SubstitutionCipher.utils;

import com.concordia.SubstitutionCipher.enums.BaseVariables;

public class GeneralUtils {

  /**
  * Validade the key
  * @param key - Chosen key
   */
  public static boolean validateKey(final String key) {
    //This checks if the key has repeatable characters
    for (int i = 0; i < BaseVariables.ALPHABET_ENGLISH.getValue().length(); i++) {
      int count = 0;
      for (int j = 0; j < key.length(); j++) {
        if (BaseVariables.ALPHABET_ENGLISH.getValue().charAt(i) == key.charAt(j))
          count++;
      }
      if (count > 1 || key.length() != 26)
        return false;
    }
    return true;
  }

  /**
   * Deletes numbers, special characters, spaces and makes upper case of a String
   * @param string
   * @return clean String
   */
  public static String adaptString(final String string) {
    return string.replaceAll("[^a-zA-Z]+", "").trim().toUpperCase();
  }

}
