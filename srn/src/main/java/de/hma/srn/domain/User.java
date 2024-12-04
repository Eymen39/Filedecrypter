package de.hma.srn.domain;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String name = "";
    private String id = "";
    private String password = "";
    private String privateKey = "";
    private Boolean notar = false;
    private Map<String, String> publicKeys;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        publicKeys = new HashMap<String, String>();
    };

    public User(String name, String id, Boolean notar) {
        this.name = name;
        this.id = id;
        publicKeys = new HashMap<String, String>();
        this.notar = notar;
    };

    public User() {
        publicKeys = new HashMap<String, String>();
    }

    public User(String name, String id, String password) {

        this.name = name;
        this.id = id;
        this.password = password;

    }

    public void setPrivateKey(String key) {
        privateKey = key;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Boolean getNotarFlag() {
        return notar;
    }

    public void setNotarFlag(Boolean flag) {
        this.notar = flag;
    }

    public void setPublicKeys(Map<String, String> map) {
        this.publicKeys = map;
    }

    public Map<String, String> getPublicKeys() {
        return publicKeys;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public Map<String, String> getPublicKeyMap() {
        return publicKeys;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String hash) {
        password = hash;
    }

    public void setUser(User newUser) {
        this.name = newUser.name;
        this.id = newUser.id;
        this.password = newUser.password;
        this.privateKey = newUser.privateKey;
        this.publicKeys = newUser.publicKeys;
        this.notar = newUser.notar;
    }
}
