package de.eymen.filedecrypter;

import java.io.IOException;

import de.eymen.filedecrypter.domain.InputGUIHandler;
import de.eymen.filedecrypter.gui.Alerts;
import de.eymen.filedecrypter.gui.WarningAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    TextField UsernameLogin;

    @FXML
    PasswordField PasswordLogin;

    @FXML
    private void register() throws IOException {
        App.setRoot("register");
    }

    @FXML
    private void login(ActionEvent e) {
        InputGUIHandler iHandler = new InputGUIHandler();
        boolean loginSuccess = iHandler.login(UsernameLogin.getText(), PasswordLogin.getText());

        Alerts a;
        if (loginSuccess) {
            System.out.println("login sucessful");
            try {
                App.setRoot("mainmenu");

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            a = new WarningAlert();
            a.show("Username or Password was Wrong", "Try again");

            System.out.println("Login not");
        }
    }
}