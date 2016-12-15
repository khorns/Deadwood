package Deadwood;
import java.util.*;
import processing.core.*;

public class Player extends  PApplet{
    private String name;
    private int credits;
    private int money;
    private int rank;
    private String role;
    private int currentRehearseBonus;
    private String playerLocation;
    private boolean inStar;
    private boolean inExtra;
    private ExtraRole myExtraRole = null;

    public int xpos;
    public int ypos;
    public int prevLocationX;
    public int prevLocationY;
    public boolean active = true;

    /**
     * constructor for Player object
     * @param playerName
     */
    public Player(String playerName){
        name = playerName;
        credits = 0;
        money = 0;
        rank = 1;
        role = "Unemployed";
        currentRehearseBonus = 0;
        playerLocation = "Trailers";
        inStar = false;
        inExtra = false;
    }

    public Player(String playerName, int xposition, int yposition){
        name = playerName;
        credits = 0;
        money = 0;
        rank = 1;
        role = "Unemployed";
        currentRehearseBonus = 0;
        playerLocation = "Trailers";
        inStar = false;
        inExtra = false;

        xpos = xposition;
        ypos = yposition;
        prevLocationX = xposition;
        prevLocationY = yposition;
    }

    /**
     * setExtraRole changes the value of myExtraRole to the ExtraRole object that is passed to it as a parameter
     * @param newExtraRole
     */
    public void setExtraRole(ExtraRole newExtraRole) {
        myExtraRole = newExtraRole;
    }

    /**
     * getExtraRole returns the myExtraRole attribute of the player object.
     * In other terms, it returns the Extra Role the player is in.
     * @return ExtraRole
     */
    public ExtraRole getExtraRole() {
        return myExtraRole;
    }

    /**
     * move takes in an array of players, an array list of Sets, and an integer representing the current turn
     * it returns a boolean representing whether or not the player successfully moved
     * @param player
     * @param allRooms
     * @param turn
     * @return boolean
     */
    public static boolean move(Player[] player, ArrayList<Set> allRooms, int turn){

        if (Objects.equals(player[turn].getRole(), "Unemployed")) {
            String playerLocation = player[turn].getPlayerLocation();
            ArrayList<String> adjRoom = new ArrayList<>();

            for (Set allRoom : allRooms) {
                String name = allRoom.name;
                if (Objects.equals(name, playerLocation)) {
                    adjRoom = allRoom.adjacentSets;
                    System.out.println(allRoom.adjacentSets);
                }
            }

            Scanner in = new Scanner(System.in);
            System.out.println("Which room you want to move to?");

            int i = 0;
            for (String availableRoom : adjRoom) {
                System.out.println(i + ": " + availableRoom);
                i++;
            }

            // Player's input
            int roomChoice;
            String nameChoice;
            boolean check = false;
            while (!check){
                try {
                    roomChoice = in.nextInt();
                    nameChoice = adjRoom.get(roomChoice);
                    player[turn].moveTo(nameChoice);
                    check = true;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("!! Incorrect Choice !!");
                    System.out.printf("Please enter the correct choice between 0 to %d: ", i-1);
                }
            }

            System.out.println("Current player location is: " + player[turn].getPlayerLocation());
            return true;
        }
        else {
            System.out.println("You need to finish your role first before moving to a new location!");
        }
    return false;

    }

    /**
     * resetRehearseBonus sets the currentRehearseBonus to 0.
     */
    public void resetRehearseBonus(){
        currentRehearseBonus = 0;
    }

    /**
     * getInStar returns a boolean representing whether or not the player object is in a starring role.
     * @return
     */
    public boolean getInStar(){
        return inStar;
    }

    /**
     * increase money takes in an integer parameter and adds the amount to the player object's money attribute.
     * @param amount
     */
    public void increaseMoney(int amount) {
        money += amount;
    }

    /**
     * The following functions are setters and getters
     * @return
     */
    public boolean getInExtra(){
        return inExtra;
    }

    public void setInStar(boolean bool){
        inStar = bool;
    }

    public void setInExtra(boolean bool){
        inExtra = bool;
    }

    /**
     * moveTo changes a player object's location attribute to the room they wish to move to, which is passed in as a String parameter.
     * @param newRoom
     */
    public void moveTo(String newRoom){
        this.playerLocation = newRoom;
    }

    /**
     * The following functions are getters and setters.
     * @return
     */
    public String getName() {
        return name;
    }

    public int getCredit(){
        return this.credits;
    }

    public void setCredit(int newCredits){
        this.credits = newCredits;
    }

    public int getMoney(){
        return this.money;
    }

    public void setMoney(int amount) {
        this.money = amount;
    }

    public void setPlayerLocation(String newLocation) {
        playerLocation = newLocation;
    }

    public int getRank(){
        return this.rank;
    }

    /**
     * removeExtraRole resets a player object's myextrarole, inextra, and role attributes to their default values
     * In other terms, it removes a player from their Extra Role one the scene has finished.
     */
    public void removeExtraRole() {
        myExtraRole = null;
        inExtra = false;
        role = "Unemployed";
    }

    /**
     * removeStarringRole performs the same operation as removeExtraRole, but for players in starring roles as opposed to players in extra roles.
     */
    public void removeStarringRole() {
        role = "Unemployed";
        inStar = false;
    }

    /**
     * updateRank changes the rank of a player in return for credit or money payout.
     * it returns a boolean representing whether or not the player successfully upgraded their rank.
     * @return
     */
    public boolean updateRank(){
        Scanner in  = new Scanner(System.in);
        String choice;
        int newRank;
        boolean upgraded = false;
        do {
            System.out.println("Rank\tDollar\tCredits");
            System.out.println("-----------------------");
            System.out.println("2\t\t4\t\t5");
            System.out.println("3\t\t10\t\t10");
            System.out.println("4\t\t18\t\t15");
            System.out.println("5\t\t28\t\t20");
            System.out.println("6\t\t40\t\t25");
            System.out.println("Which rank do you want to upgrade to?");
            newRank = in.nextInt();

            while((((newRank < 2) || (newRank > 6)) || (newRank <= rank && newRank != 0)) && newRank != 0) {
                if((newRank < 2) || (newRank > 6)) {
                    System.out.println("Only ranks 2 - 6 are available!\nPlease choose again.");
                    newRank = in.nextInt();
                }
                if(newRank <= rank && newRank != 0) {
                    System.out.println("You cannot choose a rank below or equal your current rank!\nPlease choose again!");
                    newRank = in.nextInt();
                }
            }
            if(newRank != 0) {
                System.out.println("Options:");
                System.out.println("1. Pay with credits. Credits amount: " + credits);
                System.out.println("2. Pay with money. Money Amount: $" + money);
                choice = in.next();

                while (!Objects.equals(choice, "1") && !Objects.equals(choice, "2")) {
                    System.out.println("You have to choose 1 or 2!\nPlease choose again.");
                    choice = in.next();
                }

                if (Objects.equals(choice, "2")) {
                    upgraded = payMoney(newRank);
                } else if (Objects.equals(choice, "1")) {
                    upgraded = payCredits(newRank);
                }
            }
        }while(!upgraded && !(newRank == 0));

        if(newRank == 0)
            upgraded = false;

        return upgraded;
    }

    /**
     * displayReceipt displays information in regards to an upgradeRank() function call
     * @param rank
     * @param isMoney
     */
    private void displayReceipt(int rank, boolean isMoney){
        System.out.println("You upgraded to rank " + rank + "!");
        if(isMoney)
            System.out.println("Your money amount is : $" + money + ".");
        else
            System.out.println("Your credit amount is: " + credits +".");
    }

    /**
     * payMoney allows a player to upgrade their rank in exchange for an amount of dollars
     * @param newRank
     * @return
     */
    private boolean payMoney(int newRank) {
        boolean enoughMoney = true;
        if(checkMoney(newRank)){
            if(newRank == 2) {
                money -= 4;
                this.rank = 2;
            }
            else if(newRank == 3) {
                money -= 10;
                this.rank = 3;
            }
            else if(newRank == 4) {
                money -= 18;
                this.rank = 4;
            }
            else if(newRank == 5) {
                money -= 28;
                this.rank = 5;
            }
            else if(newRank == 6) {
                money -= 40;
                this.rank = 6;
            }
            displayReceipt(newRank, true);
        }
        else {
            enoughMoney = false;
            System.out.println("You do not have enough money!");
        }
        return enoughMoney;
    }

    /**
     * payCredits allows a player to upgrade their rank in exchange for an amount of credits
     * @param newRank
     * @return
     */
    private boolean payCredits(int newRank){
        boolean enoughCredits = true;
        if(checkCredits(newRank)){
            if(newRank == 2) {
                credits -= 5;
                this.rank = 2;
            }
            else if(newRank == 3) {
                credits -= 10;
                this.rank = 3;
            }
            else if(newRank == 4) {
                credits -= 15;
                this.rank = 4;
            }
            else if(newRank == 5) {
                credits -= 20;
                this.rank = 5;
            }
            else if(newRank == 6) {
                credits -= 25;
                this.rank = 6;
            }
            displayReceipt(newRank, false);
        }
        else {
            enoughCredits = false;
            System.out.println("You do not have enough credits!");
        }
        return enoughCredits;
    }

    /**
     * checkCredits returns a boolean representing whether or not a player has enough credits to upgrade to a certain rank, which is passed in as an integer parameter
     * @param newRank
     * @return
     */
    private boolean checkCredits(int newRank) {
        boolean ret = false;

        if(newRank == 2){
            if(credits >= 4)
                ret = true;
        }
        else if(newRank == 3){
            if(credits >= 10)
                ret = true;
        }
        else if(newRank == 4){
            if(credits >= 18)
                ret = true;
        }
        else if(newRank == 5){
            if(credits >= 28)
                ret = true;
        }
        else if(newRank == 6){
            if(credits >= 40)
                ret = true;
        }
        return ret;
    }

    /**
     * checkMoney returns a boolean representing whether or not a player has enough money to upgrade to a certain rank, which is passed in as an integer parameter.
     * @param newRank
     * @return
     */
    private boolean checkMoney(int newRank) {
        boolean ret = false;

        if(newRank == 2){
            if(money >= 4)
                ret = true;
        }
        else if(newRank == 3){
            if(money >= 10)
                ret = true;
        }
        else if(newRank == 4){
            if(money >= 18)
                ret = true;
        }
        else if(newRank == 5){
            if(money >= 28)
                ret = true;
        }
        else if(newRank == 6){
            if(money >= 40)
                ret = true;
        }
        return ret;
    }

    /**
     * The following functions are getters and setters (updatePlayerLocation is a setter)
     * @return
     */
    public String getPlayerLocation(){
        return this.playerLocation;
    }

    public void updatePlayerLocation(String newRoom){
        this.playerLocation = newRoom;
    }

    public String getRole(){
        return this.role;
    }

    public void setRole(String newRole){
        this.role = newRole;
    }

    public int getRehearseBonus(){
        return this.currentRehearseBonus;
    }

    /**
     * rehearse adds 1 to a player object's current rehearse bonus
     */
    public void rehearse(){
        this.currentRehearseBonus += 1;
    }

}
