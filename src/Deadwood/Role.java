package Deadwood;
public abstract class Role {
    String roleTitle;
    String roleQuote;
    int rankRequiredForRole;
    Boolean available = true;

    public abstract void successfulRoll(Player player);

    /**
     * isAvailable returns a boolean representing whether or not a role is currently taken
     * @return
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * the following functions are getters and setters
     */
    public void setUnavailable() {
        available = false;
    }

    public String getRoleTitle(){
        return roleTitle;
    }

    public String getRoleQuote(){
        return roleQuote;
    }

    public int getRankRequiredForRole(){
        return rankRequiredForRole;
    }
}

