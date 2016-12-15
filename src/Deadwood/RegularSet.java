package Deadwood;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

public class RegularSet extends Set {

    private int shots = 0;
    private int originShots;
    private ArrayList<ExtraRole> extraRoles = new ArrayList<>();
    private Card myCard;
    private boolean isStarringRole = false;
    private Player starringPlayers[];
    private ArrayList<Player> extraPlayers;
    private boolean setCompleted = false;

    public int playerNum = 0;



    /**
     * RegularSet constructor
     * @param name
     * @param shot
     * @param adj
     */
    public RegularSet(String name, int shot, ArrayList<String> adj) {

        super.name = name;
        shots = shot;
        super.adjacentSets = adj;
        originShots = shots;
        extraPlayers = new ArrayList<>();
    }

    public void setStarringRole() {
        isStarringRole = true;
    }

    /**
     * payExtras distributes money to players in extra roles when a scene finishes, if there is at least one player in a starring role on the same set.
     */
    public void payExtras() {
        Player myPlayer;
        ExtraRole myRole;
        for(int i = 0; i < extraPlayers.size(); i++) {
            myPlayer = extraPlayers.get(i);
            if(isStarringRole) {
                myRole = myPlayer.getExtraRole();
                myPlayer.increaseMoney(myRole.getRankRequiredForRole());
            }
            myPlayer.removeExtraRole();
        }

    }

    /**
     * isCompleted returns a boolean representing whether or not a scene has been completed yet this game
     * @return
     */
    public boolean isCompleted(){
        return setCompleted;
    }

    public void setCompleted(){
        setCompleted = true;
    }

    /**
     * addExtraPlayer adds a player object to an array list of player objects
     * @param myPlayer
     */
    public void addExtraPlayer(Player myPlayer) {
        extraPlayers.add(myPlayer);
    }

    public void setPickedRole(int role, Player player) {
        starringPlayers[role] = player;
        System.out.print("");
    }

    public Card getCard(){
        return myCard;
    }

    /**
     * decrementShot subtracts 1 from the shot counter on a set
     */
    public void decrementShot() {
        shots--;
    }

    public void setCard(Card newCard) {
        int roles = newCard.getNumberOfRoles();
        myCard = newCard;
        starringPlayers = new Player[roles];
    }

    /**
     * removeCard removes a card object from the regular set object, usually when a scene has finished.
     */
    public void removeCard() {
        myCard = null;
    }

    /**
     * payout distributes money to starring roles and extra roles accordingly when a scene finishes.
     * for information on how the money is distributed, see Deadwood rules.
     */
    public void payout() {
        int budget = myCard.getBudget();
        int dice[] = new int[budget];
        int playerIndex = myCard.getNumberOfRoles() - 1;

        for(int i = 0; i < dice.length; i++){
            dice[i] = rollDie();
        }

        Arrays.sort(dice);
        if(isStarringRole) {
            System.out.println("Dice rolls for payout:");
            for (int i = 0; i < dice.length; i++) {
                System.out.println(dice[i] + " ");
            }
            for (int i = budget - 1; i >= 0; i--) {
                if (starringPlayers[playerIndex] != null) {
                    starringPlayers[playerIndex].increaseMoney(dice[i]);
                    starringPlayers[playerIndex].removeStarringRole();
                }

                playerIndex--;

                if (playerIndex < 0) {
                    playerIndex = myCard.getNumberOfRoles() - 1;
                }
            }
        }
        payExtras();
    }

    /**
     * rollDie returns a random integer between 1 and 6, representing rolling a six-sided die.
     * @return
     */
    private int rollDie() {
        Random rand = new Random();
        int num = rand.nextInt(6) + 1;
        return num;
    }

    public int getOriginShots() {
        return originShots;
    }

    public int getShot() {
        return shots;
    }

    public void setShots(int num) {
        shots = num;
    }

    /**
     * addExtraRole adds an ExtraRole object to an array list of extra role objects
     */
    public void addExtraRole(ExtraRole newRole){
        extraRoles.add(newRole);
    }

    public String getExtraRoleQuote(int i) {
        return extraRoles.get(i).getRoleQuote();
    }


    public int getNumberOfExtraRoles(){
        return extraRoles.size();
    }

    public String getExtraRoleTitle(int i) {
        return extraRoles.get(i).getRoleTitle();
    }

    public int getRankRequiredForRole(int i){
        return extraRoles.get(i).getRankRequiredForRole();
    }

    public ArrayList<ExtraRole> getExtraRoles(){
        return extraRoles;
    }

    /**
     * functions without comments are getters and setters.
     */

    public Card getMyCard() {
        return myCard;
    }

}
