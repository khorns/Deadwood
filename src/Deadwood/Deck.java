/*
    Created by Alexi Mellovich
    02/18/16
    deck.java
    This class will build and hold the information of
    all the cards in deadwood.
*/
package Deadwood;
import java.util.*;
import java.io.*;
import java.lang.String;
import java.lang.Integer;

public class Deck {
    private ArrayList<Card> deck;

    public void buildDeck() {
      try {
         ArrayList<Card> deck  = new ArrayList<Card>();
         ArrayList<StarringRole> roles;
         Scanner input = new Scanner(new File ("cards.txt"));
         String line;
         String name;
         String description = "";
         String charName;
         String charLine;
         int budget;
         int numOfRoles;
         int rank;
         deck  = new ArrayList<Card>();

         while(input.hasNextLine()){
            //Card Name
            input.nextLine();
            name = input.nextLine();
            //Scene description
            description = input.nextLine();
            //Budget
            line = input.nextLine();
            budget = Integer.parseInt(line);
            //number of roles
            line = input.nextLine();
            numOfRoles = Integer.parseInt(line);

            roles = new ArrayList<StarringRole>();
            for(int i = 0; i < numOfRoles;  i++) {
               //rank of the role
               line = input.nextLine();
               rank = Integer.parseInt(line);
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
}
