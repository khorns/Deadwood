package Deadwood;
public class ExtraRole extends Role {


    public ExtraRole() {

    }

    /**
     * ExtraRole constructor
     * @param Title
     * @param Quote
     * @param Rank
     */
    public ExtraRole(String Title, String Quote, int Rank){
        roleTitle = Title;
        roleQuote = Quote;
        rankRequiredForRole = Rank;
    }

    /**
     * successfulRoll distributes 1 dollar and 1 credit to a player object
     * @param player
     */
    public void successfulRoll(Player player){
        player.setMoney(player.getMoney() + 1);
        player.setCredit(player.getCredit() + 1);
    }
}
