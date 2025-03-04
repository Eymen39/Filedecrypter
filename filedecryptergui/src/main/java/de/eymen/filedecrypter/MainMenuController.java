package de.eymen.filedecrypter;

import de.eymen.filedecrypter.domain.DataAccessHandler;
import de.eymen.filedecrypter.domain.FileChooser;
import de.eymen.filedecrypter.domain.InputGUIHandler;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.File;

import javax.swing.*;

public class MainMenuController {

    @FXML
    VBox vBox;

    @FXML
    ScrollPane scrollPane;
    @FXML
    Button addFileBtn;

    static InputGUIHandler guiHandler;

    public void initialize() {
        guiHandler = new InputGUIHandler();

        vBox.prefWidthProperty().bind(scrollPane.widthProperty());

        for (String s : guiHandler.listData("a")) {

            Label label = new Label(s);
            label.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    showTextInputPopup(s);
                }

            });
            vBox.getChildren().add(label);

        }

    }

    private static void readfile(String datei, String password) {
        System.out.println(guiHandler.readFile(datei, password));
    }

    public static void showTextInputPopup(String datei) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("enter Password ");
        dialog.setContentText("to open this File you need to input your Password");

        dialog.showAndWait().ifPresent(input -> {
            if (!input.isEmpty()) {
                readfile(datei, input);
            } else {
                showPopup("Leere Eingabe", "Du hast nichts eingegeben.");
            }
        });
    }

    public static void showPopup(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addFile() {
        FileChooser fileChooser = new FileChooser();

        guiHandler.addfile(fileChooser.openFileChooser());
    }

}
