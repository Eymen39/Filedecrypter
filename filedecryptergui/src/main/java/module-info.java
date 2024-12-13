module de.eymen.filedecrypter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens de.eymen.filedecrypter to javafx.fxml;

    exports de.eymen.filedecrypter;
}
