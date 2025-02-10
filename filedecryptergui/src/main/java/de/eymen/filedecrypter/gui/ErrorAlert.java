package de.eymen.filedecrypter.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorAlert implements Alerts {

    Alert a;

    @Override
    public void show(String error, String titel) {
        a = new Alert(AlertType.ERROR);
        a.setTitle(titel);
        a.setContentText(error);

        a.show();
    }

}
