package MusicStore;

public class UserSession {
    private static UserSession instance;
    private static Roles role;

    private UserSession(Roles role) {
        UserSession.role = role;
    };

    public static void startSession(Roles role) {
        if (instance == null) {
            instance = new UserSession(role);
            System.out.println("Session started for: " + role.toString());
        } else {
            System.out.println("Session already active.");
        }
    }

    public static void endSession() {
        if (instance != null) {
            System.out.println("Session ended for: " + role.toString());
            instance = null;
        } else {
            System.out.println("No active session to end.");
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public Roles getRole() {
        return role;
    }

    public void handleCommand(String command) {

    }
}
