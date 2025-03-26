package MusicStore;

public enum Roles {
    EMPLOYEE("Employee"),
    MANAGER("Manager");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isEmployee() {
        return this == EMPLOYEE;
    }

    public boolean isManager() {
        return this == MANAGER;
    }

    @Override
    public String toString() {
        return roleName;
    }
}

