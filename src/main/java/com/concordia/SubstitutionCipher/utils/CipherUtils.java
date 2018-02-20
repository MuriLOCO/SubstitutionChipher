package com.concordia.SubstitutionCipher.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.concordia.SubstitutionCipher.enums.BaseVariables;

public class CipherUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CipherUtils.class);

  // The order of letter is extracted from calculation in the code
  private final static char[] FREQUENCE_ORDER_CHARACTERS = { 'E', 'T', 'A', 'O', 'I', 'N', 'S', 'R', 'H', 'L', 'D',
        'C', 'U', 'M', 'F', 'G', 'P', 'W', 'Y', 'B', 'V', 'K', 'J', 'X', 'Z', 'Q' };

  /**
  * Encrypts the plain text using the corresponding key 
  * @param plainText - Plain text to be encrypted
  * @param key - Current key
  * @return Cipher Text in String format
  */
  public static String encrypt(final String plainText, final String key) {
    // First, transform the plainText into an Array of Chars
    final char[] plainTextChar = plainText.toUpperCase().toCharArray();

    // Now transform the key into an Array of Chars
    final char[] keyChar = key.toUpperCase().toCharArray();

    // We create a String Buffer to be able to build our Cipher Text
    final StringBuffer cipherText = new StringBuffer();

    // Now we will iterate into each character of the plain text and we will apply
    // the substitution
    for (int i = 0; i < plainTextChar.length; i++) {
      // Finds the position of the current character on the alphabet
      int position = checkAlphabetPosition(plainTextChar[i]);
      // Inserts the current position of the key based on the position of the alphabet
      cipherText.append(keyChar[position]);
    }

    // Returns the Cipher Text
    return cipherText.toString();
  }

  /**
   * Method override of encrypt, the applyKey follows the same strategy as encrypt
   * @param cipherText
   * @param key
   * @return
   */
  public static String applyKey(final String cipherText, final String key) {
    return encrypt(cipherText, key);
  }

  /**
   * Checks the position of the current char based on the selected alphabe
   * @param  - Current char
   * @return Position of the char in a int format
   */
  private static int checkAlphabetPosition(final char c) {
    int currentCharPosition = 0;
    final char[] alphabetIntoArray = BaseVariables.ALPHABET_ENGLISH.getValue().toCharArray();
    for (int i = 0; i < alphabetIntoArray.length; i++) {
      if (c == alphabetIntoArray[i]) {
        currentCharPosition = i;
        break;
      }
    }
    return currentCharPosition;
  }

  /**
   * Decrypts the cipher text
   * @param cipherText - Current cipher text
   * @return Decrypted text as String
   * @throws IOException
   */
  public static String decrypt(final String cipherText) throws IOException {
    final Map<Character, Double> characterFrequency = getFrequencyOfChars(cipherText);

    // Sorts the Map to the most frequent to less frequent
    final List<Map.Entry<Character, Double>> sortedFrequencies = characterFrequency.entrySet().stream()
          .sorted(Map.Entry.<Character, Double> comparingByValue().reversed()).collect(Collectors.toList());

    final Map<Character, Character> replacementsCharacters = new HashMap<>();
    for (int i = 0; sortedFrequencies.size() > i; i++) {
      replacementsCharacters.put(FREQUENCE_ORDER_CHARACTERS[i], sortedFrequencies.get(i).getKey());
    }

    final StringBuffer recoveredKey = new StringBuffer();
    replacementsCharacters.forEach((englishChar, cipherChar) -> {
      recoveredKey.append(cipherChar);
    });

    final String recoveredKeyFinal = DistribuationUtils.applyFastMethodCryptanalysis(recoveredKey.toString(),
          cipherText);

    LOGGER.info("Initial Guess Key: " + recoveredKey.toString());
    LOGGER.info("Recovered Key: " + recoveredKeyFinal);
    System.out.println("Initial Guess Key: " + recoveredKey.toString());
    System.out.println("Recovered Key: " + recoveredKeyFinal);
    final String decryptedText = applyRecoveredKey(cipherText, recoveredKeyFinal);
    LOGGER.info(decryptedText);
    System.out.println(decryptedText);

    return null;
  }

  /**
  * Gets the frequency of the characters which is coming from the Cipher Text
  * @param cipherText- The cipher text
  * @return Map of Characters and Double where Character is the current Char and Double is the percentage of this char
  */
  private static Map<Character, Double> getFrequencyOfChars(final String cipherText) {
    final Map<Character, Integer> frequencyMap = new HashMap<>();

    for (int i = 0; i < cipherText.length(); i++) {
      if (frequencyMap.get(cipherText.charAt(i)) == null)
        frequencyMap.put(cipherText.charAt(i), 0);
      else
        frequencyMap.put(cipherText.charAt(i), frequencyMap.get(cipherText.charAt(i)) + 1);
    }

    final Map<Character, Double> percentageMap = new HashMap<>();
    frequencyMap.forEach((character, number) -> {
      percentageMap.put(character, (number + 1) / new Double((cipherText.length())) * 100);
    });
    for (int i = 0; i < BaseVariables.ALPHABET_ENGLISH.getValue().length(); i++) {
      if (percentageMap.containsKey(BaseVariables.ALPHABET_ENGLISH.getValue().charAt(i)) == false) {
        percentageMap.put(BaseVariables.ALPHABET_ENGLISH.getValue().charAt(i), 0.0);
      }
    }
    return percentageMap;
  }

  /**
   * Decrypts a Cipher Text
   * @param cipherText - Current cipherText
   * @param putativeKey - Putative Key   
   * @return Plain text as String
   */
  public static String applyRecoveredKey(final String cipherText, final String putativeKey) {
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < cipherText.length(); i++) {
      final char character = cipherText.charAt(i);
      final int ascii = putativeKey.indexOf(character) + 65;
      if (ascii < 65 || ascii > 90) {
      } else {
        buffer.append(String.valueOf((char) ascii));
      }
    }
    return buffer.toString();
  }

  /**
   * Find the key
   * @param putativeKey - Putative Key
   * @param cipherText - Current cipherText
   * @return key
   */
  public static String findKey(final String putativeKey) {
    return applyRecoveredKey(BaseVariables.ALPHABET_ENGLISH.getValue(), putativeKey);
  }
}
