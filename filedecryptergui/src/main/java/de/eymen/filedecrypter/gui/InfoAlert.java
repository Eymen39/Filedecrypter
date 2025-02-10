package de.eymen.filedecrypter.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InfoAlert implements Alerts {

    @Override
    public void show(String msg, String titel) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setContentText(msg);
        a.setTitle(titel);
        a.show();
    }

}
