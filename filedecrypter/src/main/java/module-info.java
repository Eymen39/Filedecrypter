module de.eymen.filedecrypter.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens de.eymen.filedecrypter.gui to javafx.fxml;

    exports de.eymen.filedecrypter.gui;
}
