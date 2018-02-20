package com.concordia.SubstitutionCipher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistribuationUtils {

  private static final String TEMP_PATH = System.getProperty("java.io.tmpdir");
  private static final String TEMP_FILE_NAME = "temp_file.txt";
  private static final Logger LOGGER = LoggerFactory.getLogger(DistribuationUtils.class);

  /**
   * Cryptanalysis of cipher text using "A Fast Method for the Cryptanalysis of Substitution Ciphers" paper
   * By Thomas Jakobseny
   * @param initialKey - The initial putative key
   * @param cipherText - The current cipherText
   * @return Found key
   * @throws IOException 
   */
  public static String applyFastMethodCryptanalysis(final String initialKey, final String cipherText)
        throws IOException {
    LOGGER.info("Applying Fast Method Cryptanalysis...");
    long loopCounter = 0;
    int a = 1;
    int b = 1;

    LOGGER.info("Loading the English bi-gram file...");
    LOGGER.info(
          "This file was taken from website http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/");
    final String biGramFile = IOUtils.toString(Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("english_bigrams.txt"), "UTF-8");

    LOGGER.info(
          "Preparing the E matrix (English bi-gram frequency), it is a 26X26 that contains the english distribution of bigrams.");
    final Double[][] E = generateLanguageFreqDistMatrix(biGramFile);
    LOGGER.info("Step 0 in algorithm:  Let a = b = 1");
    LOGGER.info(
          "Step 1 in algorithm,  Construct an initial key guess, k, based upon the symbol frequencies of the expected language and the ciphertext.");
    String k = initialKey;
    LOGGER.debug("The first key is: " + k);

    LOGGER.info(
          "Step 2 in algorithm: Let D = D(d(c; k)). Basically it prepare distribution matrix of cipher text, it consist of 2 steps.");

    Double[][] d = generateTextFreqDistMatrix(2, cipherText, initialKey);

    LOGGER.info("Step 3 in algorithm:  Let v = f (d(c; k))");
    double v = calculateFitness(d, E);

    LOGGER.info("Step 4 in Algorithm :  Let k0 = k.");
    String k0 = k;

    LOGGER.info("Step 5 in Algorithm :  5. Let D0 = D.");

    Double[][] d0 = cloneArray(d);
    while (true) {
      LOGGER.info("Step 6 in the algorithm:  Change k0 by swapping two elements, Alpha and Beta, in k0");

      LOGGER.info("Step 6a in algorithm,  Let Alpha = Sa and Beta = Sa+b . Swap the symbols Alpha and Beta in k0");
      LOGGER.info("Adding a *, this is done to keep a and b values as in the algorithm starts with 1.");
      String tempS = "*" + k0;
      char[] c = tempS.toCharArray();

      int alpha = c[a];
      int beta = c[a + b];

      char temp = c[a];
      c[a] = c[a + b];
      c[a + b] = temp;
      tempS = new String(c);
      LOGGER.info("Removing the *.");
      k0 = tempS.substring(1);

      LOGGER.info("Step 6b in algorithm,  Let a = a + 1.");
      a = a + 1;

      LOGGER.info("Step 6c. If a + b <= 27 then go to step 7.");
      if ((a + b) <= 26) {
        //We don't have to write any code because the next executed sentence is Step 7 
      } else {
        LOGGER.info("Step 6d in algorithm . Let a = 1.");
        a = 1;
        LOGGER.info("Step 6e in algorithm. Let b = b + 1.");
        b = b + 1;
        LOGGER.info("Step 6f in algorithm. If b = 27 then terminate algorithm.");
        if (b == 26) {
          break;
        }
      }

      LOGGER.info(
            "Step 7 in algorithm. Exchange the corresponding rows in D0. Exchange the corresponding columns in D0");
      d0 = exchangeArrayColumnAndRows(d0, alpha - 65, beta - 65);

      LOGGER.info("Step 8 in Algorithm,  calculate fitness of new distribution matrix.");
      double V0 = calculateFitness(d0, E);

      LOGGER.info("Step 9 in Algorithm,  If v 0 >= v then go to step 4.");
      if (V0 >= v) {

        LOGGER.info("Step 4 in Algorithm :  Let k0 = k.");
        k0 = k;
        LOGGER.info("Step 5 in Algorithm :  5. Let D0 = D.");
        d0 = cloneArray(d);

      } else {
        //If the new key enhance the fitness, we consider it better and we update our initial key
        LOGGER.info("Step 9b in algorithm. Let a = b = 1.");
        a = 1;
        b = 1;

        LOGGER.info("Step 10 in Algorithm,  Let v = v0");
        v = V0;

        LOGGER.info("Step 11 in Algorithm. Let k = k0");
        k = k0;

        LOGGER.info("Step 12. Let D = D0");
        d = cloneArray(d0);

        LOGGER.info("Step 13 in algorithm. Go to step 6.");
        //We don't have to write code because the next executed phrase is step  6
      }
      loopCounter++;
    }
    LOGGER.info("Loop Counter =" + loopCounter);
    return CipherUtils.findKey(k);
  }

  /**
   * Extract ngrams from a given file
   * @param bigram - English bigram
   * @return a hash table with all ngrams loaded
   * @throws IOException 
   */
  public static Hashtable<String, Double> generateLanguageNGrams(final String bigram) throws IOException {

    final Hashtable<String, Double> ngrams = new Hashtable<>();

    LOGGER.info("Saving temporay the bigram file into a temp folder...");
    final File file = new File(TEMP_PATH + TEMP_FILE_NAME);

    FileInputStream fis = null;
    BufferedReader br = null;
    BufferedWriter output = null;
    try {
      LOGGER.info("Writing the content into the File.");
      output = new BufferedWriter(new FileWriter(file));
      output.write(bigram);
      output.flush();
      fis = new FileInputStream(file);
      br = new BufferedReader(new InputStreamReader(fis));
      String line = null;
      Double counterNgram = 0.0;
      while ((line = br.readLine()) != null) {
        String[] arrOfString = line.split(" ");
        LOGGER.debug("Line is: " + line);
        counterNgram = counterNgram + Double.valueOf(arrOfString[1]);
        ngrams.put(arrOfString[0], Double.valueOf(arrOfString[1]));
      }
      LOGGER.info(
            "Calculating the frequencies because the textfile has the count of occurrence for each bigram, and not it's frequency.");
      final Enumeration<String> e = ngrams.keys();
      while (e.hasMoreElements()) {
        final String key = e.nextElement();
        LOGGER.debug("Key is: " + key);
        ngrams.put(key, ngrams.get(key) / counterNgram);
      }
    } catch (IOException e) {
      LOGGER.error("An error occured while extracting the ngrams.");
      throw e;
    } finally {
      LOGGER.info("Closing buffers...");
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
      LOGGER.debug("Gram: " + inputText);
    }

    final Enumeration<String> e = ngrams.keys();
    while (e.hasMoreElements()) {
      final String key = e.nextElement();
      LOGGER.debug("Key: " + key);
      ngrams.put(key, ngrams.get(key) / countAll);
    }
    logHashTable(ngrams);
    return (ngrams);

  }

  /**
   * Logs the HashTable with DEBUG level
   * @param table - Hashtable to be logged
   */
  private static void logHashTable(final Hashtable<String, Double> table) {
    final Enumeration<String> e = table.keys();
    while (e.hasMoreElements()) {
      final String key = (String) e.nextElement();
      LOGGER.debug(key + " : " + table.get(key));
    }
  }

  /**
   * Extract ngrams from a given text and populate the loaded hash table into in 26X26 Matrix
   * @param intgram - Integer with frequency
   * @param inputText - Current input text
   * @return a new array with distribution matrix for a given text
   */
  public static Double[][] generateTextFreqDistMatrix(final int intgram, final String inputText, final String key) {

    LOGGER.info(
          "2.1 extract the bigarms from cipher text and find the frequency and save it into a Hash Table temporary");
    final Hashtable<String, Double> nGrams = generateTextNgrams(intgram, inputText);

    LOGGER.info("2.2 convert the bigrams into 26X26 matrix");
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

    LOGGER.info("Initiates the matrix");
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
      String key = (String) e.nextElement();
      alpha = key.charAt(0);
      beta = key.charAt(1);
      i = alpha - 65;
      j = beta - 65;
      matrix[i][j] = table.get(key);

    }
    return (matrix);
  }

  /**
   * Read biGrams from a file and store it in 26X26 Matrix
   * 
   * @param bigram - English bigrams
   * @return a new array with frequency for each ngram
   * @throws IOException 
   */
  private static Double[][] generateLanguageFreqDistMatrix(String bigram) throws IOException {
    LOGGER.info("Reading bigram from the text file and put them in temporarly into  hash table...");
    LOGGER.info("Prepare the E matrix (English bi-gram frequency)...");
    LOGGER.info("It will parse the hash table and convert it into 26X26 matrix.");

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