import java.util.*;
import java.io.*;
import java.math.*;

/**
 * a card is always given by his number (two digits), an underscore and his color. ex : 08_BLUE
 * the format you give the card number doesn't matter, but you must give the number before the color, and separate them by an underscore
 * you don't have to sort your cards or your stacks to send your outputs instructions
 **/
class Waiter {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int myPlayerIndex = in.nextInt(); // the index of your player
            int playersCount = in.nextInt(); // the total amount of players
            int stacksCount = in.nextInt(); // the amount of stacks pushed in the game
            int drawCardsCount = in.nextInt(); // the amount of cards left in the common draw
            for (int i = 0; i < playersCount; i++) {
                int nbCards = in.nextInt(); // the amount of cards for player i
                int actionsLeft = in.nextInt(); // the remaining actions of the player i
                String cards = in.next(); // a comma-sparated string containing all player i cards : <Card1>;<Card2>;... ex : "08_BLUE;01_GREEN;..."
            }
            in.nextLine();
            for (int i = 0; i < stacksCount; i++) {
                String stack = in.nextLine(); // a string containing the description of a stack : <stack_id> <stack_type> <card_1> <card_2> ...
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // WAIT
            // TAKE <stackID> <cardCode>
            // ADD <stackID> <cardCode>
            // PUSH <cardCode1>, <cardCode2>...
            // SPLIT <cardCode_1> <cardCode_2>
            // JOIN <stackID_1> <stackID_2>
            // MOVE <stackID_From> <stackID_To> <cardCodeFrom>
            System.out.println("WAIT");
        }
    }
}