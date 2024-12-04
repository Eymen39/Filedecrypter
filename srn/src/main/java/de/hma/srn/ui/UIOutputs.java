package de.hma.srn.ui;

public class UIOutputs {
    String errorColor = KonsolenFarbe.ERROR.getCode();
    String resetColor = KonsolenFarbe.RESET.getCode();
    String loginColor = KonsolenFarbe.LOGIN.getCode();

    public void registerPasswordError() {
        System.out.println(errorColor + "Die Passwoerter stimmen nicht ueberein"
                + resetColor);
    }

    public void registerNameError() {
        System.out.println(errorColor + "Dieser Benutzername ist schon vergeben"
                + resetColor);

    }

    public void registerInputErrors() {
        System.out.println(
                errorColor + "register muss alleine stehen" + resetColor);
    }

    public void addFileOutput(int returnCode, String datei) {

        switch (returnCode) {
            case 0 -> {
                System.out.printf("Die Datei %s wurde in erfolgreich verschlüsselt und abgelegt \n", datei);

            }
            case 1 -> {
                dataCorrupt();
            }
            case 2 -> {

                System.out.println(errorColor + "Diese Datei existiert nicht" + loginColor);
            }
            case 3 -> {
                System.out.println(errorColor + "Falsche eingabe des Befehls" + loginColor);

            }

        }

    }

    public void readFileOutput(int returnCode, String datei, String dateiablage) {

        switch (returnCode) {
            case 0 -> {
                System.out.printf("Die Datei %s wurde in %s gelegt\n", datei, dateiablage);

            }
            case 1 -> {
                System.out.println(errorColor
                        + "Die Angegebene Datei wurde nicht gefunden geben sie ls ein und überprüfen sie die Datei auf denen sie Rechte haben");
            }
            case 2 -> {

                System.out.println(errorColor + "Das Password ist falsch");
            }
            case 3 -> {

                System.out.println(errorColor + "Es ist wohl etwas schief gelaufen versuchen sie es erneut");

            }
            case 4 -> {
                System.out.println(errorColor + "Sie haben keine Rechte für diese Datei");

            }

        }

    }

    public void loginInputError() {
        System.out
                .println(errorColor + "Login muss alleine stehen " + resetColor);
    }

    public void notarRegisterError() {
        System.out.println(errorColor + "Die PublicKey stimmen nicht überein" + resetColor);
    }

    public void addUserToFileUser(int error, String user, String dateiPfad) {
        switch (error) {

            case 0 -> {
                System.out.printf("Der Nutzer %s hat lese Berechtigung zur Datei %s \n", user, dateiPfad);

            }
            case 1 -> {
                System.out.println(errorColor + "Diese Datei gehört ihnen nicht" + loginColor);

            }
            case 2 -> {
                System.out.printf(errorColor + "Der Nutzer %s existiert nicht" + loginColor + "\n", user);
            }
            case 3 -> {
                System.out.println(errorColor + "Das Password ist falsch" + loginColor);

            }
            case 4 -> {
                System.out.printf(errorColor + "Die Datei %s existiert nicht\n" + loginColor, dateiPfad);
            }
        }

    }

    public void removeUserFromFile(int error, String user, String dateiPfad) {
        switch (error) {

            case 0 -> {
                System.out.printf("Der Nutzer %s hat die Leseberechtigung zur Datei %s verloren \n", user, dateiPfad);

            }
            case 1 -> {
                System.out.println(errorColor + "Sie sind nicht der Eigentümer dieser Datei" + loginColor);

            }
            case 2 -> {
                System.out.printf(errorColor + "Der Nutzer %s existiert nicht oder hatte keine Rechte auf der Datei %s"
                        + loginColor + "\n", user, dateiPfad);
            }
            case 3 -> {
                System.out.printf(errorColor + "Die Datei %s existiert nicht\n" + loginColor, dateiPfad);

            }
        }

    }

    public void wrongPassword() {
        System.out.println(errorColor + "Das Password is Inkorrekt " + loginColor);
    }

    public void dataCorrupt() {
        System.out.println(errorColor + "Julius hier muss data Corrupt kommen");
    }

    public void userNotExist() {
        System.out.println(errorColor + "Ein Nutzer mit diesem Namen existiert nicht" + resetColor);
    }

    public void tryAgain() {
        System.out.println(errorColor + "Etwas ist schiefgelaufen probieren sie es erneut" + loginColor);

    }

    public void registerNameEmpty() {
        System.out.println(errorColor + "Der Benutzername ist nicht eingegeben worden" + loginColor);
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

}
