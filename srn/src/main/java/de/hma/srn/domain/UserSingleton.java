package de.hma.srn.domain;

public class UserSingleton {

    private static UserSingleton instance;
    private User user;

    private UserSingleton() {
        user = new User();
    }

    public static UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    };

    public void setUser(User newUser) {
        this.user.setUser(newUser);
    }

    public User getUser() {
        return user;
    }

    public void removeUser() {
        instance = null;
        user = null;
    }

}
