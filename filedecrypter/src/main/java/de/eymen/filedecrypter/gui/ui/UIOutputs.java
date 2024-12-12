package de.eymen.filedecrypter.gui.ui;

import java.util.ArrayList;

import de.eymen.filedecrypter.gui.domain.domain.Crasher;

public class UIOutputs {
    String errorColor = KonsolenFarbe.ERROR.getCode();
    String resetColor = KonsolenFarbe.RESET.getCode();
    String loginColor = KonsolenFarbe.LOGIN.getCode();
    String helpColor = KonsolenFarbe.COMMAND.getCode();

    public void registerPasswordError() {
        System.out.println(errorColor + "Die Passwoerter stimmen nicht ueberein"
                + resetColor);
    }

    public void registerNameError() {
        System.out.println(errorColor + "Dieser Benutzername ist schon vergeben"
                + resetColor);

    }

    public void registerInputErrors(String maincolString) {
        System.out.println(
                errorColor + "register muss alleine stehen" + maincolString);
    }

    public void addFileOutput(int returnCode, String datei, String farbe) {

        switch(returnCode){case 0->{System.out.printf("Die Datei %s wurde erfolgreich verschlüsselt und abgelegt \n",datei);

        }case 1->{Crasher crasher=new Crasher();crasher.systemCrasher();}case 2->{

        System.out.println(errorColor+"Diese Datei existiert nicht"+farbe);}case 3->{

        System.out.println(errorColor+"Diese Datei wurde schon einmal angelegt"+loginColor);}case 4->{System.out.println(errorColor+"Falsche eingabe des Befehls"+farbe);

        }

        }

    }

    public void readFileOutput(int returnCode, String datei) {

        switch(returnCode){case 0->{System.out.printf("Die Datei %s wurde geöffnet \n",datei);

        }case 1->{System.out.println(errorColor+"Die Angegebene Datei wurde nicht gefunden geben sie ls ein und überprüfen sie die Datei auf denen sie Rechte haben"+loginColor);}case 2->{

        System.out.println(errorColor+"Das Password ist falsch"+loginColor);}case 3->{

        System.out.println(errorColor+"Es ist wohl etwas schief gelaufen versuchen sie es erneut"+loginColor);

        }case 4->{System.out.println(errorColor+"Sie haben keine Rechte für diese Datei"+loginColor);

        }

        }

    }

    public void saveFileOutput(int returnCode, String datei, String ablageOrt) {

        switch(returnCode){case 0->{System.out.printf("Die Datei %s wurde in %s abgelegt \n",datei,ablageOrt);

        }case 1->{System.out.println(errorColor+"Die Angegebene Datei wurde nicht gefunden geben sie ls ein und überprüfen sie die Datei auf denen sie Rechte haben"+loginColor);}case 2->{

        System.out.println(errorColor+"Das Password ist falsch"+loginColor);}case 3->{

        System.out.println(errorColor+"Es ist wohl etwas schief gelaufen versuchen sie es erneut"+loginColor);

        }case 4->{System.out.println(errorColor+"Sie haben keine Rechte für diese Datei"+loginColor);

        }case 5->{System.out.println(errorColor+"Dieser Pfad existiert nicht"+loginColor);}

        }

    }

    public void loginInputError(String mainColor) {
        System.out
                .println(errorColor + "Login muss alleine stehen " + mainColor);
    }

    public void notarRegisterError() {
        System.out
                .println(errorColor + "Sie haben keinen Notar angegeben. Das Programm wird geschlossen." + resetColor);
    }

    public void addUserToFileUser(int error, String user, String dateiPfad) {
        switch(error){

        case 0->{System.out.printf("Der Nutzer %s hat lese Berechtigung zur Datei %s \n",user,dateiPfad);

        }case 1->{System.out.println(errorColor+"Diese Datei gehört ihnen nicht"+loginColor);

        }case 2->{System.out.printf(errorColor+"Der Nutzer %s existiert nicht"+loginColor+"\n",user);}case 3->{System.out.println(errorColor+"Das Password ist falsch"+loginColor);

        }case 4->{System.out.printf(errorColor+"Die Datei %s existiert nicht\n"+loginColor,dateiPfad);}case 5->{System.out.printf(errorColor+"Der User hat schon Rechte auf die Datei %s \n"+loginColor,dateiPfad);

        }}

    }

    public void removeUserFromFile(int error, String user, String dateiPfad) {
        switch(error){

        case 0->{System.out.printf("Der Nutzer %s hat die Leseberechtigung zur Datei %s verloren \n",user,dateiPfad);

        }case 1->{System.out.println(errorColor+"Sie sind nicht der Eigentümer dieser Datei"+loginColor);

        }case 2->{System.out.printf(errorColor+"Der Nutzer %s existiert nicht oder hatte keine Rechte auf der Datei %s"+loginColor+"\n",user,dateiPfad);}case 3->{System.out.printf(errorColor+"Die Datei %s existiert nicht\n"+loginColor,dateiPfad);

        }}

    }

    public void wrongPassword() {
        System.out.println(errorColor + "Das Password is Inkorrekt " + loginColor);
    }

    public void userNotExist() {
        System.out.println(errorColor + "Ein Nutzer mit diesem Namen existiert nicht" + resetColor);
    }

    public void tryAgain() {
        System.out.println(errorColor + "Etwas ist schiefgelaufen probieren sie es erneut" + loginColor);

    }

    public void registerNameEmpty() {
        System.out.println(errorColor + "Der Benutzername ist nicht eingegeben worden" + resetColor);
    }

    public void loginError() {
        System.out.println(errorColor + "Username oder Password stimmt nicht" + resetColor);
    }

    public void logoutError() {
        System.out.println(errorColor + "Sie sind nicht angmeldet" + resetColor);
    }

    public void logoutSuccess() {
        System.out.println("Sie sind abgemeldet" + resetColor);
    }

    public void loginSuccess() {
        System.out.println("Sie sind angemeldet" + loginColor);
    }

    public void registerSuccess() {
        System.out.println("Sie sind erfolgreich registriert");

    }

    public void showUsers(ArrayList<String> users) {

        System.out.println("Es sind folgende Nutzer auf diesem Programm registiert");
        for (String s : users) {

            System.out.println(s);

        }

    }

    public void deleteFileOutput(int code, String dateString) {
        switch(code){

        case 0->{System.out.printf("Die verschlüsselte Datei %s wurde erfolgreich Gelöscht\n",dateString);

        }case 1->{System.out.printf(errorColor+"Bei der Entfernung der Datei %s ist ein Fehler aufgetretten\n"+loginColor,dateString);}case 2->{System.out.printf(errorColor+"Sie haben diese Datei nicht verschlüsselt\n"+loginColor,dateString);

        }}
    }

    public void pemFileError(String dateString) {
        System.out.printf(errorColor + "Der Pfad %s der .pem Datei ist nicht gültig\n", dateString);
    }

    public void cantBeLoggedInError() {
        System.out.println(errorColor + "Sie müssen sich abmelden für diese Aktion" + resetColor);
    }

}
