package de.eymen.filedecrypter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import de.eymen.filedecrypter.persistence.DbClient;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("register"));
        stage.setScene(scene);
        stage.setTitle("FileCrypter");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        DbClient dbClient = DbClient.getInstance();
        try {
            dbClient.createUserTable();
            dbClient.createDataAccessTable();
            dbClient.createPublicKeyTable();
            dbClient.createSignedPublicKeyTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        launch();
    }

}