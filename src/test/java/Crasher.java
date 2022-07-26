import java.util.*;
import java.io.*;
import java.math.*;

/**
 * a card is always given by his number (two digits), an underscore and his color. ex : 08_BLUE
 * the format you give the card number doesn't matter, but you must give the number before the color, and separate them by an underscore
 * you don't have to sort your cards or your stacks to send your outputs instructions
 **/
class Crasher {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int myPlayerIndex = in.nextInt(); // the index of your player
            int playersCount = in.nextInt(); // the total amount of players
            int stacksCount = in.nextInt(); // the amount of stacks pushed in the game
            int drawCardsCount = in.nextInt(); // the amount of cards left in the common draw
            for (int i = 0; i < playersCount; i++) {
                int playerIndex = in.nextInt();
                int nbCards = in.nextInt(); // the amount of cards for player i
                int actionsLeft = in.nextInt(); // the remaining actions of the player i
                String cards = in.next(); // a comma-sparated string containing all player i cards : <Card1>;<Card2>;... ex : "08_BLUE;01_GREEN;..."
            }
            in.nextLine();
            for (int i = 0; i < stacksCount; i++) {
                String stack = in.nextLine(); // a string containing the description of a stack : <stack_id> <stack_type> <card_1> <card_2> ...
            }

            if(drawCardsCount <= 0){
               crash_SOFT();
            }

            System.out.println("WAIT");
        }
    }

    public static void crash_HARD(){
        int o = 1;
        while(true){
            System.err.println("YOL" + new String(new char[o]).replace("\0", "O"));
            o++;
        } 
    }

    public static void crash_SOFT(){
        int yolo = Integer.parseInt("YOLOOOOO");
    }
}