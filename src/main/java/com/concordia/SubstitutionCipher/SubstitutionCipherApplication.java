package com.concordia.SubstitutionCipher;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.concordia.SubstitutionCipher.enums.DecryptMethod;
import com.concordia.SubstitutionCipher.utils.CipherUtils;
import com.concordia.SubstitutionCipher.utils.GeneralUtils;

@SpringBootApplication
public class SubstitutionCipherApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubstitutionCipherApplication.class);

  public static void main(String[] args) throws IOException {
    SpringApplication.run(SubstitutionCipherApplication.class, args);
    showMenu();
  }

  /**
   * Shows the initial menu
   * @throws IOException
   */
  private static void showMenu() throws IOException {
    try {
      final String option = GeneralUtils
            .getUserResponse("Choose 1 for Encrypt a Plain Text, 2 to find the key of a Ciphertext or 0 to exit:");

      switch (Integer.parseInt(option)) {
      case 0:
        System.out.println("Bye :)");
        System.exit(0);
      case 1:
        System.out.println("You chose to Encrypt a Plain Text.");
        final String text = GeneralUtils.getUserResponse("\nPlease insert the text: ");
        final String key = GeneralUtils.getUserResponse("\nPlease insert the key: ");
        final String adaptedText = GeneralUtils.adaptString(text);
        final String adaptedKey = GeneralUtils.adaptString(key);
        GeneralUtils.validate(adaptedText, adaptedKey);
        LOGGER.info("Encrypting...");
        LOGGER.info("Encrypted message: " + CipherUtils.encrypt(adaptedText, adaptedKey));
        showMenu();
      case 2:
        final String decryptionMethod = GeneralUtils
              .getUserResponse("Choose 1 for Fast Method or 2 for Decrypt and Evaluate Cryptanalysis:");
        switch (Integer.parseInt(decryptionMethod)) {
        case 1:
          System.out.println("You selected Fast Method Cryptanalysis...");
          final String cipherTextForFastMethod = GeneralUtils.getUserResponse("Please insert the cipher text: ");
          final String adapterCipherTextForFastMethod = GeneralUtils.adaptString(cipherTextForFastMethod);
          CipherUtils.decrypt(adapterCipherTextForFastMethod, DecryptMethod.FAST_METHOD);
          showMenu();
        case 2:
          System.out.println("You selected Fast Method Cryptanalysis...");
          final String cipherTextForDecryptAndEvaluate = GeneralUtils
                .getUserResponse("Please insert the cipher text: ");
          final String adapterCipherTextForDecryptAndEvaluate = GeneralUtils
                .adaptString(cipherTextForDecryptAndEvaluate);
          CipherUtils.decrypt(adapterCipherTextForDecryptAndEvaluate, DecryptMethod.DECRYPT_AND_ANALYSIS_METHOD);
          showMenu();
        default:
          System.out.println("You chose an invalid option.");
          showMenu();
        }

      default:
        System.out.println("You chose an invalid option.");
        showMenu();
      }
    } catch (NumberFormatException e) {
      throw new NumberFormatException("You must choose a number for the option.");
    }
  }
}
