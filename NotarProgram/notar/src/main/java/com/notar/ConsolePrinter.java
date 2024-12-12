package com.notar;

import java.io.Console;

public class ConsolePrinter {
    Console console;

    public ConsolePrinter() {
        console = System.console();
    }

    public String startProgramm() {
        console.printf("Willkommen in der Datenwiederherstellung!");
        console.printf("\nWas möchten Sie tun?");
        console.printf("\n(1) Ein keyPair für die Datensicherung erstellen");
        console.printf("\n(2) Daten Wiederherstellen");
        console.printf("\n(3) exit\n");
        return console.readLine();
    }

    public String creatingKeys() {
        console.printf("\n Geben Sie einen Ablageort für die beiden RSA Schlüssel an:\n");
        return console.readLine();
    }

    public String[] recoverFiles() {
        String[] list = new String[3];

        console.printf("\n Bitte geben Sie den privaten Schlüssel des Notars an (.pem file):\n");
        String privateKey = console.readLine();
        list[0] = privateKey;
        console.printf("\n Bitte geben Sie den Dateipfad der Daten an:\n");
        String fileDirectory = console.readLine();
        list[1] = fileDirectory;
        console.printf(
                "\n Bitte geben Sie einen Dateipfad an, an dem Sie die Wiederherstellung lagern wollen:\n");
        String outputDirectory = console.readLine();
        list[2] = outputDirectory;

        return list;
    }
}
