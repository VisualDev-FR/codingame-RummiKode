import java.util.*;
import java.io.*;
import java.math.*;

import com.codingame.game.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            String[] cardsInHand = in.nextLine().split(" "); // <CardNumber1_CardColor1> <CardNumber1_CardColor2> ...
            System.err.println("Hand (" + cardsInHand.length + ") : \n" + String.join("\n", cardsInHand));

            int playersCount = in.nextInt(); // the total number of player
            for (int i = 0; i < playersCount; i++) {
                int playerScore = in.nextInt(); // the score of the player i (your playerIndex will always be 0)
            }
            int stackCount = in.nextInt(); // the number of stacks pushed in the game, at the begin of the game, it always equals to 0
            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int i = 0; i < stackCount; i++) {
                String stack = in.nextLine(); // <stack_id> <stack_type> <card_1> <card_2> ...
            }

            // Write an answer using System.out.println()
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