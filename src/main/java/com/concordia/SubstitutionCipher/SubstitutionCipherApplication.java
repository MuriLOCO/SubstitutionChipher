package com.concordia.SubstitutionCipher;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.concordia.SubstitutionCipher.enums.DecryptMethod;
import com.concordia.SubstitutionCipher.utils.CipherUtils;
import com.concordia.SubstitutionCipher.utils.GeneralUtils;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SpringBootApplication
public class SubstitutionCipherApplication extends Application {

  private double xOffset = 0;
  private double yOffset = 0;

  private static final Logger LOGGER = LoggerFactory.getLogger(SubstitutionCipherApplication.class);

  public static void main(String[] args) throws IOException {
    SpringApplication.run(SubstitutionCipherApplication.class, args);
    launch(args);
    //showMenu();
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
      
      stage.initStyle(StageStyle.TRANSPARENT);
      root.setOnMousePressed(new EventHandler<MouseEvent>(){
       @Override
      public void handle(final MouseEvent event) {
              xOffset = event.getSceneX();
              yOffset = event.getSceneY();
          }
       });
       root.setOnMouseDragged(new EventHandler<MouseEvent>(){
          @Override
      public void handle(final MouseEvent event) {
               stage.setX(event.getSceneX() - xOffset);
               stage.setY(event.getScreenY() - yOffset);
           }
       });

    final Scene scene = new Scene(root);
      scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
      stage.setScene(scene);
      stage.show();
  }
  
  /**
   * Shows the initial menu
   * @throws Exception 
   */
  private static void showMenu() throws Exception {
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
              .getUserResponse(
                    "Choose 1 for Fast Method, 2 for Decrypt and Evaluate Cryptanalysis or 3 for a Mixed method:");
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
          CipherUtils.decrypt(adapterCipherTextForDecryptAndEvaluate, DecryptMethod.DECRYPT_AND_EVALUATE_METHOD);
          showMenu();
        case 3:
          System.out.println("You selected Mixed Method...");
          final String cipherTextForMixedMethod = GeneralUtils
                .getUserResponse("Please insert the cipher text: ");
          final String adapterCipherTextForMixedMethod = GeneralUtils
                .adaptString(cipherTextForMixedMethod);
          CipherUtils.decrypt(adapterCipherTextForMixedMethod, DecryptMethod.MIXED_METHOD);
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
