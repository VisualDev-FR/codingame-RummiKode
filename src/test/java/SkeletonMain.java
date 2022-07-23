import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.game.Player;
import com.codingame.game.card.*;
import com.codingame.game.stack.StackSequence;

public class SkeletonMain {
    public static void main(String[] args) {

        playRummiKode();
        //canMakeSequenceStack_TEST();
        //canMakeColorStack_TEST();

        //stackTest();
        //testColors();
    }

    public static void testColors(){

        System.err.println(CardColors.valueOf("black"));
    }

    static void playRummiKode(){

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Boss2.class);
        gameRunner.addAgent(Boss2.class);
        gameRunner.addAgent(Boss2.class);
        gameRunner.addAgent(Boss2.class);

        gameRunner.setSeed(3453794771752566333L); // referee doesn't give score to player 0

        gameRunner.start();

    }

    static void assertionTest(){

        for(int i = 0; i < 10; i++){            
            assert i < 5 : "i should be less than 5";
            System.out.println(i);
        }

    }

    static void takableTest(){

        // verify that the takable cards are correctly computed
        // test completed 15/07/2022

        List<Card> list = new ArrayList<Card>();

        final int LENGHT = 14;

        for(int lengh = 1; lengh < LENGHT; lengh++){

            for (int i = 0; i < lengh; i++) {
                list.add(new Card(CardColors.BLUE, i));
            }
    
            StackSequence sequence = new StackSequence(7, list);
    
            List<Card> takableCards = sequence.getTakableCards();
    
            System.err.println("Sequence : " + sequence.getString());
            System.err.println("Takable  : " + cardListToString(takableCards));
            System.err.println(String.format("Lenght = %s, Takable Count = %s\n", lengh, takableCards.size()));
        }
    }

    static void sortingTest(){

        List<Card> list = new ArrayList<Card>();

        //list.add(new Card(CardColors.GREEN, 7).getHashCode());
        //list.add(new Card(CardColors.YELLOW, 3).getHashCode());
        
        list.add(new Card(CardColors.BLUE, 6));
        list.add(new Card(CardColors.BLUE, 7));        

        StackSequence sequence = new StackSequence(26, list);

        System.err.println(sequence.getInputs());

        /* for(Card card : sequence.getCards().values()){

            System.err.println(card.getHashCode());
        }

        System.err.println(sequence.isValid()); */
    }

    static String cardListToString(List<Card> cardList){

        List<String> strList = new ArrayList<String>();

        for(Card card : cardList){
            strList.add(card.getHashCode());
        }

        return String.join(" ", strList);
    }

    static void canMakeSequenceStack_TEST(){

        Player player = new Player();
        player.init();

        player.addCardInHand(new Card(CardColors.BLUE, 1));
        player.addCardInHand(new Card(CardColors.BLUE, 2));
        player.addCardInHand(new Card(CardColors.BLUE, 11));
        player.addCardInHand(new Card(CardColors.BLUE, 10));

        System.err.println(player.canMakeSequenceStack(new Card(CardColors.BLUE, 0)));
    }

    static void canMakeColorStack_TEST(){

        Player player = new Player();
        player.init();

        player.addCardInHand(new Card(CardColors.BLUE, 5));
        player.addCardInHand(new Card(CardColors.GREEN, 5));
        player.addCardInHand(new Card(CardColors.YELLOW, 7));

        System.err.println(player.canMakeColorStack(new Card(CardColors.BLUE, 15)));
    }    

    static void stackTest(){

        Stack<String> strStack = new Stack<String>();

        String str1 = "String1";
        String str2 = "String3";
        String str3 = "String2";

        String str4 = "String1";
    
        strStack.add(str1);
        strStack.add(str2);
        strStack.add(str3);

        System.err.println(strStack.contains(str4));
    }

}
