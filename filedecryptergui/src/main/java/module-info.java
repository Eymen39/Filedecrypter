module de.eymen.filedecrypter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens de.eymen.filedecrypter to javafx.fxml;

    exports de.eymen.filedecrypter;
}
