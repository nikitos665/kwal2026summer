package util;

import model.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isMaster() {
        return currentUser != null && currentUser.getRole().name().equals("MASTER");
    }

    public static boolean isClient() {
        return currentUser != null && currentUser.getRole().name().equals("CLIENT");
    }
}