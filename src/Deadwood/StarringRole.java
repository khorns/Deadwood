package Deadwood;
import java.util.*;
import java.io.*;


public class StarringRole extends Role {

    /**
     * constructor for StarringRole object
     * @param Title
     * @param Quote
     * @param Rank
     */
    StarringRole(String Title, String Quote, int Rank){
        roleTitle = Title;
        roleQuote = Quote;
        rankRequiredForRole = Rank;
    }

    /**
     * successfulRoll adds two credits to a player object's credit attribute
     * @param player
     */
    public void successfulRoll(Player player){
        player.setCredit(player.getCredit() + 2);
    }

    /**
     * The following functions are getters, setters, or display functions to display attribute information about a starringRole object
     * @return
     */
    public String getRoleTitle() {
        return roleTitle;
    }

    public String getRoleQuote() {
        return roleQuote;
    }

    public void displayTitle() {
        System.out.printf("%s\n", roleTitle);
    }

    public void displayQuote() {
        System.out.printf("%s\n", roleQuote);
    }

    public void displayRank() {
        System.out.printf("%d\n", rankRequiredForRole);
    }

    public void displayAll() {
        displayTitle();
        displayQuote();
        displayRank();
    }
}
