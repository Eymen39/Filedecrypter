package de.eymen.filedecrypter.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class WarningAlert implements Alerts {

    @Override
    public void show(String msg, String titel) {

        Alert a = new Alert(AlertType.WARNING);
        a.setTitle(titel);
        a.setContentText(msg);
        a.show();
    }

}
