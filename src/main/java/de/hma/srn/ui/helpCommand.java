package de.hma.srn.ui;

public class helpCommand {

    public void ShowAllCmd() {

        String registerString = "register: geben sie register ohne einen anhang an und geben sie danach eine Einzigartigen Namen und ihr Password ein welches sie immer beim anmelden benötigen werden";
        String loginString = "login: geben sie login ohne anhang an um sich mit ihrem Benutzernamen und Password anzumelden";
        String addfileString = "addfile: geben sie addfile so an um Datein zuverschlüsslen und sicher abzulegen [addfile <dateipfad>]";
        String readFileString = "readfile: geben sie readfile an um eine Datei zu entschlüsseln und angezeigt zu bekommen.";
        String readFileString2 = "Falls sie diese Datei verschlüsselt haben können sie diese nach Bestätigung an einen bestimmten Ort ablegen [readfile <orginalDateiPfad>]";
        String logoutString = "logout: um sich mit dem aktuel angemeldeten Benutzer abzumelden";
        String listDataString = "ls: die berechtigten Datein des aktuellen Benutzers aufzulisten";
        String addUserString = "adduser: gibt einem Nutzer die Berechtigung eine bestimtme angegebene Datei zu lesen[adduser <user> <dateipfad>]";
        String removeUserString = "removeuser: nehmt einem Nutzer die Berechtigung eine bestimtme angegebene Datei zu lesen [removeuser <user> <dateipfad>]";
        String showusersString = "showusers: gibt alle auf diesem System registrierten Leute zurück";
        String deleteFileString = "deletefile: sie können damit die angegebene verschlüsselte Datei löschen und alle Berechtigungen zu dieser Datei die von ihnen vergeben wurden entfernen. ";

        String format = "%-10s | %-50s%n";

        // Überschrift
        System.out.println("Verfügbare Befehle:");
        System.out.println("=".repeat(120));
        System.out.printf(format, "Befehl", "Beschreibung");
        System.out.println("-".repeat(120));

        // Befehle ausgeben
        System.out.printf(format, "register", registerString);
        System.out.printf(format, "login", loginString);
        System.out.printf(format, "addfile", addfileString);
        System.out.printf(format, "readfile", readFileString);
        System.out.printf(format, " ", readFileString2);
        System.out.printf(format, "logout", logoutString);
        System.out.printf(format, "ls", listDataString);
        System.out.printf(format, "adduser", addUserString);
        System.out.printf(format, "removeuser", removeUserString);
        System.out.printf(format, "showusers", showusersString);
        System.out.printf(format, "deletefile", deleteFileString);

        System.out.println("=".repeat(120));

    }

}
