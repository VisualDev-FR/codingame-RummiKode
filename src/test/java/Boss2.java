import java.util.*;

import com.codingame.game.card.Card;
import com.codingame.game.card.CardColors;
import com.codingame.game.stack.StackColor;
import com.codingame.game.stack.StackSequence;
import com.codingame.game.stack.StackType;

class Boss2 {

    public static int actionsLeft;
    public static int playerScore;
    public static boolean pushedFirstSequence;
    public static List<Player> players;
    private static Scanner in;

    public static void main(String args[]) throws Exception {

        in = new Scanner(System.in);

        List<String> playedActions = new ArrayList<String>();
        
        while (true) {

            players = new ArrayList<Player>();

            int myPlayerIndex = in.nextInt();   // the index of your player
            int playersCount = in.nextInt();    // the total number of player
            int stacksCount = in.nextInt();        // the number of stacks pushed in the game
            int drawCardsLeft = in.nextInt();
            
            for (int i = 0; i < playersCount; i++) {
                
                playerScore = in.nextInt();     // the score of the player i (your playerIndex will always be 0)
                actionsLeft = in.nextInt();     // the remaining actions of the player i
                
                players.add(new Player(i, playerScore, actionsLeft));

                String[] strCards = in.next().split(";");

                for (int j = 0; j < strCards.length; j++) {
                    players.get(i).addCard(strCards[j]);
                }
            }            

            Player myPlayer = players.get(myPlayerIndex);

            System.err.println("myScore = " + myPlayer.score);

            Game game = new Game();

            in.nextLine();
            for (int i = 0; i < stacksCount ; i++) {                
                
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

            System.err.println("Search action\n");

            String action = "WAIT";

            String pushAction = myPlayer.getPushAction();

            if(pushAction != null){
                
                action = pushAction;
                pushedFirstSequence = true;
                playedActions = new ArrayList<String>();
            
            }else if(pushedFirstSequence){

                String addAction = myPlayer.getAddAction(game);

                if(addAction != null){
                
                    action = addAction;
                    //playedActions = new ArrayList<String>();
                
                }else{

                    String takeAction = myPlayer.getTakeAction(game);

                    if(takeAction != null && actionsLeft > 0 && (!playedActions.contains(takeAction) || drawCardsLeft <= 0)){                
                        action = takeAction;
                        playedActions.add(takeAction);
                        myPlayer.removeOneAction();
                    }
                }
            
            }

            System.err.println("Action = " + action);

            System.out.println(action);
        }
    }

    public static class Player{

        /* 
            WAIT
            TAKE <stackID> <cardCode>
            ADD <stackID> <cardCode>
            PUSH <cardCode1>, <cardCode2>...
            SPLIT <stackID> <cardCode_1> <cardCode_2>
            JOIN <stackID_1> <stackID_2>
            MOVE <stackID_From> <stackID_To> <cardCodeFrom>
         */        

        public Stack<String> cardsInHand;
        public boolean mustDraw;
        public boolean pushedFirstSequence;
        public int actionsLeft;
        public int index;
        public int score;

        public Player(int index, int score, int actionsLeft){
            this.cardsInHand = new Stack<String>();
            this.mustDraw = false;
            this.pushedFirstSequence = false;
            this.actionsLeft = actionsLeft;
            this.score = score;
            this.index = index;
        }

        public void removeOneAction(){
            this.actionsLeft--;
        }

        public void addCard(String strCard){
            this.cardsInHand.add(strCard);
        }

        public String getAddAction(Game game){

            System.err.println("Search AddAction");

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
            
            System.err.println("No AddAction Found\n");
            return null;
        }
    
        public String getPushAction(){

            System.err.println("Search PushAction");
            
            for(String strCard : this.cardsInHand){
                
                List<Card> cards = new ArrayList<Card>();

                cards = getSequenceStack(new Card(strCard));

                if(cards == null)  cards = getColorStack(new Card(strCard));

                if(cards != null){

                    String[] strCards = new String[cards.size()];

                    for (int i = 0; i < strCards.length; i++) {
                        strCards[i] = cards.get(i).getHashCode();
                    }

                    return String.format("PUSH %s", String.join(" ", strCards));
                
                }
            }
            System.err.println("No PushAction Found\n");
            return null;
        }

        public String getTakeAction(Game game){

            System.err.println("Search TakeAction");

            for(StackSequence sequenceStack : game.sequenceStacks.values()){

                List<Card> takableCards = sequenceStack.getTakableCards();

                for(Card card : takableCards){

                    if(getSequenceStack(card) != null || getColorStack(card) != null){
                        return String.format("TAKE %s %s", sequenceStack.getID(), card.getHashCode());
                    }
                }
            }

            for(StackColor colorStack : game.colorStacks.values()){

                List<Card> takableCards = colorStack.getTakableCards();
            
                for(Card card : takableCards){

                    if(getSequenceStack(card) != null || getColorStack(card) != null){
                        return String.format("TAKE %s %s", colorStack.getID(), card.getHashCode());
                    }
                }            
            }

            System.err.println("No TakeAction found\n");
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