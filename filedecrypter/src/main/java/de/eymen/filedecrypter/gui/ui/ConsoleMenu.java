package de.eymen.filedecrypter.gui.ui;

import java.io.Console;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import de.eymen.filedecrypter.gui.domain.domain.ConsoleInputHandler;
import de.eymen.filedecrypter.gui.domain.domain.CustomParser;
import de.eymen.filedecrypter.gui.domain.domain.DataAccessHandler;
import de.eymen.filedecrypter.gui.domain.domain.UserSingleton;
import de.eymen.filedecrypter.gui.domain.domain.BigBrainCipher.BigBrainCipher;

public class ConsoleMenu {
    ConsoleInputHandler cmd;
    UIOutputs uiOutputs = new UIOutputs();
    BigBrainCipher bbc;
    LineReader reader;
    Terminal terminal;
    String mainColor = KonsolenFarbe.RESET.getCode();
    Boolean isLoggedIn = false;
    Supplier<Boolean> LoggedInStatus = () -> isLoggedIn;

    public ConsoleMenu() {
        this.cmd = new ConsoleInputHandler();
        this.cmd = new ConsoleInputHandler();
    }

    public void terminal() {
        CustomParser parser = new CustomParser();
        parser.setEscapeChars(null);
        terminal = null;
        try {
            terminal = TerminalBuilder.builder().system(true).jna(true).build();

        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = LineReaderBuilder.builder().terminal(terminal).parser(parser).build();
        // CustomCompleter customCompleter = new CustomCompleter(LoggedInStatus);
        Set<String> commands = new HashSet<>();
        commands.add("register");
        commands.add("login");
        commands.add("logout");
        commands.add("addfile");
        commands.add("readfile");
        commands.add("adduser");
        commands.add("removeuser");
        commands.add("deletefile");
        commands.add("showusers");
        commands.add("ls");
        commands.add("help");
        commands.add("exit");
        Completer completer = new StringsCompleter(commands);

        ((LineReaderImpl) reader).setCompleter(completer);

        Console console = System.console();
        if (console == null) {
            System.out.println("Konsole ist null");
        }
        console.printf("willkommen\n");
        Boolean notarExists = false;

        while (!notarExists) {
            if (!cmd.checkIfNotarExists()) {
                console.printf(KonsolenFarbe.COMMAND.getCode()
                        + "Es existiert noch kein Notar bitte geben sie eine .pem Datei die den PublicKey des Notaren enthält ein(Dieser Key muss PKCS#8 und das Schlüsselpaar muss 4096 Bit enthalten) \n");
                String pemFilePfad = console.readLine();
                console.printf(KonsolenFarbe.COMMAND.getCode()
                        + "Sind sie sich sicher das diese Datei richtig ist? [Y/N](default N): \n");
                if (console.readLine().toLowerCase().equals("y")) {
                    cmd.registerNotar(pemFilePfad);
                } else {
                    uiOutputs.notarRegisterError();
                    System.exit(0);
                }

            } else {
                notarExists = true;
            }
        }

        while (true) {

            bbc = new BigBrainCipher();
            String input = reader.readLine(">>> ");

            String[] worte = input.split("\\s+");

            if (worte.length == 0) {
                System.out.println(KonsolenFarbe.COMMAND.getCode() + "Geben sie bitte help ein" + mainColor);
            } else {

                String befehl = worte[0];

                switch (befehl.toLowerCase()) {

                    case "login" -> {

                        if (worte.length > 1) {
                            uiOutputs.loginInputError(mainColor);
                        } else {

                            if (alreadyLoggedIn()) {

                                String userName = reader
                                        .readLine(mainColor + "Geben sie ihr Benutzername ein: " + mainColor);

                                String password = reader.readLine(
                                        KonsolenFarbe.COMMAND.getCode() + "Geben sie ihr Password ein: " + mainColor,
                                        '*');

                                if (cmd.login(userName, password)) {
                                    password = null;
                                    mainColor = KonsolenFarbe.LOGIN.getCode();
                                    isLoggedIn = true;

                                } else {
                                    password = null;
                                    uiOutputs.loginError();
                                }
                            } else {
                                alreadyLoggedIn();
                            }
                        }
                    }

                    case "logout" -> {
                        cmd.logout();
                        mainColor = KonsolenFarbe.RESET.getCode();
                        isLoggedIn = false;
                    }

                    case "register" -> {

                        if (worte.length > 1) {
                            uiOutputs.registerInputErrors(mainColor);
                        } else {
                            if (alreadyLoggedIn()) {

                                String userNamer = reader.readLine(mainColor + "Geben sie einen Benutzernamen ein: ");

                                String password = reader.readLine(mainColor + "Geben sie ihr Password ein: ", '*');

                                String passwordagain = reader.readLine(
                                        KonsolenFarbe.COMMAND.getCode() + "Geben sie das Password erneut ein: ", '*');

                                switch (cmd.registerCheck(password, passwordagain, userNamer)) {

                                    case 0 -> {

                                        Boolean notarFlag = false;
                                        String notarFlagentscheidung = reader.readLine(KonsolenFarbe.COMMAND.getCode()
                                                + "wollen sie das ihre Daten mithilfe eines Notars gesichert werden [Y/N](default N): "
                                                + mainColor).toLowerCase();
                                        if (notarFlagentscheidung.equals("y")) {
                                            notarFlag = true;
                                            System.out.println(
                                                    mainColor + "Es wird mit dem Notar gearbeitet" + mainColor);

                                        } else {
                                            notarFlag = false;
                                            System.out.println(mainColor + "Es wird ohne Notar gearbeitet" + mainColor);
                                        }
                                        cmd.register(userNamer, password, notarFlag);
                                        mainColor = KonsolenFarbe.LOGIN.getCode();
                                        password = null;
                                        passwordagain = null;
                                        isLoggedIn = true;
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

                    }

                    case "addfile" -> {

                        if (worte.length == 2) {
                            DataAccessHandler dAH = new DataAccessHandler();
                            if (loginRequired()) {

                                uiOutputs.addFileOutput(dAH.addFile(worte[1]), worte[1], mainColor);

                            }
                        } else {
                            uiOutputs.addFileOutput(4, "", mainColor);
                        }
                    }

                    case "help" -> {

                        cmd.help();

                    }
                    case "readfile" -> {
                        if (worte.length == 2) {

                            if (loginRequired()) {

                                String dateiPfad = worte[1];

                                String password = reader
                                        .readLine(mainColor + "Geben sie ihr Password ein: " + mainColor, '*');

                                int returnCode = cmd.readFile(dateiPfad, password);
                                if (returnCode == 0) {
                                    if (reader.readLine(KonsolenFarbe.COMMAND.getCode()
                                            + "wollen sie die Datei irgendwo ablegen? [Y/N](default N): " + mainColor)
                                            .toLowerCase().equals("y")) {
                                        String ablageOrt = reader.readLine(KonsolenFarbe.COMMAND.getCode()
                                                + "Wo wollen sie die Datei ablegen?" + mainColor);
                                        cmd.saveFileTo(dateiPfad, password, ablageOrt);
                                    }
                                }
                                password = null;
                                dateiPfad = null;
                            }

                        } else {
                            System.out.println(KonsolenFarbe.ERROR.getCode()
                                    + "Geben sie readfile und danach den originalen Pfad der zu lesenden Datei ein"
                                    + mainColor);
                        }

                    }
                    case "ls" -> {
                        if (loginRequired()) {

                            String password = reader.readLine(
                                    KonsolenFarbe.COMMAND.getCode() + "geben sie ihr Password ein: " + mainColor, '*');
                            if (UserSingleton.getInstance().getUser().getPassword()
                                    .equals(bbc.hash(password.getBytes()))) {

                                System.out.println("--------------------");
                                for (String s : cmd.listData(password)) {
                                    password = null;
                                    System.out.println(mainColor + s);
                                }
                                password = null;
                            } else {
                                System.out
                                        .println(KonsolenFarbe.ERROR.getCode() + "Das Password war falsch" + mainColor);
                                password = null;
                            }
                        }

                    }
                    case "adduser" -> {

                        if (loginRequired()) {
                            if (worte.length != 3) {
                                System.out.println(KonsolenFarbe.ERROR.getCode()
                                        + "Geben sie adduser und danach den nutzer und danach den originalpfad der Datei an"
                                        + mainColor);
                            } else {
                                String userToAdd = worte[1];
                                String fileToAdd = worte[2];

                                String password = reader.readLine(KonsolenFarbe.COMMAND.getCode()
                                        + "Geben sie bitte ihr Password ein: " + mainColor, '*');
                                cmd.addUsertoFile(userToAdd, fileToAdd, password);
                                password = null;

                            }
                        }

                    }
                    case "removeuser" -> {
                        if (loginRequired()) {
                            if (worte.length != 3) {
                                System.out.println(KonsolenFarbe.ERROR.getCode()
                                        + "Geben sie removeuser und danach den nutzer und danach den originalpfad der Datei an"
                                        + mainColor);
                            } else {
                                String userToAdd = worte[1];
                                String fileToAdd = worte[2];
                                cmd.removeUserFromFile(userToAdd, fileToAdd);
                            }

                        }

                    }
                    case "showusers" -> {
                        if (loginRequired()) {
                            cmd.showUsers();

                        }

                    }
                    case "deletefile" -> {
                        if (loginRequired()) {
                            if (worte.length != 2) {
                                System.out.println(
                                        KonsolenFarbe.ERROR.getCode() + "falsch benutzung des Befehls" + mainColor);
                            }
                            String dateiPfad = worte[1];
                            if (reader
                                    .readLine(KonsolenFarbe.COMMAND.getCode() + "Sind sie Sicher ob sie die Datei "
                                            + dateiPfad + " löschen wollen [Y/N](default N): " + mainColor)
                                    .toLowerCase().equals("y")) {
                                cmd.deleteFile(dateiPfad);

                            }

                        }
                    }

                    case "exit" -> {
                        if (!UserSingleton.getInstance().getUser().getId().equals("")) {
                            cmd.logout();
                        }
                        System.out.println("Auf Wiedersehen!");
                        System.exit(0);
                    }

                    default -> {
                        System.out.println(KonsolenFarbe.COMMAND.getCode()
                                + "mit help bekommen sie alle Befehle angezeigt" + mainColor);
                    }

                }

            }

        }

    }

    private boolean loginRequired() {
        if (!isLoggedIn) {
            System.out.println(mainColor + "sie müssen angemeldet sein dafür" + mainColor);
            return false;
        } else {
            return true;
        }
    }

    private boolean alreadyLoggedIn() {
        if (!UserSingleton.getInstance().getUser().getId().equals("")) {
            System.out.println(
                    KonsolenFarbe.ERROR.getCode() + "Sie sind schon angemeldet wollen sie sich abmelden und fortfahren?"
                            + mainColor);
            String confirm = reader.readLine("[Y/N](dafault: N): ").toLowerCase();
            if (confirm.equals("y")) {
                cmd.logout();
                mainColor = KonsolenFarbe.RESET.getCode();
                isLoggedIn = false;
                return true;
            } else
                uiOutputs.cantBeLoggedInError();
            return false;

        }
        return true;
    }

}
