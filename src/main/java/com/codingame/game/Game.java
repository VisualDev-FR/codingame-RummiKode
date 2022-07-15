package com.codingame.game;

import java.util.*;

import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.view.View;
import com.google.inject.Inject;
import com.codingame.game.action.Action;
import com.codingame.game.card.*;
import com.codingame.game.stack.*;


public class Game {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private View view;

    public Random random;
    public Stack<Card> drawCards;
    
    public Map<Integer, StackSequence> sequenceStacks;  // HashMap wich will contain all sequenceStacks pushed in the game with their unique id
    public Map<Integer, StackColor> colorStacks;        // HashMap wich will contain all colorStacks pushed in the game with their unique id
    public Map<Integer, StackType> stacks;              // HashMap wich will contain all stackIDs pushed in the game with their stackType

    private boolean lastTurn;

    public void init(Random secureRandom){

        stacks = new HashMap<Integer, StackType>();        
        sequenceStacks = new HashMap<Integer, StackSequence>();
        colorStacks = new HashMap<Integer, StackColor>();

        random = secureRandom;

        init_drawCards();

        view.init();

        for(Player player : gameManager.getActivePlayers()) {
            player.init(this);            
        }

        view.addInDraw(gameManager.getActivePlayers().get(0).getCards());

        System.err.println("drawCount = " + drawCards.size());
    }

    public boolean isLastTurn() {
        return lastTurn;
    }    

    public Map<String, Card> getPlayerCard(int playerIndex){
        return gameManager.getActivePlayers().get(playerIndex).getCards();
    }

    public List<String> getCurrentFrameInfoFor(Player player) {
        
        List<String> lines = new ArrayList<>();

        String[] cards = (String[]) player.getCards().keySet().toArray();

        lines.add(Integer.toString(gameManager.getPlayerCount())); // playersCount
        lines.add(String.join(" ", cards)); // cardsInHand
        
        for(int i = 0; i < gameManager.getPlayerCount(); i++){
            lines.add(Integer.toString(gameManager.getPlayers().get(i).getScore())); // playerScores
        }
        
        lines.add(Integer.toString(stacks.size())); // stacksCount
        
        for(int stackID : stacks.keySet()){

            String stackData = "";

            if(stacks.get(stackID) == StackType.SEQUENCE){
                stackData = sequenceStacks.get(stackID).getInputs();
            }else{
                stackData = colorStacks.get(stackID).getInputs();
            }

            lines.add(String.format("%s %s", stackID, stackData)); // stacks data
        }

        return lines;
    }
    
    public void init_drawCards(){

        drawCards = new Stack<Card>();

        Stack<Card> drawTemp = new Stack<Card>();
        
        for(CardColors color : CardColors.values()){

            for(int i = 0; i < 13; i++){
        
                drawTemp.add(new Card(color, i));
                drawTemp.add(new Card(color, i));
            }
        }

        List<CardColors> bonusColors = Arrays.asList(CardColors.values());

        int indexBonus_1 = random.nextInt(bonusColors.size() - 1);
        int indexBonus_2 = indexBonus_1;

        while(indexBonus_1 == indexBonus_2){
            indexBonus_2 = random.nextInt(bonusColors.size());
        }

        drawTemp.add(new Card(bonusColors.get(indexBonus_1), 13));
        drawTemp.add(new Card(bonusColors.get(indexBonus_2), 13));

        while(drawTemp.size() > 0){

            int cardIndex = random.nextInt(drawTemp.size());

            drawCards.push(drawTemp.get(cardIndex));
            drawTemp.remove(cardIndex);
        }


    }

    public Card drawCard(){

        Card card = drawCards.get(random.nextInt(drawCards.size()));

        drawCards.remove(card);

        return card; 
    }

    public List<String> getGlobalInfoFor(Player player) {
        List<String> lines = new ArrayList<>();

        return lines;
    }    

    public void PRINT_DRAW_CARDS(){
        for(Card card : drawCards){
            System.err.println(card.getHashCode());
        }
    }

    // PLAYING FUNCTIONS

    public void WAIT(Player player){
        player.setAction(Action.WAIT);
    }

    public void TAKE(Player player, int stackID, Card cardToTake) throws Exception{

        player.setAction(Action.TAKE);

        removeCardFromStack(stackID, cardToTake);
        
        player.addCardInHand(cardToTake);
        player.removeOnePlay();
    }

    public void ADD(Player player, int stackID, Card cardToAdd) throws Exception{
        
        player.setAction(Action.TAKE);
        player.removeCardInHand(cardToAdd);

        StackType type = stacks.get(stackID);        

        if(type == StackType.SEQUENCE){
            sequenceStacks.get(stackID).addCard(cardToAdd);
        }else{
            colorStacks.get(stackID).addCard(cardToAdd);
        }
    }

    public void PUSH(Player player, List<Card> cards, StackType type){

        player.setAction(Action.PUSH);
        player.removeCardInHand(cards);

        int stackID = getFreeStackID();
        this.stacks.put(stackID, type);

        if(type == StackType.SEQUENCE){

            StackSequence stack = new StackSequence(stackID, cards);
            this.sequenceStacks.put(stackID, stack);            
            
        }else{

            StackColor stack = new StackColor(stackID, cards);
            this.colorStacks.put(stackID, stack);
        }
    }

    public void SPLIT(Player player, int stackID, Card card1, Card card2){

        //TODO: implémenter la fonction
        player.setAction(Action.SPLIT);
        
    }

    public void JOIN(Player player, int stackID_1, int stackID_2){

        player.setAction(Action.JOIN);
        
        StackSequence stack_1 = sequenceStacks.get(stackID_1);
        StackSequence stack_2 = sequenceStacks.get(stackID_2);

        stacks.remove(stackID_1);
        stacks.remove(stackID_2);

        sequenceStacks.remove(stackID_1);
        sequenceStacks.remove(stackID_2);

        StackSequence mergedStack = stack_1.merge(stack_2);

        stacks.put(mergedStack.getID(), StackType.SEQUENCE);
        sequenceStacks.put(mergedStack.getID(), mergedStack);
    }

    public void MOVE(Player player, int stackID_From, int stackID_To, Card card) throws Exception{

        player.setAction(Action.MOVE);

        removeCardFromStack(stackID_From, card);
        addCardInStack(stackID_To, card);        
    }

    // STACKS HANDLING

    public void removeCardFromStack(int stackID, Card card) throws Exception{

        if(stacks.get(stackID) == StackType.SEQUENCE){

            StackSequence cardStack = sequenceStacks.get(stackID);

            List<StackSequence> sequences = cardStack.remove(getFreeStackID(), card);

            removeStack(cardStack);
    
            for(StackSequence sequence : sequences){
                addStack(sequence);
            }         

        }else{

            colorStacks.get(stackID).remove(card);
        }

    }

    public void addCardInStack(int stackID, Card card) throws Exception{

        if(stacks.get(stackID) == StackType.SEQUENCE){

            sequenceStacks.get(stackID).addCard(card);

        }else{

            colorStacks.get(stackID).addCard(card);
        }          
    }

    public void removeStack(StackSequence stack){
        this.stacks.remove(stack.getID());
        this.sequenceStacks.remove(stack.getID());
    }

    public void removeStack(StackColor stack){
        this.stacks.remove(stack.getID());
        this.colorStacks.remove(stack.getID());
    }    

    public void addStack(StackSequence stack){

        this.stacks.put(stack.getID(), stack.getType());
        this.sequenceStacks.put(stack.getID(), stack);
    }

    public void addStack(StackColor stack){

        this.stacks.put(stack.getID(), stack.getType());
        this.colorStacks.put(stack.getID(), stack);
    }  

    public int getFreeStackID(){
        
        int freeStackID = this.stacks.size();

        for(int i = 0; i < stacks.size(); i++){
            if(!stacks.containsKey(i)) return i;
        }

        return freeStackID;
    }
}
