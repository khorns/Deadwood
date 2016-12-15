package Deadwood;
import processing.core.PApplet;

import java.util.*;
import java.io.*;

import static java.lang.Integer.parseInt;


public class Deadwood {

    private static int numPlayer = 0;
    private static int currentDay = 0;
    private static int cardsOnBoard = 0;
    private static ArrayList<Card> deck;
    private static ArrayList<Set> allRooms;
    public static ArrayList<RegularSet> rooms;

    public static void main(String[] args) {

        PApplet.main(new String[] {"Deadwood.Processing"});

    }

    public void loadGame(ArrayList<Set> allRoom) throws IOException {
        //  Loading up all assets
        String collectRoom = readFile("board.txt");
        String[] splitCollect = collectRoom.split("\\r\\n");
        ArrayList<String> allExtraRole = assignExtraRoles("exRole.txt");
        //  Create all the extra role put them into its place
        buildDeck();
        allRooms = new ArrayList<>();
        rooms = createRooms(splitCollect);
        CastingOffice castOffice = new CastingOffice();
        loadUpExtraRoles(allExtraRole, rooms);

        setRoomCard(rooms);

        for (int i = 0; i < rooms.size(); i++) {
            allRooms.add(rooms.get(i));
        }
        allRooms.add(castOffice);

        for (int i = 0; i < rooms.size(); i++) {
            allRoom.add(rooms.get(i));
        }
        allRoom.add(castOffice);

        cardsOnBoard = 10;
    }

    public int changeTurn(int turn) {
        if (turn < 1) {
            turn ++;
        }
        else {
            turn =0;
        }
        return turn;
    }

    public int rollingDie() {
        Random rand = new Random();
        return rand.nextInt(6) + 1;
    }


    public void setRoomCard(ArrayList<RegularSet> room) {

        for (int i = 0; i < room.size(); i++) {
            int randomCard = (int) (Math.random() * 39);
            room.get(i).setCard(deck.get(randomCard));
        }
    }

    private static void loadUpExtraRoles(ArrayList<String> allExtraRole, ArrayList<RegularSet> rooms) {
        //  Create all the extra role put them into its place
        for(int i = 0; i < allExtraRole.size(); i++ ){
            String element = allExtraRole.get(i);
            String[] splitElement = element.split("-");


            for (int j = 0; j < rooms.size(); j++) {

                if(Objects.equals(rooms.get(j).name, splitElement[0])){
                    ExtraRole exRole = new ExtraRole(splitElement[1], splitElement[2], parseInt(splitElement[3]));
                    rooms.get(j).addExtraRole(exRole);
                }
            }
        }
    }

    private static ArrayList<RegularSet> createRooms(String[] splitCollect) {
        ArrayList<RegularSet> rooms = new ArrayList<>();

        //  Create the 11 regular rooms
        for (int i = 0; i < 11; i++) {
            int j = i * 4;

            String[] splitAdj = splitCollect[j+3].split("-");
            ArrayList<String> adjacentSets = new ArrayList<>(Arrays.asList(splitAdj));

            RegularSet roomCollect = new RegularSet(splitCollect[j], parseInt(splitCollect[j+1]), adjacentSets);
            rooms.add(roomCollect);
        }
        return rooms;
    }

    private static void buildDeck() {
        try {
            ArrayList<StarringRole> roles;
            Scanner input = new Scanner(new File ("cards.txt"));
            String line;
            String name;
            String description;
            String charName;
            String charLine;
            int budget;
            int numOfRoles;
            int rank;
            deck  = new ArrayList<>();

            while(input.hasNextLine()){
                //Card Name
                input.nextLine();
                name = input.nextLine();
                //Scene description
                description = input.nextLine();
                //Budget
                line = input.nextLine();
                budget = parseInt(line);
                //number of roles
                line = input.nextLine();
                numOfRoles = parseInt(line);

                roles = new ArrayList<>();
                for(int i = 0; i < numOfRoles;  i++) {
                    //rank of the role
                    line = input.nextLine();
                    rank = parseInt(line);
                    //character name
                    charName = input.nextLine();
                    //character line
                    charLine = input.nextLine();
                    roles.add(new StarringRole(charName, charLine, rank));
                }
                deck.add(new Card(name, description, budget, numOfRoles, roles));
            }
            input.close();
        }
        catch(FileNotFoundException e){
            System.out.printf("File not found\n");
        }
    }

    public int finalPlayerStatus(Player player) {
        int score = calFinal(player);
        System.out.println("**********************************");
        System.out.println("Name:             " + player.getName());
        System.out.println("Rank:             " + player.getRank());
        System.out.println("Credit:           " + player.getCredit());
        System.out.println("Money:            " + player.getMoney());
        System.out.println("Total Score:      " + score);
        System.out.println("**********************************");
        return score;
    }

    public int calFinal(Player player) {
        return player.getMoney() + player.getCredit() + (player.getRank() * 5);
    }

    private static ArrayList<String> assignExtraRoles(String roleFileName){
        // read in the file
        ArrayList<String> allExtraRoles = new ArrayList<>();
        File roleFile = new File (roleFileName);

        try (BufferedReader br = new BufferedReader(new FileReader(roleFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                allExtraRoles.add(line);
                // process the line.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allExtraRoles;

    }

    private static String readFile(String filename) throws IOException {
        String content = null;
        File file = new File(filename);
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader !=null){reader.close();}
        }
        return content;
    }


}
