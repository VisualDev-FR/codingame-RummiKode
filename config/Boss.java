import java.util.*;

class Player {

    public static int actionsLeft;
    public static int playerScore;
    public static boolean pushedFirstSequence;
    public static List<Team> players;
    private static Scanner in;

    public static void main(String args[]) throws Exception {

        in = new Scanner(System.in);

        List<String> playedActions = new ArrayList<String>();
        
        while (true) {

            players = new ArrayList<Team>();

            int myPlayerIndex = in.nextInt();   // the index of your player
            int playersCount = in.nextInt();    // the total number of player
            int stacksCount = in.nextInt();        // the number of stacks pushed in the game
            int drawCardsLeft = in.nextInt();
            
            for (int i = 0; i < playersCount; i++) {
                
                playerScore = in.nextInt();     // the score of the player i (your playerIndex will always be 0)
                actionsLeft = in.nextInt();     // the remaining actions of the player i
                
                players.add(new Team(i, playerScore, actionsLeft));

                String[] strCards = in.next().split(";");

                for (int j = 0; j < strCards.length; j++) {
                    players.get(i).addCard(strCards[j]);
                }
            }            

            Team myPlayer = players.get(myPlayerIndex);

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

    public enum StackType {
        SEQUENCE, COLOR
    }

    public static class Team{

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

        public Team(int index, int score, int actionsLeft){
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

    public static class Card{

        protected CardColors color;
        protected int number;
        protected boolean isBonus;
    
        public Card(CardColors color_, int number_){
            this.color = color_;
            this.number = number_;
            this.isBonus = false;
        }
    
        public Card(String strCard){
            
            // 08_BLACK | 08_blAck | 8_BLACK | 8_blAcK
    
            this.color = CardColors.valueOf(strCard.split("_")[1].toUpperCase());
            this.number = Integer.parseInt(strCard.split("_")[0]);
            this.isBonus = false;
        }
    
        public Card(CardColors cardColor){
            this.color = cardColor;
            this.number = -1;
            this.isBonus = true;
        }
    
        public void setBonusNumber(int bonusNumber) throws Exception{
    
            if(this.isBonus()){
                this.number = bonusNumber;
            }else{
                throw new Exception(String.format("Card.setBonus() : The card %s is not a bonusCard", this.getHashCode()));
            }
        }
    
        public void resetBonus() throws Exception{
    
            if(this.isBonus()){
                this.number = -1;
            }else{
                throw new Exception(String.format("Card.resetBonus() : The card %s is not a bonusCard", this.getHashCode()));
            }
        }
    
        public CardColors getColor(){
            return this.color;
        }
    
        public int getNumber(){
            return this.number;
        }
    
        public boolean isBonus(){
            return isBonus;
        }
    
        public String getImage(){
            return String.format("%s_%s.png", this.color.toString().toLowerCase(), this.isBonus() ? "none" : number);
        }
    
        public String getHashCode(){
            return String.format("%02d_%s", this.number, this.color.toString());
        }
    
        public String toString(){
            return String.format("%s_%02d", this.color.toString(), this.number);
        }
    }

    public enum CardColors{ BLACK, BLUE, GREEN, YELLOW }

    public static class StackSequence extends CardStack{

        private CardColors stackColor;
        private int sequenceStart;
        private int sequenceEnd;
    
        public StackSequence(int stackID, List<Card> cards){
            
            this.cards = new TreeMap<String, Card>();
            this.ID = stackID;
            this.type = StackType.SEQUENCE;
            this.stackColor = cards.get(0).getColor();
            this.sequenceStart = 99;
            this.sequenceEnd = -1;
    
            for(Card card : cards){
                this.cards.put(card.getHashCode(), card);
            }
    
            this.resetBounds();
        }
    
        public List<Card> getTakableCards(){
    
            List<Card> takableCards = new ArrayList<Card>();
    
            if(this.cardsCount() > 3){
                takableCards.add(new ArrayList<Card>(this.cards.values()).get(0));
                for(int i = 3; i < this.cardsCount() - 3; i++){
                    takableCards.add(new ArrayList<Card>(this.cards.values()).get(i));
                }            
                takableCards.add(new ArrayList<Card>(this.cards.values()).get(this.cardsCount() - 1));
            }
    
            return takableCards;
        }
    
        public void mergeWith(StackSequence sequence){
            this.cards.putAll(sequence.getCards());
        }
        
        public void addCard(Card card){
            this.cards.put(card.getHashCode(), card);
            this.resetBounds();
        }
    
        public StackSequence[] split(int newID, int card_1, int card_2){
    
            int card1 = -1;
            int card2 = -1;
    
            if(card_1 < card_2){
                card1 = card_1;
                card2 = card_2;
            }else if(card_1 > card_2){
                card1 = card_2;
                card2 = card_1;
            }else{
                assert false : "cant split, the two cards index are the same";
            }
    
            List<Card> cards_1 = new ArrayList<Card>();
            List<Card> cards_2 = new ArrayList<Card>();
    
            for(Card card : this.cards.values()){
                if(card.getNumber() <= card1){
                    cards_1.add(card);
                }else if(card.getNumber() >= card2){
                    cards_2.add(card);
                }
            }            
    
            StackSequence sequence_1 = new StackSequence(this.ID, cards_1);
            StackSequence sequence_2 = new StackSequence(newID, cards_2);
    
            return new StackSequence[]{sequence_1, sequence_2};
    
        }
    
        public boolean canSplit(int card_1, int card_2){
    
            int card1 = -1;
            int card2 = -1;
    
            if(card_1 < card_2){
                card1 = card_1;
                card2 = card_2;
            }else if(card_1 > card_2){
                card1 = card_2;
                card2 = card_1;
            }else{
                return false;
            }
    
            return card1 - sequenceStart >= Config.MIN_CARDS_TO_SPLIT && sequenceEnd - card2 >= Config.MIN_CARDS_TO_SPLIT && Math.abs(card_1 - card_2) == 1;
        }
    
        public void remove(Card cardToRemove){
            this.cards.remove(cardToRemove.getHashCode());
            this.resetBounds();
        }
    
        private void resetBounds(){
            this.sequenceStart = this.cards.firstEntry().getValue().getNumber();
            this.sequenceEnd = this.cards.lastEntry().getValue().getNumber();
        }
    
        public boolean canAdd(Card card){
            return card.getColor() == this.stackColor && (card.getNumber() == this.sequenceStart - 1 || card.getNumber() == this.sequenceEnd + 1 || card.isBonus());
        }
    
        public boolean canRemove(Card card){
            
            boolean canRemove = false;
            int minCards = Config.MIN_CARDS_TO_SPLIT;
            
            if(card.getNumber() > this.sequenceStart && card.getNumber() < this.sequenceEnd){
    
                // if the cardNumber is between two sequence bounds, the card can only be removed if the two new stacks contain at least <Config.MIN_CARDS_TO_SPLIT> cards
                
                canRemove = card.getNumber() - this.sequenceStart >= minCards && this.sequenceEnd - card.getNumber() >= minCards; 
            
            }else if(card.getNumber() == this.sequenceStart || card.getNumber() == this.sequenceEnd){
    
                // if the cardNumber equals one of the two bounds of the stack, the card can only be removed if the cardsCount is greater than <Config.MIN_CARDS_TO_SPLIT>  
    
                canRemove = this.cardsCount() > minCards;
    
            }
    
            // else, it means that the card is not into the stack, so we return false
    
            return canRemove;
        }
    
        public int getFirstNumber(){
            return sequenceStart;
        }
    
        public int getLastNumber(){
            return sequenceEnd;
        }
    
    }  
    
    public static class CardStack{

        protected int ID;
        protected TreeMap<String, Card> cards;
        protected StackType type;
        protected int bonusValue;
        protected int bonusCardCount;
    
        public int cardsSum(){
    
            int sum = 0;
    
            for(Card card : this.cards.values()){
                sum += card.getNumber();
            }
    
            return sum;
        }
    
        public void remove(Card card){
            cards.remove(card.getHashCode());
        }
    
        public int getID(){
            return ID;
        }
    
        public StackType getType(){
            return this.type;
        }
    
        public String getInputs(){
    
            List<String> inputs = new ArrayList<String>();
            
            for(Card card : this.cards.values()){
                inputs.add(card.getHashCode());
            }
            
            return String.format("%s %s %s", this.ID, this.getType().ordinal(), String.join(" ", inputs.toArray(new String[0])));
        }
    
        protected void removeIfBonus(Card card){
            if(card.isBonus()) this.bonusCardCount--;
        }
    
        public boolean containsBonus(){
            return this.bonusCardCount > 0;
        }
    
        public int cardsCount(){
            return this.cards.size();
        }
    
        public Map<String, Card> getCards(){
            return this.cards;
        }
    
        public String getString(){
    
            return String.join(" ", this.cards.keySet().toArray(new String[0]));
        }
    
        public boolean containsCard(Card card){
            return this.cards.containsKey(card.getHashCode());
        }
    
        @Override
        public String toString(){
            return String.format("%s : %s (%s)", this.ID, this.cards.keySet().toString(), this.type.toString());
        }
    }

    public static class StackColor extends CardStack{

        final int MAX_STACK_LENGHT = 4;
    
        int stackNumber;    
        
        boolean[] colors;
    
        public StackColor(int stackID, List<Card> cards){
            
            this.cards = new TreeMap<String, Card>();
            this.ID = stackID;
            this.type = StackType.COLOR;
            this.stackNumber = cards.get(0).getNumber();
            this.colors = new boolean[MAX_STACK_LENGHT];
    
            for(Card card : cards){
                //this.addIfBonus(card);
                this.cards.put(card.getHashCode(), card);
                this.colors[card.getColor().ordinal()] = true;
            }
        }
    
        public void addCard(Card card){
            this.cards.put(card.getHashCode(), card);
            this.colors[card.getColor().ordinal()] = true;
        }
    
        public List<Card> getTakableCards(){
    
            List<Card> takableCards = new ArrayList<Card>();
    
            if(this.cardsCount() > 3){
                takableCards = new ArrayList<Card>(this.cards.values());
            }
    
            return takableCards;
        }    
    
        public void remove(Card card){
            //this.removeIfBonus(card);
            this.cards.remove(card.getHashCode());
            this.colors[card.getColor().ordinal()] = false;
        }
    
        public boolean canRemove(){
            return this.cards.size() > Config.MIN_CARDS_TO_SPLIT;
        }
    
        public boolean canSplit(){
            return false;
        }
    
        public boolean canAdd(Card card){
    
            int colorRank = card.getColor().ordinal();
    
            return (card.getNumber() == stackNumber || card.getNumber() == 13) && colors[colorRank] == false;
        }
    
        public boolean isValid(){
    
            List<Card> cards = new ArrayList<Card>(this.cards.values());
            List<CardColors> colors = new ArrayList<CardColors>();
            
            for (int i = 0; i < cards.size() - 1; i++){
                
                boolean checkNumber = cards.get(i + 1).getNumber() == (cards.get(i).getNumber());
                boolean checkColor = !colors.contains(cards.get(i).getColor());
    
                if(!checkNumber || !checkColor) return false;
            }
    
            return cards.size() >= Config.MIN_CARDS_TO_SPLIT;
        }
    }    

    public static class Config {

        public static int CARDS_PER_PLAYER = 14;
        public static int MIN_CARDS_TO_SPLIT = 3;
        public static int MAX_BONUS_COUNT = 2;
        public static int CARDS_COUNT_TO_WIN = 0;
        public static int CARDS_COUNT_TO_LOSE = 48;
        public static int MIN_SUM_TO_START = 20;
        public static int PLAYERS_COUNT = 2;
        public static int ACTIONS_PER_PLAYER = 5;
        public static boolean ENABLE_BONUS = false;
        public static int CARDS_MAX_VALUE = 12; 
    }    
        
}