package de.eymen.filedecrypter;

import java.io.IOException;

import de.eymen.filedecrypter.domain.InputGUIHandler;
import de.eymen.filedecrypter.domain.RegistrationHandler;
import de.eymen.filedecrypter.gui.Alerts;
import de.eymen.filedecrypter.gui.ErrorAlert;
import de.eymen.filedecrypter.gui.InfoAlert;
import de.eymen.filedecrypter.gui.WarningAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    TextField userField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField passwordField2;
    @FXML
    CheckBox notarCheck;
    InputGUIHandler iHandler;

    @FXML
    private void login(ActionEvent e) throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void register(ActionEvent e) {

        Alerts alerts;

        String usernameInput = userField.getText();

        String passwordInput = passwordField.getText();

        Boolean notar = notarCheck.isSelected();
        iHandler = new InputGUIHandler();

        int returncode = iHandler.registerCheck(passwordInput, passwordInput, usernameInput);

        if (returncode == 0) {
            RegistrationHandler rH = new RegistrationHandler();
            try {

                rH.registerNewUser(usernameInput, passwordInput, notar);
                passwordInput = null;
                alerts = new InfoAlert();
                alerts.show("Your User was registered", "Sucess");

            } catch (Exception ex) {
                passwordInput = null;
                ex.printStackTrace();
            }

        } else if (returncode == 1) {
            alerts = new ErrorAlert();

            alerts.show("Passwords are not identical", "Try again");
        } else if (returncode == 2) {
            alerts = new WarningAlert();
            alerts.show("This Username is already taken", "Try again");
        }

    }

}
