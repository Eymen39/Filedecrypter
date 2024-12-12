package de.eymen.filedecrypter.gui.ui;

public enum KonsolenFarbe {

    RESET("\u001B[0m"),
    COMMAND("\u001B[33m"),
    ERROR("\u001B[31m"),
    LOGIN("\u001B[34m");

    private final String code;

    KonsolenFarbe(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}