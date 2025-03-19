FileCrypter

Eine in Java geschriebene Shell-Line-Anwendung, die verwendet werden kann, um Dateien zu verschlüsseln und an beliebigen Ablageorten auf einem Computer zu speichern. 
Mehrere Benutzer auf einer lokalen Maschine können sich gegenseitig den Zugriff auf verschiedene verschlüsselte Dateien autherisieren.

Geschrieben in Java 17, werden moderne Crypto Algorithms verwendet, mithilfe der Java Crypto Library.
Alle Keys werden sicher in einer SQLite Databank gespeichert.
Die Anwendung verwendet Jline Autocomplete, um Befehle zu erstellen und verwendet ein Farbschema, um zwischen angemeldetem und abgemeldetem Status zu unterscheiden.


Commands:
        register: geben sie register ohne einen anhang an und geben sie danach eine Einzigartigen Namen und ihr Password ein welches sie immer beim anmelden benötigen werden\n
        login: geben sie login ohne anhang an um sich mit ihrem Benutzernamen und Password anzumelden\n
        addfile: geben sie addfile so an um Datein zuverschlüsslen und sicher abzulegen [addfile <dateipfad>]\n
        readfile: geben sie readfile an um eine Datei zu entschlüsseln und angezeigt zu bekommen.
          Falls sie diese Datei verschlüsselt haben können sie diese nach Bestätigung an einen bestimmten Ort ablegen [readfile <orginalDateiPfad>]
        logout: um sich mit dem aktuel angemeldeten Benutzer abzumelden
        ls: die berechtigten Datein des aktuellen Benutzers aufzulisten
        adduser: gibt einem Nutzer die Berechtigung eine bestimtme angegebene Datei zu lesen[adduser <user> <dateipfad>]
        removeuser: nehmt einem Nutzer die Berechtigung eine bestimtme angegebene Datei zu lesen [removeuser <user> <dateipfad>]
        showusers: gibt alle auf diesem System registrierten Leute zurück
        deletefile: sie können damit die angegebene verschlüsselte Datei löschen und alle Berechtigungen zu dieser Datei die von ihnen vergeben wurden entfernen. 
