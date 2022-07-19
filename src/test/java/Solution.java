import java.util.*;

import com.codingame.game.card.Card;
import com.codingame.game.card.CardColors;
import com.codingame.game.stack.StackColor;
import com.codingame.game.stack.StackSequence;
import com.codingame.game.stack.StackType;

class Solution {

    public static int playerScore;
    public static boolean pushedFirstSequence;

    public static void main(String args[]) throws Exception {
        
        Scanner in = new Scanner(System.in);

        while (true) {
            
            Player myPlayer = new Player();
            Game game = new Game();

            int cardsInHandCount = in.nextInt();
            System.err.println("CardCount = " + cardsInHandCount);
            
            for (int i = 0; i < cardsInHandCount; i++) {
                String card = in.next(); // <CardNumber_CardColor> ex : "08_BLUE"
                myPlayer.addCard(card);
            }
            
            int playersCount = in.nextInt(); // the total number of player
            for (int i = 0; i < playersCount; i++) {
                playerScore = in.nextInt(); // the score of the player i (your playerIndex will always be 0)
            }

            int n = in.nextInt(); if (in.hasNextLine()){in.nextLine();}            
            
            for (int i = 0; i < n; i++) {                
                String[] stack = in.nextLine().split(" "); // <stack_id> <stack_type> <card_1> <card_2> ...
                
                System.err.println(String.join(" ", stack));

                int stackID = Integer.parseInt(stack[0]);
                StackType stackType = StackType.values()[Integer.parseInt(stack[1])];
                List<Card> cards = new ArrayList<Card>();

                for (int j = 2; j < stack.length; j++) {
                    
                    cards.add(new Card(stack[j]));
                }

                game.createStack(stackID, stackType, cards);
            }

            String pushAction = myPlayer.getPushAction();

            if(pushAction != null){
                System.out.println(pushAction);
                pushedFirstSequence = true;
            }else if(pushedFirstSequence){

                String addAction = myPlayer.getAddAction(game);

                if(addAction != null){
                    System.out.println(addAction);
                }else{
                    System.out.println("WAIT");
                }
            
            }else{
                System.out.println("WAIT");
            }
        }
    }

    public static class Player{

        public Stack<String> cardsInHand;
        public int remainingActions;
        public boolean mustDraw;
        public boolean pushedFirstSequence;

        public Player(){
            this.cardsInHand = new Stack<String>();
            this.remainingActions = 5;
            this.mustDraw = false;
            this.pushedFirstSequence = false;
        }

        public void addCard(String strCard){
            this.cardsInHand.add(strCard);
        }

        public String getAddAction(Game game){

            for(StackSequence sequence : game.sequenceStacks.values()){
                for(String strCard : this.cardsInHand){
                    if(sequence.canAdd(new Card(strCard))) return String.format("ADD %s %s", sequence.getID(), strCard);
                }
            }
    
            for(StackColor colorStack : game.colorStacks.values()){
                for(String strCard : this.cardsInHand){
                    if(colorStack.canAdd(new Card(strCard))) return String.format("ADD %s %s", colorStack.getID(), strCard);
                }
            }
    
            return null;
        }
    
        public String getPushAction(){
            
            for(String strCard : this.cardsInHand){
                
                List<Card> cards = new ArrayList<Card>();

                cards = getColorStack(new Card(strCard));
                cards = getSequenceStack(new Card(strCard));

                if(cards != null){

                    String[] strCards = new String[cards.size()];

                    for (int i = 0; i < strCards.length; i++) {
                        strCards[i] = cards.get(i).getHashCode();
                    }

                    return String.format("PUSH %s", String.join(" ", strCards));
                
                }
            }
            return null;
        }

        public List<Card> getSequenceStack(Card card){
        
            int cardNumber = card.getNumber();
            CardColors cardColor = card.getColor();

            List<Card> cards = new ArrayList<Card>();
            
            if(this.hasThisCard(new Card(cardColor, cardNumber + 1)) && this.hasThisCard(new Card(cardColor, cardNumber - 1))){
                cards.add(new Card(cardColor, cardNumber - 1));
                cards.add(new Card(cardColor, cardNumber));
                cards.add(new Card(cardColor, cardNumber + 1));
                return cards;
            }
            else if(this.hasThisCard(new Card(cardColor, cardNumber + 1)) && this.hasThisCard(new Card(cardColor, cardNumber + 2))){
                cards.add(new Card(cardColor, cardNumber));
                cards.add(new Card(cardColor, cardNumber + 1));
                cards.add(new Card(cardColor, cardNumber + 2));
                return cards;
            }
            else if(this.hasThisCard(new Card(cardColor, cardNumber - 1)) && this.hasThisCard(new Card(cardColor, cardNumber - 2))){
                cards.add(new Card(cardColor, cardNumber - 2));
                cards.add(new Card(cardColor, cardNumber - 1));
                cards.add(new Card(cardColor, cardNumber));
                return cards;
            }

            return null;            
        }
    
        public List<Card> getColorStack(Card card){
    
            int cardNumber = card.getNumber();
    
            List<CardColors> missingColors = new ArrayList<CardColors>();
    
            for(CardColors color : CardColors.values()){
                if(color != card.getColor()){
                    missingColors.add(color);
                }
            }
    
            List<Card> cards = new ArrayList<Card>();

            if(this.hasThisCard(new Card(missingColors.get(0), cardNumber)) && this.hasThisCard(new Card(missingColors.get(1), cardNumber))){
                cards.add(new Card(card.getColor(), cardNumber));
                cards.add(new Card(missingColors.get(0), cardNumber));
                cards.add(new Card(missingColors.get(1), cardNumber));
                return cards;
            }else if(this.hasThisCard(new Card(missingColors.get(0), cardNumber)) && this.hasThisCard(new Card(missingColors.get(2), cardNumber))){
                cards.add(new Card(card.getColor(), cardNumber));
                cards.add(new Card(missingColors.get(0), cardNumber));
                cards.add(new Card(missingColors.get(2), cardNumber));
                return cards;
            }else if(this.hasThisCard(new Card(missingColors.get(1), cardNumber)) && this.hasThisCard(new Card(missingColors.get(2), cardNumber))){
                cards.add(new Card(card.getColor(), cardNumber));
                cards.add(new Card(missingColors.get(1), cardNumber));
                cards.add(new Card(missingColors.get(2), cardNumber));
                return cards;
            }

            return null;
        }

        public  boolean hasThisCard(Card card){
            return this.cardsInHand.contains(card.getHashCode());
        }   

    }

    public static class Game{

        public Map<Integer, StackSequence> sequenceStacks;  // HashMap wich will contain all sequenceStacks pushed in the game with their unique id
        public Map<Integer, StackColor> colorStacks;        // HashMap wich will contain all colorStacks pushed in the game with their unique id
        public Map<Integer, StackType> stacks;              // HashMap wich will contain all stackIDs pushed in the game with their stackType
        
        public Game(){
            stacks = new HashMap<Integer, StackType>();        
            sequenceStacks = new HashMap<Integer, StackSequence>();
            colorStacks = new HashMap<Integer, StackColor>();
        }

        public void createStack(int stackID, StackType type, List<Card> cards) throws Exception{

            if(type == StackType.SEQUENCE){            
                sequenceStacks.put(stackID, new StackSequence(stackID, cards));
                stacks.put(stackID, type);            
            }else if(type == StackType.COLOR){
                colorStacks.put(stackID, new StackColor(stackID, cards));
                stacks.put(stackID, type);                
            }else{
                throw new Exception("Type non reconnu par le joueur");
            }

        }
        
    }
}