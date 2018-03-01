package com.concordia.SubstitutionCipher.frontend;

import java.net.URL;
import java.util.ResourceBundle;

import com.concordia.SubstitutionCipher.utils.CipherUtils;
import com.concordia.SubstitutionCipher.utils.GeneralUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SubstitutionController implements Initializable {

  @FXML
  private ImageView btnExit, btnEncrypt, btnDecrypt;

  @FXML
  private AnchorPane hSettings, hEncrypt, hDecrypt;

  @FXML
  private Button btnClear, btnDecryption, btnEncryption;

  @FXML
  private TextField tfKey;

  @FXML
  private TextArea taPlainText, taCipherText;

  private String adaptedText, adaptedKey;

  @FXML
  private void handleButtonAction(MouseEvent event) {
    if (event.getTarget() == btnExit) {
      System.exit(0);

    } else if (event.getTarget() == btnEncrypt) {
      hSettings.setVisible(true);
      hEncrypt.setVisible(true);
      hDecrypt.setVisible(false);

    } else if (event.getTarget() == btnDecrypt) {
      hSettings.setVisible(true);
      hEncrypt.setVisible(false);
      hDecrypt.setVisible(true);

    } else if (event.getTarget() == btnClear) {
      taPlainText.setText(" ");
      tfKey.setText(" ");
      taCipherText.setText(" ");

    } else if (event.getTarget() == btnClear) {
      taPlainText.setText(" ");
      taCipherText.setText(" ");
      tfKey.setText(" ");

    } else if (event.getTarget() == btnEncryption) {
      adaptedText = GeneralUtils.adaptString(taPlainText.getText());
      adaptedKey = GeneralUtils.adaptString(tfKey.getText());
      GeneralUtils.validate(adaptedText, adaptedKey);
      taCipherText.setText(CipherUtils.encrypt(adaptedText, adaptedKey));

    } else if (event.getTarget() == btnDecryption) {

    }
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // TODO
  }

}
