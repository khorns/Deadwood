package Deadwood;
import java.util.*;
import java.lang.String;

public class Card {
    private String name;
    private String description;
    private int budget;
    private int numOfRoles;
    private ArrayList<StarringRole> roles;

    /**
     * Card object constructor
     * @param name
     * @param description
     * @param budget
     * @param numOfRoles
     * @param roles
     */
    Card(String name, String description, int budget, int numOfRoles, ArrayList<StarringRole> roles) {
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.numOfRoles = numOfRoles;
        this.roles = roles;
    }

    Card(String name, String description, int budget) {
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.numOfRoles = numOfRoles;
        roles = new ArrayList<StarringRole>();
    }

    /**
     * the following functions are getters and setters
     * @return
     */
    public ArrayList<StarringRole> getRoles(){
        return roles;
    }

    public String getName() {
       return name;
    }

    public int getBudget() {
       return budget;
    }

    public int getNumberOfRoles(){
        return numOfRoles;
    }

    public String getImage() {
        return description;
    }

}
