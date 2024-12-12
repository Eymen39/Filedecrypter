package de.eymen.filedecrypter.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.Key;

/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLACK);

        // Image bild = new Image("Screenshot 2023-04-20 190137.png");
        // stage.getIcons().add(bild);
        stage.setTitle("Stage Demo Programm");
        stage.setWidth(420);
        stage.setHeight(420);
        stage.setResizable(false);
        // stage.setX(50);
        // stage.setX(50);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("You cant escape bitch q");
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));
        stage.setScene(scene);
        stage.show();
    }

}