import java.util.ArrayList;
import java.util.List;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.game.card.*;
import com.codingame.game.stack.StackSequence;

public class SkeletonMain {
    public static void main(String[] args) {

        playRummiKode();
        //assertionTest();

    }

    static void playRummiKode(){

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Solution.class);
        gameRunner.addAgent(Solution.class);

        //gameRunner.setSeed(6335831626512785165L);

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
}
