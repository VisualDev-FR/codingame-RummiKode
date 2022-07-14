import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Stub {

    public static void main(String args[]){
        
        Scanner in = new Scanner(System.in);
        
        int playersCount = in.nextInt();
        
        if (in.hasNextLine()) {
            in.nextLine();
        }

        // game loop
        while (true) {
            String cardsInHand = in.nextLine();
            for (int i = 0; i < playersCount; i++) {
                int playerScore = in.nextInt();
            }
            in.nextLine();
        }
    }
}