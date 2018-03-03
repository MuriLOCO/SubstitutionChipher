package com.concordia.SubstitutionCipher;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

  public static void main(String[] args) throws IOException {
    SpringApplication.run(SubstitutionCipherApplication.class, args);
    launch(args);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
      
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
}
