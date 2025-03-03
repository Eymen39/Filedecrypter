package de.eymen.filedecrypter;

import de.eymen.filedecrypter.domain.DataAccessHandler;
import de.eymen.filedecrypter.domain.InputGUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainMenuController {

    @FXML
    VBox vBox;

    @FXML
    ScrollPane scrollPane;
    @FXML
    Button addFileBtn;

    InputGUIHandler guiHandler;

    public void initialize() {
        guiHandler = new InputGUIHandler();
        guiHandler.listData("a");

        // vBox.prefWidthProperty().bind(scrollPane.widthProperty());
        /*
         * for (String s : ) {
         * Label label = new Label(s);
         * vBox.getChildren().add(label);
         * 
         * }
         */
    }

    public void addFile() {

        DataAccessHandler dHandler = new DataAccessHandler();
        dHandler.addFile(null);
    }

}
