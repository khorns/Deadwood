package Deadwood;

import processing.core.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Processing extends PApplet{

    Deadwood main;
    Player[] player;

    Player player1;
    Player player2;
    int playerSize = 32;
    int turn = 0;
    boolean pressed = false;
    PImage img;
    String moveMsg = "";

    private ArrayList<Set> allRooms;
    private ArrayList<RegularSet> rooms;
    private String cardLocation;
    private int buttonColor;
    private int cardsOnBoard;
    private int currentDay;
    private int dieNum;

    private String roleResult = "";
    private int cardBudget;

    public void settings() {
        size(1300,750);
    }

    public void setup() {
        background(0);
        img = loadImage("rsz_board.jpg");
        buttonColor = color(232, 189, 174);
        cardsOnBoard = 10;
        currentDay = 1;

        ///////////////////
        main = new Deadwood();
        allRooms = new ArrayList<>();
        try {
            main.loadGame(allRooms);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rooms = Deadwood.rooms;
        ////////////////////

        player = new Player[2];
        int playerInitLocationx = 880;
        int playerInitLocationy = 300;
        for (int i = 0; i < 2; i++) {
            player[i] = new Player("Player" + (i + 1), playerInitLocationx, playerInitLocationy);

        }
        player1 = new Player("Student", 880, 300);
        player2 = new Player("Disciple", 930, 300);
        smooth();
        noStroke();
    }

    public void draw() {
        background(0);
        image(img,0,0);
        fill(255);

//        turnBased();
        fill(255);
        text("x: " + mouseX, 1000, 150);
        text("y: " + mouseY, 1000, 200);
//        text(turn+1, 1000, 150);

        text("Turn: Player " + (turn+1), 1000, 100);
        showPlayerColor();
        fill(255);

        displayPlayerInfo(player[0], 240);
        displayPlayerInfo(player[1], 400);

        textSize(15);
        text(moveMsg, 1000, 30);

        turnBased();
        showCard();
        button();

        changeDay();
        shotCounterDraw();

        displayDie();

    }

    private void displayDie() {
        fill(255);
        player[turn].getPlayerLocation();

        text("Target budget: " + cardBudget, 1000, 550);
        text("Rolling:            " +dieNum, 1000, 570);
        text(roleResult, 1000, 590);

    }

    public void changeDay() {
        if (currentDay <= 3) {
            if (cardsOnBoard == 1) {
                currentDay++;
                int numPlayer = 2;
                for (int j = 0; j < numPlayer; j++) {
                    player[j].updatePlayerLocation("Trailers");
                }

                main.setRoomCard(rooms); // Reset all the card

                for (RegularSet room : rooms) {
                    room.setShots(room.getOriginShots());
                }
                cardsOnBoard = 10;
                println("cardsOnBoard: " + cardsOnBoard);
                println("currentDay: " + currentDay);
            }
        }
        else {
            endGame();
        }

    }

    private void showPlayerColor() {
        if (turn == 0) {
            fill(color(200, 25, 25));
            ellipse(1150, 95, playerSize, playerSize);
        }
        else {
            fill(color(25, 25, 255));
            ellipse(1150, 95, playerSize, playerSize);
        }
    }

    public void endGame() {
        System.out.println("\n\n**Game is ended**");
        System.out.println("Score Board");
        int bestScore = 0;
        int score;
        int numPlayer = 2;
        String winner= "";


        for (int i = 0; i < numPlayer; i++) {
            score = main.finalPlayerStatus(player[i]);
            if (score > bestScore) {
                bestScore = score;
                winner = player[i].getName();
            }
        }

        System.out.printf("The winner is %s\n", winner);
    }

    public boolean isAdj(String newRoom, String prevRoom) {
        boolean isAdjacent = false;
        Set prev = getRoom(prevRoom);

        if(prev != null) {
            for(int i = 0; i < prev.adjacentSets.size() && !isAdjacent; i++) {
                if(prev.adjacentSets.get(i).equals(newRoom)) {
                    isAdjacent = true;
                }
            }
        }

        return isAdjacent;
    }

    public void displayPlayerInfo(Player player, int yLocation) {
        text(player.getName() + ": " + player.getPlayerLocation(), 1000, yLocation);
        text("Role         : " + player.getRole(), 1025, yLocation+20);
        text("Rank        : " + player.getRank(), 1025, yLocation+40);
        text("Rehearse : " + player.getRehearseBonus(), 1025, yLocation+60);
        text("Money      : " + player.getMoney(), 1025, yLocation+80);
        text("Credits     : " + player.getCredit(), 1025, yLocation+100);
    }


    public Set getRoom(String room) {
        Set newRoom = null;
        boolean found = false;
        int i;

        for(i = 0; i < allRooms.size() && !found; i++) {
            if(room.equals(allRooms.get(i).name)) {
                newRoom = allRooms.get(i);
                found = true;
            }
        }

        return newRoom;
    }

    public void button() {
        buttonColor = color(232, 189, 174);
        // Rehearse Button
        fill(buttonColor);
        if(isInButton(1035, 620, 1135, 660)) {
            if (mousePressed) {
                fill(232, 138, 107);
            } else {
                fill(230, 156, 131);
            }
        }
        rect(1035,620,100,40);
        fill(128,90,65);
        text("Rehearse", 1050, 645);
        //----------------

        // Skip Button
        fill(buttonColor);
        if(isInButton(1155, 620, 1255, 660)) {
            if (mousePressed) {
                fill(232, 138, 107);
            } else {
                fill(230, 156, 131);
            }
        }
        rect(1155,620,100,40);
        fill(128,90,65);
        text("Skip", 1190, 645);
        //----------------

        // Act Button-----
        fill(buttonColor);
        if(isInButton(1035, 675, 1255, 715)) {
            if (mousePressed) {
                fill(232, 138, 107);
            } else {
                fill(230, 156, 131);
            }
        }
        rect(1035,675,220,40);
        fill(128,90,65);
        text("ACT", 1130, 700);
        //----------------

    }

    public boolean act() {
        boolean result = false;

        if (!Objects.equals(player[turn].getRole(), "Unemployed")) {
            dieNum = main.rollingDie();
            int rehearseTotal = player[turn].getRehearseBonus();
            System.out.println("You rolled a:\t\t" + dieNum);
            System.out.println("Your rehearsal bonus: " + rehearseTotal);

            int i = 0;
            while (!Objects.equals(allRooms.get(i).name, player[turn].getPlayerLocation())) {
                i++;
            }

            int budget = rooms.get(i).getCard().getBudget();
            cardBudget = budget;
            System.out.println("The budget is:\t\t" + budget);

            if (dieNum >= (budget - rehearseTotal)) {                     // Success
                result = true;
                System.out.println("****** Success ******");
                roleResult = "Success";
                if(player[turn].getInStar()){
                    for (int j = 0; j < rooms.get(i).getCard().getNumberOfRoles(); j++){
                        if(player[turn].getRole().equals(rooms.get(i).getCard().getRoles().get(j).getRoleTitle())){
                            rooms.get(i).getCard().getRoles().get(j).successfulRoll(player[turn]);
                            System.out.println("You received 2 credits.");
                        }
                    }
                }
                else if(player[turn].getInExtra()){
                    for (int j = 0; j < rooms.get(i).getExtraRoles().size(); j++){
                        if (player[turn].getRole().equals(rooms.get(i).getExtraRoles().get(j).getRoleTitle())){
                            rooms.get(i).getExtraRoles().get(j).successfulRoll(player[turn]);
                            System.out.println("You received 1 credit and 1 dollar.");
                        }
                    }
                }

                rooms.get(i).decrementShot();
                if(rooms.get(i).getShot() == 0){
                    System.out.println("There are no shots remaining.");
                }
                else {
                    System.out.println("There are: " + rooms.get(i).getShot() + " shots left.");
                }

                if (rooms.get(i).getShot() == 0 && !rooms.get(i).isCompleted()) {
                    rooms.get(i).payout();
                    player[turn].setRole("Unemployed");
                    player[turn].resetRehearseBonus();
                    rooms.get(i).setCompleted();

                    // Reset player to unemployed
                    if (player[turn].getInStar()) {
                        player[turn].setInStar(false);
                    } else if (player[turn].getInExtra()) {
                        player[turn].setInExtra(false);

                    }

                    // Remove the card from the set
                    rooms.get(i).removeCard();
                    cardsOnBoard--;
                    System.out.println("The scene has been completed. There are " + cardsOnBoard + " cards remaining.");

                }
                turn = main.changeTurn(turn);
            } else {                                      // Fail
                result = false;
                System.out.println("****** Fail *******");
                roleResult = "Fail";
                if(player[turn].getInExtra()){
                    player[turn].increaseMoney(1);
                    System.out.println("You received 1 dollar.");
                }
                turn = main.changeTurn(turn);
            }
        }
        else {
            System.out.println("You do not have role yet!");
            moveMsg = "Please choose a role first";
        }
        return result;
    }


    public void mouseClicked() {
        if (isInButton(1035, 620, 1135, 660)) {
            buttonColor = color(143, 68, 19);
            println("Click Rehearse");

            if (!Objects.equals(player[turn].getRole(), "Unemployed")) {
                player[turn].rehearse();
                System.out.println("Your current rehearse bonus is: " + player[turn].getRehearseBonus());
                turn = main.changeTurn(turn);
            }
            else {
                System.out.println("You have to have a role first!");
            }

        }

        if (isInButton(1155, 620, 1255, 660)) {
            buttonColor = color(143, 68, 19);
            println("Click Skip");
            turn = main.changeTurn(turn);
        }

        if (isInButton(1035, 675, 1255, 715)) {
            buttonColor = color(143, 68, 19);
            println("Click Act");
            act();
        }
    }

    public boolean isInButton(int x1, int y1, int x2, int y2) {
        return ((mouseX > x1) && (mouseX < x2) && (mouseY > y1) && (mouseY < y2));
    }


    public void turnBased() {


        if (player[0].getRole().equals("Unemployed") && (turn == 0) && isInCircle(player[0].xpos, player[0].ypos)) {
            cursor(HAND);

            if ((mousePressed && isInWindow())) {
                pressed = true;
                fill(color(150, 25, 25));
                player[0].xpos = mouseX;
                player[0].ypos = mouseY;
            } else {
                fill(color(50, 25, 25));
            }

        } else {
            fill(color(200, 25, 25));
            cursor(ARROW);
        }
        // Player1
        ellipse(player[0].xpos, player[0].ypos, playerSize, playerSize);

        if (player[1].getRole().equals("Unemployed") && (turn == 1) && isInCircle(player[1].xpos, player[1].ypos)) {
            cursor(HAND);

            if ((mousePressed && isInWindow())) {
                pressed = true;
                fill(color(25, 25, 150));
                player[1].xpos = mouseX;
                player[1].ypos = mouseY;
            } else {
                fill(color(25, 25, 80));
            }

        } else {
            fill(color(25, 25, 255));
            cursor(ARROW);
        }
        // Player2
        ellipse(player[1].xpos, player[1].ypos, playerSize, playerSize);

    }

    public boolean isInCircle(int playerX, int playerY) {
        return(dist(playerX, playerY, mouseX, mouseY) < playerSize/2);
    }

    public boolean isInWindow() {

        return ((mouseY > 0 && mouseY < height) && (mouseX > 0 && mouseX < width));
    }

    public void mouseDragged() {
        if(mousePressed && isInWindow() && pressed) {
            strokeWeight(5);
            player[turn].xpos = mouseX;
            player[turn].ypos = mouseY;
        }
        else {
            strokeWeight(2);
        }
    }

    public void showCard() {

        if (!pressed) {
            // Card on MainStreet
            // The stroke and line functions are highlighting the scenes when the mouse hovers over them
            if (mouseX > 800 && mouseX < 966 && mouseY > 28 && mouseY < 122) {
                cardLocation = "Main Street";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(797, 25, 968, 25);
                line(797, 124, 968, 124);
                line(797, 25, 797, 124);
                line(968, 25, 968, 124);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 530 && mouseX < 694 && mouseY > 232 && mouseY < 326) { // Card on Saloon
                cardLocation = "Saloon";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(525, 229, 697, 229);
                line(525, 328, 697, 328);
                line(525, 229, 525, 328);
                line(697, 229, 697, 328);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 520 && mouseX < 690 && mouseY > 400 && mouseY < 496) { // Card on Bank
                cardLocation = "Bank";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(518, 398, 690, 398);
                line(518, 398, 518, 497);
                line(518, 497, 690, 497);
                line(690, 398, 690, 497);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 523 && mouseX < 688 && mouseY > 610 && mouseY < 705) { // Card on Church
                cardLocation = "Church";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(518, 607, 690, 607);
                line(518, 607, 518, 706);
                line(518, 706, 690, 706);
                line(690, 607, 690, 706);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 870 && mouseX < 970 && mouseY > 400 && mouseY < 570) { // Card on Hotel
                cardLocation = "Hotel";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(869, 399, 968, 399);
                line(869, 399, 869, 570);
                line(869, 570, 968, 570);
                line(968, 399, 968, 570);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 232 && mouseX < 400 && mouseY > 30 && mouseY < 125) { // Card on Jail
                cardLocation = "Jail";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(230, 24, 402, 24);
                line(230, 24, 230, 123);
                line(230, 123, 402, 123);
                line(402, 24, 402, 123);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 25 && mouseX < 123 && mouseY > 180 && mouseY < 350) { // Card on Train Station
                cardLocation = "Train Station";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(24, 179, 123, 179);
                line(24, 179, 24, 351);
                line(24, 351, 123, 351);
                line(123, 179, 123, 351);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 300 && mouseX < 470 && mouseY > 235 && mouseY < 330) { // Card on GeneralStore
                cardLocation = "General Store";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(300, 230, 472, 230);
                line(300, 230, 300, 329);
                line(300, 329, 472, 329);
                line(472, 230, 472, 329);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 209 && mouseX < 377 && mouseY > 400 && mouseY < 500) { // Card on Ranch
                cardLocation = "Ranch";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(205, 398, 377, 398);
                line(205, 398, 205, 497);
                line(205, 497, 377, 497);
                line(377, 398, 377, 497);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
            else if (mouseX > 27 && mouseX < 195 && mouseY > 607 && mouseY < 705) { // Card on Hideout
                cardLocation = "Secret Hideout";
                strokeWeight(6);
                stroke(153, 88, 14, 255);
                line(24, 605, 196, 605);
                line(24, 605, 24, 704);
                line(24, 704, 196, 704);
                line(196, 605, 196, 704);
                noStroke();
                if(mousePressed) {
                    checkCardLocation();
                }
            }
        }
    }

    public void checkCardLocation() {
        PImage cardImage;

        for (int i = 0; i < rooms.size(); i++) {

            if (rooms.get(i).name.equals(cardLocation)) {
                if(rooms.get(i).getCard() != null) {
                    String imageCard = rooms.get(i).getCard().getImage();

                    if (!imageCard.equals("")) {
                        cardImage = loadImage(imageCard);
                        image(cardImage, 196, 198);
                    }
                }
                else {
                    println("No card anymore");
                    moveMsg = "No card anymore";
                }


            }
        }
    }

    public boolean chooseRole() {
        boolean roleChose = false;
        // Main Street Role
        for (int i = 0; i < rooms.size(); i++) {
            if(player[turn].getPlayerLocation().equals(rooms.get(i).name)) {
                if (player[turn].getPlayerLocation().equals("Main Street") && rooms.get(i).getCard() != null) {
                    roleChose = roleMainStreet();
                } else if (player[turn].getPlayerLocation().equals("Saloon") && rooms.get(i).getCard() != null) {
                    roleChose = roleSaloon();
                } else if (player[turn].getPlayerLocation().equals("Bank") && rooms.get(i).getCard() != null) {
                    roleChose = roleBank();
                } else if (player[turn].getPlayerLocation().equals("Hotel") && rooms.get(i).getCard() != null) {
                    roleChose = roleHotel();
                } else if (player[turn].getPlayerLocation().equals("Church") && rooms.get(i).getCard() != null) {
                    roleChose = roleChurch();
                } else if (player[turn].getPlayerLocation().equals("Jail") && rooms.get(i).getCard() != null) {
                    roleChose = roleJail();
                } else if (player[turn].getPlayerLocation().equals("Train Station") && rooms.get(i).getCard() != null) {
                    roleChose = roleTrainStation();
                } else if (player[turn].getPlayerLocation().equals("General Store") && rooms.get(i).getCard() != null) {
                    roleChose = roleGeneralStore();
                } else if (player[turn].getPlayerLocation().equals("Ranch") && rooms.get(i).getCard() != null) {
                    roleChose = roleRanch();
                } else if (player[turn].getPlayerLocation().equals("Secret Hideout") && rooms.get(i).getCard() != null) {
                    roleChose = roleSecretHideout();
                }
            }
        }

            if (roleChose) {
                player[turn].setInExtra(true);
            }
        return roleChose;
    }

    public boolean roleChurch() {
        boolean roleChose = false;
        String role;
        if(isInButton(713,609,745,642)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Church")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for(int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(729, 626);
                        } else {
                            System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                        }
                    }
                }
            }
        }
        else if(isInButton(714,675,745,707)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Church")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(729, 626);
                        }
                        else{
                            System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                        }
                    }
                }
            }
        }
        return roleChose;
    }

    public boolean roleHotel() {
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(822,633,855,666)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Hotel")) {
                    role = rooms.get(i).getExtraRoleTitle(2);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(2);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(2));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(837, 649);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(2) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(853,686,886,719)) {
            //updatePlayerLoc(870, 703);
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Hotel")) {
                    role = rooms.get(i).getExtraRoleTitle(3);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(3);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(3));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(869, 702);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(3) + " is unavailable to you.");
                    }
                }
            }
        }

        else if(isInButton(891,632,926,665)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Hotel")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(907, 648);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(924,687,957,719)) {
            //updatePlayerLoc(941, 703);
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Hotel")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(941, 703);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public boolean roleBank() {
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(757,400,788,433)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Bank")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(773, 416);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(757,469,788,500)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Bank")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(773, 484);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public boolean roleSaloon() {
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(729,230,763,264)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Saloon")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(746, 247);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(729,294,763,326)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Saloon")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(746, 309);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public boolean roleMainStreet() {
        boolean roleChose = false;
        String role;
        if(isInButton(537,28,570,60)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Main Street")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(552, 43);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(537,94,570,126)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Main Street")) {
                    role = rooms.get(i).getExtraRoleTitle(2);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(2);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(2));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(552, 109);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(2) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(602,27,635,60)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Main Street")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(618, 43);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(602,93,635,127)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Main Street")) {
                    role = rooms.get(i).getExtraRoleTitle(3);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(3);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(3));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(618, 109);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(3) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public boolean roleJail(){
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(429,29,458,59)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Jail")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(746, 247);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(429,95,458,126)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Jail")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(746, 309);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public boolean roleGeneralStore(){
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(201,233,231,263)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("General Store")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(215, 248);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(201,300,231,329)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("General Store")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(215, 314);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public boolean roleTrainStation(){
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(34,40,63,72)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Train Station")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(48, 56);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(67,92,96,121)) {
            //updatePlayerLoc(870, 703);
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Train Station")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(81, 107);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }

        else if(isInButton(108,40,138,72)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Train Station")) {
                    role = rooms.get(i).getExtraRoleTitle(2);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(2);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(2));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(123, 56);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(2) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(136,93,167,124)) {
            //updatePlayerLoc(941, 703);
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Train Station")) {
                    role = rooms.get(i).getExtraRoleTitle(3);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(3);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(3));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(152, 108);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(3) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;

    }

    public boolean roleRanch(){
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(343,514,373,541)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Ranch")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(358, 527);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(403,445,434,474)) {
            //updatePlayerLoc(870, 703);
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Ranch")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(418,459);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }

        else if(isInButton(403,511,434,543)) {

            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Ranch")) {
                    role = rooms.get(i).getExtraRoleTitle(2);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(2);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(2));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(418, 526);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(2) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;

    }

    public boolean roleSecretHideout(){
        boolean roleChose = false;
        String role = player[turn].getRole();
        if(isInButton(361,602,392,633)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Secret Hideout")) {
                    role = rooms.get(i).getExtraRoleTitle(0);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(0);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(0));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(376, 616);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(0) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(361,673,392,704)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Secret Hideout")) {
                    role = rooms.get(i).getExtraRoleTitle(2);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(2);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(2));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(376, 688);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(2) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(431,602,462,633)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Secret Hideout")) {
                    role = rooms.get(i).getExtraRoleTitle(1);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(1);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(1));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(445, 616);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(1) + " is unavailable to you.");
                    }
                }
            }
        }
        else if(isInButton(430,673,459,703)) {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).name.equals("Secret Hideout")) {
                    role = rooms.get(i).getExtraRoleTitle(3);
                    for (int j = 0; j < rooms.get(i).getNumberOfExtraRoles(); j++) {
                        if (rooms.get(i).getExtraRoles().get(j).getRoleTitle().equals(role) && player[turn].getRank() >= rooms.get(i).getRankRequiredForRole(j) && rooms.get(i).getExtraRoles().get(j).isAvailable()) {
                            role = rooms.get(i).getExtraRoleTitle(3);
                            rooms.get(i).addExtraPlayer(player[turn]);
                            player[turn].setRole(role);
                            System.out.println("Congratulations! You accepted the role: " + rooms.get(i).getExtraRoleTitle(3));
                            roleChose = true;
                            rooms.get(i).getExtraRoles().get(j).setUnavailable();
                            updatePlayerLoc(618, 109);
                        }
                    }
                    if(!roleChose){
                        System.out.println("The role " + rooms.get(i).getExtraRoleTitle(3) + " is unavailable to you.");
                    }
                }
            }
        }

        return roleChose;
    }

    public void shotCounterDraw() {

        // Main Street
        if (rooms.get(1).getShot() == 2) {
            ellipse(686,44,37,37);
        }
        else if(rooms.get(1).getShot() == 1) {
            ellipse(686,44,37,37);
            ellipse(729,44,37,37);
        }
        else if (rooms.get(1).getShot() == 0){
            ellipse(686,44,37,37);
            ellipse(729,44,37,37);
            ellipse(773,44,37,37);
        }
        // Saloon
        if (rooms.get(0).getShot() == 1) {
            ellipse(542,199,37,37);
        }
        else if(rooms.get(0).getShot() == 0) {
            ellipse(542,199,37,37);
            ellipse(586,199,37,37);
        }
        // Bank
        if (rooms.get(4).getShot() == 0) {
            ellipse(715,480,37,37);
        }
        // Church
        if (rooms.get(5).getShot() == 1) {
            ellipse(588,582,37,37);
        }
        else if (rooms.get(5).getShot() == 0) {
            ellipse(588,582,37,37);
            ellipse(540,582,37,37);
        }
        // Hotel
        if (rooms.get(6).getShot() == 2) {
            ellipse(859,598,37,37);
        }
        else if (rooms.get(6).getShot() == 1) {
            ellipse(859,598,37,37);
            ellipse(905,598,37,37);
        }
        else if (rooms.get(6).getShot() == 0) {
            ellipse(859,598,37,37);
            ellipse(905,598,37,37);
            ellipse(949,598,37,37);
        }
        // Jail
        if (rooms.get(8).getShot() == 0) {
            ellipse(382,151,37,37);
        }
        // Train Station
        if (rooms.get(7).getShot() == 2) {
            ellipse(128,154,37,37);
        }
        if (rooms.get(7).getShot() == 1) {
            ellipse(128,154,37,37);
            ellipse(86,154,37,37);
        }
        if (rooms.get(7).getShot() == 0) {
            ellipse(128,154,37,37);
            ellipse(86,154,37,37);
            ellipse(44,154,37,37);
        }
        // General Store
        if (rooms.get(9).getShot() == 1) {
            ellipse(278,291,37,37);
        }
        else if (rooms.get(9).getShot() == 0) {
            ellipse(278,291,37,37);
            ellipse(278,248,37,37);
        }
        // Ranch
        if (rooms.get(2).getShot() == 1) {
            ellipse(449,418,37,37);
        }
        else if (rooms.get(2).getShot() == 0) {
            ellipse(449,418,37,37);
            ellipse(405,418,37,37);
        }
        //Secret Hideout
        if (rooms.get(3).getShot() == 2) {
            ellipse(311,653,37,37);
        }
        else if (rooms.get(3).getShot() == 1) {
            ellipse(311,653,37,37);
            ellipse(266,653,37,37);
        }
        else if (rooms.get(3).getShot() == 0) {
            ellipse(311,653,37,37);
            ellipse(266,653,37,37);
            ellipse(222,653,37,37);
        }

    }

    void updatePlayerLoc(int x, int y) {
        player[turn].xpos = x;
        player[turn].ypos = y;
        player[turn].prevLocationX = x;
        player[turn].prevLocationY = y;
    }

    public void mouseReleased() {
        boolean moved = false;
        boolean roleChose;
        if(pressed) {
            pressed = false;
            String place = player[turn].getPlayerLocation();

            roleChose = chooseRole();

            if(!roleChose) {
                if (isInMainStreet() && isAdj("Main Street", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Main Street.";
                    place = "Main Street";

//                    player[turn].xpos = 844;
//                    player[turn].ypos = 74;
                    player[turn].xpos = 760;
                    player[turn].ypos = 145;

                } else if (isInTrailers() && isAdj("Trailers", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Trailers.";
                    place = "Trailers";
                    player[turn].xpos = 871;
                    player[turn].ypos = 246;
                } else if (isInHotel() && isAdj("Hotel", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Hotel.";
                    place = "Hotel";
//                    player[turn].xpos = 916;
//                    player[turn].ypos = 534;
                    player[turn].xpos = 810;
                    player[turn].ypos = 595;
                } else if (isInBank() && isAdj("Bank", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Bank.";
                    place = "Bank";
//                    player[turn].xpos = 560;
//                    player[turn].ypos = 450;
                    player[turn].xpos = 720;
                    player[turn].ypos = 420;
                } else if (isInChurch() && isAdj("Church", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Church.";
                    place = "Church";
//                    player[turn].xpos = 656;
//                    player[turn].ypos = 659;
                    player[turn].xpos = 647;
                    player[turn].ypos = 577;
                } else if (isInSaloon() && isAdj("Saloon", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Saloon.";
                    place = "Saloon";
//                    player[turn].xpos = 586;
//                    player[turn].ypos = 282;
                    player[turn].xpos = 630;
                    player[turn].ypos = 200;
                } else if (isInSecretHideout() && isAdj("Secret Hideout", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Secret Hideout";
                    place = "Secret Hideout";
//                    player[turn].xpos = 73;
//                    player[turn].ypos = 660;
                    player[turn].xpos = 240;
                    player[turn].ypos = 700;
                } else if (isInRanch() && isAdj("Ranch", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Ranch.";
                    place = "Ranch";
//                    player[turn].xpos = 273;
//                    player[turn].ypos = 449;
                    player[turn].xpos = 255;
                    player[turn].ypos = 550;
                } else if (isInGeneralStore() && isAdj("General Store", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to General Store.";
                    place = "General Store";
//                    player[turn].xpos = 438;
//                    player[turn].ypos = 278;
                    player[turn].xpos = 270;
                    player[turn].ypos = 335;
                } else if (isInJail() && isAdj("Jail", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Jail.";
                    place = "Jail";
//                    player[turn].xpos = 286;
//                    player[turn].ypos = 75;
                    player[turn].xpos = 335;
                    player[turn].ypos = 172;
                } else if (isInTrainStation() && isAdj("Train Station", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Train Station.";
                    place = "Train Station";
//                    player[turn].xpos = 70;
//                    player[turn].ypos = 232;
                    player[turn].xpos = 170;
                    player[turn].ypos = 167;
                } else if (isInCastingOffice() && isAdj("Casting Office", player[turn].getPlayerLocation())) {
                    moved = true;
                    moveMsg = "Moved to Casting Office.";
                    place = "Casting Office";
                    player[turn].xpos = 61;
                    player[turn].ypos = 417;
                } else {
                    moveMsg = "You can't move\nthere!";
                }
            }

            if (moved) {
                player[turn].prevLocationX = player[turn].xpos;
                player[turn].prevLocationY = player[turn].ypos;
                player[turn].setPlayerLocation(place);
                turn = main.changeTurn(turn);
            } else if (roleChose) {
                turn = main.changeTurn(turn);
            } else {
                player[turn].xpos = player[turn].prevLocationX;
                player[turn].ypos = player[turn].prevLocationY;
            }

        }
    }


    boolean isInCastingOffice() {
        return (mouseX > 13 && mouseX < 182 && mouseY > 388 && mouseY < 558 );
    }

    boolean isInTrainStation() {
        return ((mouseX > 13 && mouseX < 201 && mouseY > 13 && mouseY < 207) ||
                (mouseX > 13 && mouseX < 158 && mouseY > 207 && mouseY < 365));
    }

    boolean isInJail() {
        return (mouseX > 218 && mouseX < 485 && mouseY > 13 && mouseY < 201 );
    }

    boolean isInGeneralStore() {
        return (mouseX > 170 && mouseX < 485 && mouseY > 213 && mouseY < 365);
    }

    boolean isInRanch() {
        return (mouseX > 194 && mouseX < 485 && mouseY > 389 && mouseY < 581 );
    }

    boolean isInSecretHideout() {
        return ((mouseX > 339 && mouseX < 485 && mouseY > 587 && mouseY < 739) ||
                (mouseX > 163 && mouseX < 339 && mouseY > 593 && mouseY < 739) ||
                (mouseX > 13 && mouseX < 163 && mouseY > 569 && mouseY < 739));
    }

    boolean isInSaloon() {
        return (mouseX > 510 && mouseX < 806 && mouseY > 170 && mouseY < 365);
    }

    boolean isInChurch() {
        return (mouseX > 510 && mouseX < 770 && mouseY > 552 && mouseY < 739 );
    }

    boolean isInBank() {
        return (mouseX > 510 && mouseX < 818 && mouseY > 389 && mouseY < 540 );
    }

    boolean isInHotel() {
        return ((mouseX > 829 && mouseX < 982 && mouseY > 388 && mouseY < 739) ||
                (mouseX > 781 && mouseX < 829 && mouseY > 576 && mouseY < 739));
    }

    boolean isInTrailers() {
        return (mouseX > 818 && mouseX < 982 && mouseY > 206 && mouseY < 364 );
    }

    boolean isInMainStreet() {
        return (mouseX > 510 && mouseX < 982 && mouseY > 13 && mouseY < 192 );
    }

}