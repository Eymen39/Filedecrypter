package de.hma.srn.domain;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import de.hma.srn.ui.UIOutputs;

public class FilePresenter {

    public FilePresenter() {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop();
        }
    }

    public void openFile(String url) {
        File datei = new File(url);
        if (Desktop.isDesktopSupported()) {
            try {

                Desktop.getDesktop().open(datei);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                UIOutputs uiOutputs = new UIOutputs();
                uiOutputs.dataCorrupt();
            }
        }
    }

}
