package com.concordia.SubstitutionCipher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.concordia.SubstitutionCipher.enums.BaseVariables;
import com.concordia.SubstitutionCipher.enums.DecryptMethod;
import com.concordia.SubstitutionCipher.wrappers.CryptanalysisWrapper;

public class CipherUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CipherUtils.class);

  private static final String TEMP_PATH = System.getProperty("java.io.tmpdir");
  private static final String TEMP_FILE_NAME = "temp_file.txt";

  private static String globalRecoveredKey;

  private final static char[] FREQUENCE_ORDER_CHARACTERS = { 'E', 'T', 'A', 'O', 'I', 'N', 'S', 'R', 'H', 'L', 'D',
        'C', 'U', 'M', 'F', 'G', 'P', 'W', 'Y', 'B', 'V', 'K', 'J', 'X', 'Z', 'Q' };

  /**
  * Encrypts the plain text using the corresponding key 
  * @param plainText - Plain text to be ted
  * @param key - Current key
  * @return Cipher Text in String format
  */
  public static String encrypt(final String plainText, final String key) {
    LOGGER.trace("First, transform the plainText into an Array of Chars");
    final char[] plainTextChar = plainText.toUpperCase().toCharArray();

    LOGGER.trace("Now transform the key into an Array of Chars");
    final char[] keyChar = key.toUpperCase().toCharArray();

    LOGGER.trace("We create a String Buffer to be able to build our Cipher Text");
    final StringBuffer cipherText = new StringBuffer();

    LOGGER.trace("Now we will iterate into each character of the plain text and we will apply the substitution");
    for (int i = 0; i < plainTextChar.length; i++) {
      LOGGER.trace("Finds the position of the current character on the alphabet");
      int position = checkAlphabetPosition(plainTextChar[i]);
      LOGGER.trace("Inserts the current position of the key based on the position of the alphabet");
      cipherText.append(keyChar[position]);
    }

    // Returns the Cipher Text
    return cipherText.toString();
  }

  /**
   * Decrypts the plain text using the corresponding key 
   * @param plainText - Plain text to be ted
   * @param key - Current key
   * @return Cipher Text in String format
   */
  public static String applyDecryption(final String plainText, final String key) {
    return encrypt(plainText, key);
  }

  /**
   * Order the key to English Alphabet   
   * @param key - key  
   * @return Ordered key as String
   */
  private static String orderKey(final String key) {
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < BaseVariables.ALPHABET_ENGLISH.getValue().length(); i++) {
      final char character = BaseVariables.ALPHABET_ENGLISH.getValue().charAt(i);
      final int ascii = key.indexOf(character) + 65;
      if (ascii < 65 || ascii > 90) {
      } else {
        buffer.append(String.valueOf((char) ascii));
      }
    }
    return buffer.toString();
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
   * @throws Exception 
   */
  public static CryptanalysisWrapper decrypt(final String cipherText, final DecryptMethod decryptMethod)
        throws Exception {
    final Map<Character, Double> characterFrequency = getFrequencyOfChars(cipherText);

    LOGGER.trace("Sorts the Map to the most frequent to less frequent");
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
    if (decryptMethod.equals(DecryptMethod.FAST_METHOD))
      return applyFastMethodCryptanalysis(recoveredKey.toString(),
            cipherText);

    else if (decryptMethod.equals(DecryptMethod.DECRYPT_AND_EVALUATE_METHOD))
      return applyDecrypAndEvaluateCryptanalysis(recoveredKey.toString(),
            cipherText);
    
    else if (decryptMethod.equals(DecryptMethod.MIXED_METHOD))
      return applyMixedMethod(recoveredKey.toString(), cipherText);

    else
      throw new Exception("Failed to Decrypt.");
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
   * Cryptanalysis of cipher text using "A Fast Method for the Cryptanalysis of Substitution Ciphers" paper
   * By Thomas Jakobseny
   * @param initialKey - Initial key
   * @param cipherText - Cipher Text
   * @return Plain text at String format
   * @throws IOException 
   */
  private static CryptanalysisWrapper applyFastMethodCryptanalysis(final String initialKey, final String cipherText)
        throws IOException {
    LOGGER.trace("Reseting the global found key...");
    globalRecoveredKey = null;
    LOGGER.info("Applying Fast Method Cryptanalysis...");
    long loopCounter = 0;
    int a = 1;
    int b = 1;

    LOGGER.trace("Loading the English bi-gram file...");
    LOGGER.trace(
          "This file was taken from website http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/");
    final String bigramFile = IOUtils.toString(Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("english_bigrams.txt"), "UTF-8");

    LOGGER.trace(
          "Preparing the E matrix (English bi-gram frequency), it is a 26X26 that contains the english distribution of bigrams.");
    final Double[][] languageFreqDistMatrix = generateLanguageFreqDistMatrix(bigramFile);
    LOGGER.trace("Step 0 in algorithm:  Let a = b = 1");
    LOGGER.trace(
          "Step 1 in algorithm,  Construct an initial key guess, k, based upon the symbol frequencies of the expected language and the ciphertext.");
    String key = initialKey;
    LOGGER.info("Initial key guess is: " + key);

    LOGGER.trace(
          "Step 2 in algorithm: Let D = D(d(c; k)). Basically it prepare distribution matrix of cipher text, it consist of 2 steps.");

    Double[][] textFreqDistMatrix = generateTextFreqDistMatrix(2, cipherText, initialKey);

    LOGGER.trace("Step 3 in algorithm:  Let v = f (d(c; k))");
    double fitness = calculateFitness(textFreqDistMatrix, languageFreqDistMatrix);

    LOGGER.trace("Step 4 in Algorithm :  Let k0 = k.");
    String k0 = key;

    LOGGER.trace("Step 5 in Algorithm :  5. Let D0 = D.");
    Double[][] clonedArray = cloneArray(textFreqDistMatrix);
    while (true) {
      LOGGER.trace("Step 6 in the algorithm:  Change k0 by swapping two elements, Alpha and Beta, in k0");

      LOGGER.trace("Step 6a in algorithm,  Let Alpha = Sa and Beta = Sa+b . Swap the symbols Alpha and Beta in k0");
      LOGGER.trace("Adding a *, this is done to keep a and b values as in the algorithm starts with 1.");
      String tempS = "*" + k0;
      char[] c = tempS.toCharArray();

      int alpha = c[a];
      int beta = c[a + b];

      char temp = c[a];
      c[a] = c[a + b];
      c[a + b] = temp;
      tempS = new String(c);
      LOGGER.trace("Removing the *.");
      k0 = tempS.substring(1);

      LOGGER.trace("Step 6b in algorithm,  Let a = a + 1.");
      a = a + 1;

      LOGGER.trace("Step 6c. If a + b <= 27 then go to step 7.");
      if (!((a + b) <= 26)) {
        LOGGER.trace("Step 6d in algorithm . Let a = 1.");
        a = 1;
        LOGGER.trace("Step 6e in algorithm. Let b = b + 1.");
        b = b + 1;
        LOGGER.trace("Step 6f in algorithm. If b = 27 then terminate algorithm.");
        if (b == 26)
          break;
      }

      LOGGER.trace(
            "Step 7 in algorithm. Exchange the corresponding rows in D0. Exchange the corresponding columns in D0");
      clonedArray = exchangeArrayColumnAndRows(clonedArray, alpha - 65, beta - 65);

      LOGGER.trace("Step 8 in Algorithm,  calculate fitness of new distribution matrix.");
      double secondFitness = calculateFitness(clonedArray, languageFreqDistMatrix);

      LOGGER.trace("Step 9 in Algorithm,  If v 0 >= v then go to step 4.");
      if (secondFitness >= fitness) {

        LOGGER.trace("Step 4 in Algorithm :  Let k0 = k.");
        k0 = key;
        LOGGER.trace("Key " + loopCounter + " is: " + k0);
        LOGGER.trace("Step 5 in Algorithm :  5. Let D0 = D.");
        clonedArray = cloneArray(textFreqDistMatrix);

      } else {
        //If the new key enhance the fitness, we consider it better and we update our initial key
        LOGGER.info("Key fitness has improved at attempt " + (loopCounter + 1) + ". New key: " + k0);
        LOGGER.trace("Step 9b in algorithm. Let a = b = 1.");
        a = 1;
        b = 1;

        LOGGER.trace("Step 10 in Algorithm,  Let v = v0");
        fitness = secondFitness;

        LOGGER.trace("Step 11 in Algorithm. Let k = k0");
        key = k0;

        LOGGER.trace("Step 12. Let D = D0");
        textFreqDistMatrix = cloneArray(clonedArray);

        LOGGER.trace("Step 13 in algorithm. Go to step 6.");
      }
      loopCounter++;
    }
    final String decryptedText = CipherUtils.applyDecryption(cipherText, key);
    globalRecoveredKey = CipherUtils.orderKey(key);
    LOGGER.info("Total number of attempts: " + loopCounter);
    LOGGER.info("Key that fits the most is: " + key);
    LOGGER.info("Ordering key with English Alphabet...");
    LOGGER.info("Ordered and final key is: " + globalRecoveredKey);
    LOGGER.info("Decrypted text is: " + decryptedText);
    return new CryptanalysisWrapper(globalRecoveredKey, decryptedText);
  }

  /**
   * Method more accurate but slower to Decrypt
   * @param initialKey - Initial key
   * @param cipherText - Cipher Text
   * @return Plain text at String format
   * @throws IOException
   */
  private static CryptanalysisWrapper applyDecrypAndEvaluateCryptanalysis(String initialKey, String cipherText)
        throws IOException {
    LOGGER.info("Applying Decrypt and Evaluate Cryptanalysis....");
    LOGGER.trace(
          "Loading NGram files, this file was taken from website http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/");
    final String ngramFile = IOUtils.toString(Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("english_quadgrams.txt"), "UTF-8");
    long loopCounter = 0;
    String k0;
    int alpha;
    int beta;
    double fitness;
    double newFitness;

    LOGGER.trace("Preparing the quadgrams from english...");
    final Hashtable<String, Double> languageNGram = generateLanguageNGrams(ngramFile);

    LOGGER.trace("Step 0 in algorithm :  Let a = b = 1");
    int a = 1;
    int b = 1;

    LOGGER.trace(
          "Step 1 in algorithm,  Construct an initial key guess, k, based upon the symbol frequencies of the expected language and the ciphertext");
    String key = initialKey.toUpperCase();
    LOGGER.trace("The initial guess key was calculated in the calling function and passed to this method");

    LOGGER.trace(
          "Step 2 in algorithm :  Let D = D(d(c; k)). Basically it prepare distribution matrix of cipher text, it consist of 2 steps");
    LOGGER.trace("Step 3 in algorithm:  Let v = f (d(c; k))");
    final String ptext = CipherUtils.applyDecryption(cipherText, key);
    final Hashtable<String, Double> textNgram = generateTextNgrams(4, ptext);
    fitness = calculateFitness(textNgram, languageNGram);

    LOGGER.trace("Step 4 in Algorithm:  Let k0 = k.");
    k0 = key;
    String finalDecryptedText = null;
    while (true) {
      LOGGER.trace("Step 6 in the algorithm:  Change k0 by swapping two elements, Alpha and Beta, in k0");
      LOGGER.trace("Step 6a in algorithm,  Let Alpha = Sa and Beta = Sa+b . Swap the symbols Alpha and Beta in k0");

      LOGGER.trace("This is done to keep a , b values as in the algorithm starts with 1");
      String tempS = "*" + k0;
      char[] c = tempS.toCharArray();

      alpha = c[a];
      beta = c[a + b];

      char temp = c[a];
      c[a] = c[a + b];
      c[a + b] = temp;
      tempS = new String(c);
      k0 = tempS.substring(1);
      LOGGER.trace("Removing *");

      LOGGER.trace("Step 6b in algorithm, Let a = a + 1.");
      a = a + 1;

      LOGGER.trace("Step 6c. If a + b <= 27 then go to step 7.");
      LOGGER.trace(
            "Note: They are using 27 because their array starts from 1 to 27. which contains Space, A,B....Z while the code starts from 0 to 25 which contains A,B,...Z");
      if (!((a + b) <= 26)) {
        LOGGER.trace("Step 6d in algorithm . Let a = 1.");
        a = 1;
        LOGGER.trace("Step 6e in algorithm. Let b = b + 1");
        b = b + 1;

        LOGGER.trace("Step 6f in algorithm. If b = 27 then terminate algorithm.");
        LOGGER.trace("Sote our matrix starts from 0 to 25, this is why do comparison ad b=25");
        if (b == 26) {
          break;
        }
      }
      LOGGER.trace("Decrypting the cipher text using the new key and evaluate it is fitness");
      final String decryptedText = CipherUtils.applyDecryption(cipherText, k0);
      final Hashtable<String, Double> p0 = generateTextNgrams(4, decryptedText);
      newFitness = calculateFitness(p0, languageNGram);
      LOGGER.trace(
            "Counter: " + loopCounter + " " + (char) alpha + (char) beta + " " + k0 + " " + newFitness + " " + fitness);
      LOGGER.trace(decryptedText);
      LOGGER.trace("Step 9 in Algorithm,  If v 0 >= v then go to step 4.");
      if (newFitness >= fitness) {
        LOGGER.trace("Step 4 in Algorithm :  Let k0 = k.");
        k0 = key;
      } else {
        LOGGER.info("Key fitness is better at loop: " + loopCounter + ", the new key is: " + k0);
        LOGGER.info("Decrypted text at loop: " + loopCounter + " : " + decryptedText);
        finalDecryptedText = decryptedText;
        LOGGER.trace("Step 9b in algorithm. Let a = b = 1");
        a = 1;
        b = 1;
        LOGGER.trace("Step 10 in Algorithm,  Let v = v0");
        fitness = newFitness;
        LOGGER.trace("Step 11 in Algorithm. Let k = k0");
        key = k0;
        LOGGER.trace("Step 13 in algorithm. Go to step 6.");
        LOGGER.trace("We don't have to write code because the next executed phrase is step 6.");
      }
      loopCounter++;
    }
    final String foundKey = CipherUtils.orderKey(key);
    LOGGER.info("Found key is: " + foundKey);
    CipherUtils.orderKey(key);
    LOGGER.trace("Reseting the global found key...");
    globalRecoveredKey = null;
    return new CryptanalysisWrapper(foundKey, finalDecryptedText);
  }

  /**
   * Decrypts the cipher text using a mixed method
   * @param key - the key
   * @param cipherText - The cipher text
   * @throws IOException
   */
  private static CryptanalysisWrapper applyMixedMethod(final String key, final String cipherText) throws IOException {
    applyFastMethodCryptanalysis(key, cipherText);
    return applyDecrypAndEvaluateCryptanalysis(globalRecoveredKey, cipherText);
  }

  /**
   * Extract ngrams from a given file
   * @param bigram - English bigram
   * @return a hash table with all ngrams loaded
   * @throws IOException 
   */
  private static Hashtable<String, Double> generateLanguageNGrams(final String bigram) throws IOException {

    final Hashtable<String, Double> ngrams = new Hashtable<>();

    LOGGER.trace("Saving temporay the bigram file into a temp folder...");
    final File file = new File(TEMP_PATH + TEMP_FILE_NAME);

    FileInputStream fis = null;
    BufferedReader br = null;
    BufferedWriter output = null;
    try {
      LOGGER.trace("Writing the content into the File.");
      output = new BufferedWriter(new FileWriter(file));
      output.write(bigram);
      output.flush();
      fis = new FileInputStream(file);
      br = new BufferedReader(new InputStreamReader(fis));
      String line = null;
      Double counterNgram = 0.0;
      while ((line = br.readLine()) != null) {
        String[] arrOfString = line.split(" ");
        LOGGER.trace("Line is: " + line);
        counterNgram = counterNgram + Double.valueOf(arrOfString[1]);
        ngrams.put(arrOfString[0], Double.valueOf(arrOfString[1]));
      }
      LOGGER.trace(
            "Calculating the frequencies because the textfile has the count of occurrence for each bigram, and not it's frequency.");
      final Enumeration<String> e = ngrams.keys();
      while (e.hasMoreElements()) {
        final String key = e.nextElement();
        LOGGER.trace("Key is: " + key);
        ngrams.put(key, ngrams.get(key) / counterNgram);
      }
    } catch (IOException e) {
      LOGGER.error("An error occured while extracting the ngrams.");
      throw e;
    } finally {
      LOGGER.trace("Closing buffers...");
      fis.close();
      br.close();
      output.close();
    }
    return ngrams;
  }

  /**
   * Extract ngrams from a given text and populate the loaded hash table into hashtable, ngram = 1 for mono-grams , ngram = 2 for bigrams, ngram = 3 for trigrams, etc
   * @param ngranType - Int Gram
   * @param inputText - Input Text    
   * @return a hash table with all ngrams loaded
   */
  private static Hashtable<String, Double> generateTextNgrams(final int ngranType, String inputText) {
    final Hashtable<String, Double> ngrams = new Hashtable<>();
    Double countAll = 0.0;
    while (inputText.length() > 0) {
      final StringBuffer buffer = new StringBuffer();
      if (inputText.length() >= ngranType) {
        buffer.append(inputText.substring(0, ngranType));
        if (buffer.length() == ngranType) {
          countAll = countAll + 1;
          if (ngrams.containsKey(buffer.toString())) {
            ngrams.put(buffer.toString(), ngrams.get(buffer.toString()) + 1.0);
          } else {
            ngrams.put(buffer.toString(), 1.0);
          }
        }
      }
      inputText = inputText.substring(1);
      LOGGER.trace("Gram: " + inputText);
    }

    final Enumeration<String> e = ngrams.keys();
    while (e.hasMoreElements()) {
      final String key = e.nextElement();
      LOGGER.trace("Key: " + key);
      ngrams.put(key, ngrams.get(key) / countAll);
    }

    final Enumeration<String> eForLog = ngrams.keys();
    while (e.hasMoreElements()) {
      final String key = eForLog.nextElement();
      LOGGER.trace(key + " : " + ngrams.get(key));

    }
    return ngrams;

  }

  /**
   * Extract ngrams from a given text and populate the loaded hash table into in 26X26 Matrix
   * @param intgram - Integer with frequency
   * @param inputText - Current input text
   * @return a new array with distribution matrix for a given text
   */
  private static Double[][] generateTextFreqDistMatrix(final int intgram, final String inputText, final String key) {

    LOGGER.trace(
          "2.1 extract the bigarms from cipher text and find the frequency and save it into a Hash Table temporary");
    final Hashtable<String, Double> nGrams = generateTextNgrams(intgram, inputText);

    LOGGER.trace("2.2 convert the bigrams into 26X26 matrix");
    return generateFreqDistMatrix(nGrams, key);
  }

  /**
   * Populates the loaded hash table into in 26X26 Matrix
   * @param table
   * @param k - key
   * @return New array with frequency for each ngram
   */

  private static Double[][] generateFreqDistMatrix(final Hashtable<String, Double> table, final String k) {

    String key;
    int i;
    int j;
    char alpha;
    char beta;
    Double[][] matrix = new Double[26][26];

    LOGGER.trace("Initiates the matrix");
    for (i = 0; i < 26; i++) {
      for (j = 0; j < 26; j++) {
        matrix[i][j] = 0.0;
      }
    }

    final Enumeration<String> e = table.keys();
    while (e.hasMoreElements()) {
      key = (String) e.nextElement();
      alpha = key.charAt(0);
      beta = key.charAt(1);

      i = getAlphabetMapping(alpha, k) - 65;
      j = getAlphabetMapping(beta, k) - 65;
      matrix[i][j] = table.get(key);

    }

    return matrix;
  }

  /**
   * Gets the Alphabet Mapping
   * @param c - Current C
   * @param k - Current K
   * @return
   */
  private static char getAlphabetMapping(final char c, final String k) {
    return k.toCharArray()[c - 65];
  }

  /**
   * Populate the loaded hash table into in 26X26 Matrix
   * @param table - The current hash table
   * @return a new array with frequency for each ngram
   */
  private static Double[][] generateLanguageFreqDistMatrix(final Hashtable<String, Double> table) {

    int i;
    int j;
    char alpha;
    char beta;
    final Double[][] matrix = new Double[26][26];

    // initiate the matrix
    for (i = 0; i < 26; i++) {
      for (j = 0; j < 26; j++) {
        matrix[i][j] = 0.0;
      }
    }

    final Enumeration<String> e = table.keys();
    while (e.hasMoreElements()) {
      final String key = (String) e.nextElement();
      alpha = key.charAt(0);
      beta = key.charAt(1);
      i = alpha - 65;
      j = beta - 65;
      matrix[i][j] = table.get(key);

    }
    return matrix;
  }

  /**
   * Read biGrams from a file and store it in 26X26 Matrix
   * 
   * @param bigram - English bigrams
   * @return a new array with frequency for each ngram
   * @throws IOException 
   */
  private static Double[][] generateLanguageFreqDistMatrix(String bigram) throws IOException {
    LOGGER.trace("Reading bigram from the text file and put them in temporarly into  hash table...");
    LOGGER.trace("Prepare the E matrix (English bi-gram frequency)...");
    LOGGER.trace("It will parse the hash table and convert it into 26X26 matrix.");

    return generateLanguageFreqDistMatrix(generateLanguageNGrams(bigram));
  }

  /**
   * Calculate fitness of a text n comparison to the English language using 26X26 matrix
   * @param matrix1 - First Matrix
   * @param matrix2 - Second Matrix
   * @return score of the text
   */
  private static Double calculateFitness(final Double[][] matrix1, final Double[][] matrix2) {
    Double score = 0.0;
    for (int i = 0; i < 26; i++) {
      for (int j = 0; j < 26; j++) {
        score = score + Math.abs(matrix1[i][j] - matrix2[i][j]);
      }
    }
    return score;
  }

  private static Double calculateFitness(final Hashtable<String, Double> hashTable1,
        Hashtable<String, Double> hashTable2) {
    Double score = 0.0;
    final Enumeration<String> e = hashTable2.keys();
    while (e.hasMoreElements()) {
      final String key = e.nextElement();
      if (hashTable1.containsKey(key))
        score = score + Math.abs(hashTable1.get(key) - hashTable2.get(key));
      else
        score = score + Math.abs(hashTable2.get(key));
    }
    final Enumeration<String> e1 = hashTable1.keys();
    while (e1.hasMoreElements()) {
      String key = (String) e1.nextElement();
      if (!hashTable2.containsKey(key))
        score = score + Math.abs(hashTable1.get(key));
    }
    return score;
  }

  /**
   * Clones the provided array
   * @param src - Source
   * @return a new clone of the provided array
   */
  private static Double[][] cloneArray(final Double[][] src) {
    Double[][] target = new Double[src.length][src[0].length];
    for (int i = 0; i < src.length; i++) {
      System.arraycopy(src[i], 0, target[i], 0, src[i].length);
    }
    return target;
  }

  /**
   * Exchange two columns and rows of the provided array
   * @param src - Source
   * @param indx1 - Index 1
   * @param indx2 - Index 2
   * @return a new array with exchanged column of the provided array
   */
  private static Double[][] exchangeArrayColumnAndRows(final Double[][] src, final int indx1, final int indx2) {
    Double[][] target = new Double[src.length][src[0].length];
    for (int i = 0; i < src.length; i++) {
      System.arraycopy(src[i], 0, target[i], 0, src[i].length);
    }

    for (int i = 0; i < 26; i++) {
      Double tmp = target[(indx1)][i];
      target[indx1][i] = target[indx2][i];
      target[indx2][i] = tmp;
    }
    for (int i = 0; i < 26; i++) {
      Double tmp = target[i][(indx1)];
      target[i][indx1] = target[i][indx2];
      target[i][indx2] = tmp;
    }

    return target;
  }
}
