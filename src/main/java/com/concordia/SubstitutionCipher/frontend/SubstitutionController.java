package com.concordia.SubstitutionCipher.frontend;

import com.concordia.SubstitutionCipher.enums.DecryptMethod;
import com.concordia.SubstitutionCipher.utils.CipherUtils;
import com.concordia.SubstitutionCipher.utils.GeneralUtils;
import com.concordia.SubstitutionCipher.wrappers.CryptanalysisWrapper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SubstitutionController {

  private static final String FAST_METHOD = "Fast Method";
  private static final String DECRYPT_AND_EVALUATE_METHOD = "Decrypt and Evaluate Method";
  private static final String MIXED_METHOD = "Mixed Method";

  @FXML
  private ImageView btnMenuExit, btnMenuEncrypt, btnMenuDecrypt;

  @FXML
  private AnchorPane hSettings, hEncrypt, hDecrypt;

  @FXML
  private Button btnClear, btnDecryption, btnEncryption;

  @FXML
  private TextField tfKeyEncrypt, tfKeyDecrypt;

  @FXML
  private TextArea taPlainTextEncrypt, taPlainTextDecrypt, taCipherTextEncrypt, taCipherTextDecrypt;

  @FXML
  private ComboBox<String> cbDecryptionMethod;

  private String adaptedText, adaptedKey;

  private DecryptMethod decryptMethod;

  @FXML
  private void handleButtonAction(MouseEvent event) throws Exception {
    if (event.getTarget() == btnMenuExit) {
      System.exit(0);

    } else if (event.getTarget() == btnMenuEncrypt) {
      hSettings.setVisible(true);
      hEncrypt.setVisible(true);
      hDecrypt.setVisible(false);
      cbDecryptionMethod.setVisible(false);

    } else if (event.getTarget() == btnMenuDecrypt) {
      hSettings.setVisible(true);
      hEncrypt.setVisible(false);
      hDecrypt.setVisible(true);
      cbDecryptionMethod.setVisible(true);

    } else if (event.getTarget() == btnClear || event.getTarget().toString().contains("Clear")) {
      taPlainTextEncrypt.setText("");
      taPlainTextDecrypt.setText("");
      tfKeyEncrypt.setText("");
      tfKeyDecrypt.setText("");
      taCipherTextEncrypt.setText("");
      taCipherTextDecrypt.setText("");

    } else if (event.getTarget() == btnEncryption || event.getTarget().toString().contains("Encrypt")) {
      if (!GeneralUtils.validateKey(tfKeyEncrypt.getText())) {

        final Stage dialogStage = new Stage();
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        final Scene scene = new Scene(grid, 300, 150);
        dialogStage.setScene(scene);
        dialogStage.setTitle("Error");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        final Label lab_alert = new Label("Problems with the key!");
        grid.add(lab_alert, 0, 1);

        final Button btbOk = new Button("Ok I got it ;) I will fix");
        btbOk.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(final ActionEvent arg0) {
            dialogStage.hide();
          }
        });
        grid.add(btbOk, 0, 2);
        dialogStage.show();

      } else {
      adaptedText = GeneralUtils.adaptString(taPlainTextEncrypt.getText());
        adaptedKey = GeneralUtils.adaptString(tfKeyEncrypt.getText());
      String result = CipherUtils.encrypt(adaptedText, adaptedKey);
      taCipherTextEncrypt.setText(result);
      }

    } else if (event.getTarget() == btnDecryption || event.getTarget().toString().contains("Decrypt")) {
      adaptedText = GeneralUtils.adaptString(taCipherTextDecrypt.getText());
      decryptMethod = cbDecryptionMethod.getValue().equals(FAST_METHOD) ? DecryptMethod.FAST_METHOD
            : cbDecryptionMethod.getValue().equals(DECRYPT_AND_EVALUATE_METHOD)
                  ? DecryptMethod.DECRYPT_AND_EVALUATE_METHOD
                  : cbDecryptionMethod.getValue().equals(MIXED_METHOD) ? DecryptMethod.MIXED_METHOD : null;
      final CryptanalysisWrapper result = CipherUtils.decrypt(adaptedText, decryptMethod);
      tfKeyDecrypt.setText(result.getKey());
      taPlainTextDecrypt.setText(result.getText());
    }
  }
}
