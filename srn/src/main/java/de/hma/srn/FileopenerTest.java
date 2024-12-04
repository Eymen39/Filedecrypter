package de.hma.srn;

import de.hma.srn.domain.*;
import de.hma.srn.domain.DataAccessHandler;
import de.hma.srn.domain.FilePresenter;

public class FileopenerTest {

    public static void main(String[] args) {

        FilePresenter fP = new FilePresenter();
        DataAccessHandler dAH = new DataAccessHandler(System.getProperty("os.name"));
        ConsoleHandler cmd = new ConsoleHandler(System.getProperty("os.name"));
        cmd.login("eymen", "1");
        dAH.showFile("D:\\eymen.txt", "1");

    }
}
