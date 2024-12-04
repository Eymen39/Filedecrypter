package de.hma.srn.ui;

import java.io.Console;
import de.hma.srn.domain.ConsoleHandler;
import de.hma.srn.domain.DataAccessHandler;
import de.hma.srn.domain.UserSingleton;
import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;

public class ConsoleMenu {
    ConsoleHandler cmd;
    UIOutputs uiOutputs = new UIOutputs();
    BigBrainCipher bbc;
    String mainColor = KonsolenFarbe.RESET.getCode();

    public ConsoleMenu(String osName) {
        this.cmd = new ConsoleHandler();
    }

    public void terminal() {
        Console console = System.console();
        if (console == null) {
            System.out.println("Konsole ist null");
        }
        console.printf("willkommen\n");
        Boolean notarExists = false;

        while (!notarExists) {
            if (!cmd.checkIfNotarExists()) {
                console.printf(KonsolenFarbe.COMMAND.getCode()
                        + "Es existiert noch kein Notar bitte geben sie den Public Key des Notaren an\n");
                String publicKeyNotar = console.readLine();
                console.printf(KonsolenFarbe.COMMAND.getCode() + "geben sie den Publickey nocheinmal ein\n");
                String publicKeyAgain = console.readLine();
                if (cmd.registerCheck(publicKeyNotar, publicKeyAgain, "Notar") == 0) {
                    cmd.registerNotar(publicKeyAgain);
                } else {
                    uiOutputs.notarRegisterError();
                }

            } else {
                notarExists = true;
            }
        }

        while (true) {

            bbc = new BigBrainCipher();
            String input = console.readLine();
            String[] worte = input.split("\\s+");

            if (worte.length == 0) {
                System.out.println(mainColor + "Geben sie bitte help ein" + mainColor);
            } else {

                String befehl = worte[0];

                switch (befehl.toLowerCase()) {

                    case "login" -> {
                        if (!UserSingleton.getInstance().getUser().getId().equals("")) {
                            System.out.println(
                                    "Sie sind schon angemeldet wollen sie sich abmelden und fortfahren? (Y/N)");
                            if (console.readLine().toLowerCase().equals("y")) {
                                cmd.logout();
                                mainColor = KonsolenFarbe.RESET.getCode();
                            } else if (console.readLine().toLowerCase().equals("n")) {

                            }
                        }
                        if (worte.length > 1) {
                            uiOutputs.loginInputError();
                        } else {
                            console.printf(mainColor + "Geben sie ihr Benutzername ein\n" + mainColor);
                            String userName = console.readLine();

                            console.printf(mainColor + "Geben sie ihr Password ein\n" + mainColor);
                            String password = new String(console.readPassword());

                            if (cmd.login(userName, password)) {
                                password = null;
                                mainColor = KonsolenFarbe.LOGIN.getCode();

                            } else {
                                password = null;
                                uiOutputs.loginError();
                            }
                        }
                    }

                    case "logout" -> {
                        cmd.logout();
                        mainColor = KonsolenFarbe.RESET.getCode();
                    }

                    case "register" -> {

                        if (worte.length > 1) {
                            uiOutputs.registerInputErrors();
                        } else {

                            console.printf(mainColor + "Geben sie einen Benutzernamen ein\n");
                            String userNamer = console.readLine();

                            console.printf(mainColor + "Geben sie ihr Password ein\n");
                            String password = new String(console.readPassword());

                            console.printf(mainColor + "Geben sie das Password erneut ein\n");
                            String passwordagain = new String(console.readPassword());

                            switch (cmd.registerCheck(password, passwordagain, userNamer)) {

                                case 0 -> {

                                    System.out.println(
                                            "wollen sie das ihre Daten mithilfe eines Notars gesichert werden Y/N");
                                    Boolean notarFlag = false;
                                    String notarFlagentscheidung = console.readLine();
                                    if (notarFlagentscheidung.equals("y")) {
                                        notarFlag = true;
                                        console.printf(mainColor + "mit Notar arbeiten\n");

                                    } else if (notarFlagentscheidung.equals("n")) {
                                        notarFlag = false;
                                        console.printf(mainColor + "ohne Notar arbeiten\n");

                                    }

                                    cmd.register(userNamer, password, notarFlag);

                                    mainColor = KonsolenFarbe.LOGIN.getCode();

                                    password = null;
                                    passwordagain = null;

                                }

                                case 1 -> {
                                    uiOutputs.registerPasswordError();
                                    password = null;
                                    passwordagain = null;
                                }
                                case 2 -> {
                                    uiOutputs.registerNameError();
                                    password = null;
                                    passwordagain = null;

                                }
                                case 3 -> {
                                    uiOutputs.registerNameEmpty();
                                    password = null;
                                    passwordagain = null;
                                }

                            }

                        }

                    }

                    case "addfile" -> {

                        if (worte.length == 2) {
                            DataAccessHandler dAH = new DataAccessHandler();
                            if (UserSingleton.getInstance().getUser().getId().equals("")) {
                                System.out.println("sie müssen dafür angemeldet sein");
                            } else {
                                uiOutputs.addFileOutput(dAH.addFile(worte[1]), worte[1]);

                            }
                        } else {
                            uiOutputs.addFileOutput(3, "");
                        }
                    }

                    case "help" -> {

                        cmd.help();

                    }
                    case "readfile" -> {
                        if (worte.length == 3) {

                            if (UserSingleton.getInstance().getUser().getId().equals("")) {
                                System.out.println("sie müssen dafür angemeldet sein");
                            } else {

                                System.out.println("Geben sie ihr Password ein");
                                String password = new String(console.readPassword());

                                cmd.readFile(worte[1], password, worte[2]);
                                password = null;
                            }

                        } else {
                            System.out.println(
                                    "Geben sie readfile und danach den originalen Pfad der zu lesenden Datei und danach den Ablageort ein");
                        }

                    }
                    case "ls" -> {
                        if (UserSingleton.getInstance().getUser().getId().equals("")) {
                            System.out.println("Melden sie sich zuerst an");
                        } else {
                            System.out.println("geben sie ihr Password ein");
                            String password = new String(console.readPassword());
                            if (UserSingleton.getInstance().getUser().getPassword()
                                    .equals(bbc.hash(password.getBytes()))) {

                                System.out.println("--------------------");
                                for (String s : cmd.listData(password)) {
                                    password = null;
                                    System.out.println(s);
                                }

                            } else {
                                System.out.println("Das Password war falsch");
                            }
                        }

                    }
                    case "adduser" -> {
                        if (UserSingleton.getInstance().getUser().getId().equals("")) {
                            System.out.println("Melden sie sich zuerst an");
                        } else {
                            if (worte.length != 3) {
                                System.out.println(mainColor
                                        + "Geben sie adduser und danach den nutzer und danach den originalpfad der Datei an");
                            } else {
                                String userToAdd = worte[1];
                                String fileToAdd = worte[2];
                                System.out.println("Geben sie bitte ihr Password ein");
                                String password = new String(console.readPassword());
                                cmd.addUsertoFile(userToAdd, fileToAdd, password);
                                password = null;

                            }
                        }

                    }
                    case "removeuser" -> {
                        if (UserSingleton.getInstance().getUser().getId().equals("")) {
                            System.out.println("Melden sie sich zuerst an");
                        } else {
                            if (worte.length != 3) {
                                System.out.println(mainColor
                                        + "Geben sie removeuser und danach den nutzer und danach den originalpfad der Datei an");
                            } else {
                                String userToAdd = worte[1];
                                String fileToAdd = worte[2];
                                cmd.removeUserFromFile(userToAdd, fileToAdd);
                            }
                        }

                    }
                    default -> {
                        System.out.println("mit help bekommen sie alle Befehle angezeigt");
                    }

                }

            }

        }
    }

}
