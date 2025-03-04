
package de.eymen.filedecrypter.domain;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FilePresenter {

    public FilePresenter() {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop();
        }
    }

    public void openFile(String url) {
        File datei = new File(url);
        try {
            if (Desktop.isDesktopSupported()) {

                Desktop.getDesktop().open(datei);

            }
        } catch (IOException e) {
            System.out.println("\nDesktop wird nicht supported\n");
        }
    }

}
