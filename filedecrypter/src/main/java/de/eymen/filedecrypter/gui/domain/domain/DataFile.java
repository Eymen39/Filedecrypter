package de.eymen.filedecrypter.gui.domain.domain;

public class DataFile {

    public String hashname; // der gehashte name der Datei
    public String AESkey;// Das ist der AES-Key mit dem Namen der Datei dieser wird mit dem public key
                         // verschlüsselt
    public String name;
    public String MasterId;
    public String SlaveId;

}
