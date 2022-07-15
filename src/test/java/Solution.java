import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            String cardsInHand = in.nextLine();
            int playersCount = in.nextInt();
            for (int i = 0; i < playersCount; i++) {
                int playerScore = in.nextInt();
            }
            int stackCount = in.nextInt();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int i = 0; i < stackCount; i++) {
                String stack = in.nextLine();
            }

            // Write an answer using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // WAIT
            // TAKE <stackID> <cardCode>
            // ADD <stackID> <cardCode>
            // PUSH <cardCode1>, <cardCode2>...
            // SPLIT <stackID> <cardCode_1> <cardCode_2>
            // JOIN <stackID_1> <stackID_2>
            // MOVE <stackID_From> <stackID_To> <cardCodeFrom>
            System.out.println("WAIT");
        }
    }
}