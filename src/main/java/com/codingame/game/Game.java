package com.codingame.game;

import java.util.*;

import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.view.View;
import com.google.inject.Inject;
import com.codingame.game.action.AddAction;
import com.codingame.game.action.JoinAction;
import com.codingame.game.action.MoveAction;
import com.codingame.game.action.PushAction;
import com.codingame.game.action.SplitAction;
import com.codingame.game.action.TakeAction;
import com.codingame.game.card.*;
import com.codingame.game.stack.*;


public class Game {

    @Inject private GameSummaryManager gameSummaryManager;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private View view;

    private Random random;
    private Stack<Card> drawCards;
    
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

        for(Player player : gameManager.getActivePlayers()) {
            player.init(this);            
            System.err.printf("game.init after player %s draw : DrawSize = %s\n", player.getNicknameToken(), this.getDrawCards().size()); 
        }

        view.init(this);
    }

    public List<Player> getPlayers(){
        return gameManager.getActivePlayers();
    }

    public int playersCount(){
        return gameManager.getPlayerCount();
    }

    public boolean isLastTurn() {
        return lastTurn;
    }    

    public List<String> getCurrentFrameInfoFor(Player player) {
        
        List<String> lines = new ArrayList<>();
        
        lines.add(Integer.toString(player.cardsCount())); // total number of cards in hand
        lines.add(player.getInfos()); // cardsInHand
        lines.add(Integer.toString(gameManager.getPlayerCount())); // playersCount
        
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

            lines.add(stackData); // stacks data
        }

        return lines;
    }
    
    public void init_drawCards(){

        this.drawCards = new Stack<Card>();

        // we had two cards of each in common draw
        
        for(CardColors color : CardColors.values()){

            for(int i = 0; i < 13; i++){
        
                this.drawCards.add(new Card(color, i));
                this.drawCards.add(new Card(color, i));
            }
        }

        /* if(!Config.ENABLE_BONUS) return;

        // we search two random colors to add them in the common draw

        CardColors[] bonusColors = CardColors.values();

        int indexBonus_1 = random.nextInt(bonusColors.length - 1);
        int indexBonus_2 = indexBonus_1;

        while(indexBonus_1 == indexBonus_2){
            indexBonus_2 = random.nextInt(bonusColors.length);
        }

        // we had two random-color bonus to the common draw

        drawCards.add(new Card(bonusColors[indexBonus_1]));
        drawCards.add(new Card(bonusColors[indexBonus_2])); 
        */
    }

    public Card drawOneCard(){

        Card card = drawCards.get(random.nextInt(drawCards.size()));
        drawCards.remove(card);

        return card; 
    }

    public Stack<Card> getDrawCards(){
        return this.drawCards;
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

    public boolean isGameOver() {
        
        List<Player> activePlayers = gameManager.getActivePlayers();
        
        if (activePlayers.size() <= 1) {
            return true;
        }else{
            for(Player player : activePlayers){
                if(player.cardsCount() <= 0) return true;
            }
        }
        return false;
    }    

    public void resetGameTurnData() {
        gameSummaryManager.clear();
        for (Player player : gameManager.getActivePlayers()) {
            player.setMessage(null);
            //view.setPlayerMessage(player); // TODO: Implement this Function
        }
    }

    public void performGameOver() {
        List<Player> activePlayers = gameManager.getActivePlayers();
        if (activePlayers.size() <= 1) {
            if (activePlayers.size() == 1) {
                gameManager.addToGameSummary(String.format("Only %s is still playing!",activePlayers.get(0).getNicknameToken()));
            } else {
                gameManager.addToGameSummary("No player remaining!");
            }
            return;
        }
    }

    // PLAYING FUNCTIONS

    public void performGameUpdate(Player player){

        //view.startOfTurn();
        //view.setPlayerMessage(player);        
                 
        if (player.getAction().isMove()) {
            MOVE(player, (MoveAction) player.getAction());
        }
        else if (player.getAction().isTake()) {
            TAKE(player, (TakeAction) player.getAction());
        }
        else if (player.getAction().isAdd()) {
            ADD(player, (AddAction) player.getAction());
        }
        else if (player.getAction().isPush()) {
            PUSH(player, (PushAction) player.getAction());
        }
        else if (player.getAction().isSplit()) {
            SPLIT(player, (SplitAction) player.getAction());
        }
        else if (player.getAction().isJoin()) {
            JOIN(player, (JoinAction) player.getAction());
        }        
        else if (player.getAction().isWait()) {
            gameSummaryManager.wait(player);
            player.setLeftActions(0);
        }

        player.setScore(player.cardsCount());

        // update view
        //view.endOfTurn(this);
    }    

    public void TAKE(Player player, TakeAction takeAction){

        int stackID = takeAction.getStackID();
        Card cardToTake = takeAction.getCardToTake();

        removeCardFromStack(stackID, cardToTake);
        
        player.addCardInHand(cardToTake);
        player.removeOnePlay();
    }

    public void ADD(Player player, AddAction addAction){

        int stackID = addAction.getStackID();
        Card cardToAdd = addAction.getCardToAdd();
        
        player.removeCardInHand(cardToAdd);

        StackType type = stacks.get(stackID);        

        if(type == StackType.SEQUENCE){
            sequenceStacks.get(stackID).addCard(cardToAdd);
            view.addCard(player, stackID, addAction);
        }
        else if(type == StackType.COLOR){
            colorStacks.get(stackID).addCard(cardToAdd);
            view.addCard(player, stackID, addAction);
        }
        else{
            assert false : "stackType unknown : " + type;
        }

        player.disableDraw();
    }

    public void PUSH(Player player, PushAction pushAction){

        List<Card> cards = pushAction.getCards();
        StackType type = pushAction.getType();

        player.removeCardsInHand(cards);

        int stackID = getFreeStackID();

        if(type == StackType.SEQUENCE){
            StackSequence stack = new StackSequence(stackID, cards);
            this.sequenceStacks.put(stackID, stack);
            this.stacks.put(stackID, type);
            view.pushStack(player, stackID, pushAction);
        }
        else if(type == StackType.COLOR){
            StackColor stack = new StackColor(stackID, cards);
            this.colorStacks.put(stackID, stack);
            this.stacks.put(stackID, type);
            view.pushStack(player, stackID, pushAction);
        }
        else{
            assert false : "stackType unknown : " + type;
        }

        player.pushFirstStack();
        player.disableDraw();
    }

    public void SPLIT(Player player, SplitAction splitAction){

        int stackID = splitAction.getStackID();
        Card card1 = splitAction.getCard1();
        Card card2 = splitAction.getCard2();

        this.sequenceStacks.get(stackID).split(getFreeStackID(), card1.getNumber(), card2.getNumber());
    }

    public void JOIN(Player player, JoinAction joinAction){

        int stackID_1 = joinAction.getStackID_1();
        int stackID_2 = joinAction.getStackID_2();
        
        StackSequence stack_1 = sequenceStacks.get(stackID_1);
        StackSequence stack_2 = sequenceStacks.get(stackID_2);

        stacks.remove(stackID_1);
        stacks.remove(stackID_2);

        sequenceStacks.remove(stackID_1);
        sequenceStacks.remove(stackID_2);

        StackSequence mergedStack = stack_1.mergeWith(stack_2);

        stacks.put(mergedStack.getID(), StackType.SEQUENCE);
        sequenceStacks.put(mergedStack.getID(), mergedStack);
    }

    public void MOVE(Player player, MoveAction moveAction){

        removeCardFromStack(moveAction.getStackID_From(), moveAction.getCardToMove());
        addCardInStack(moveAction.getStackID_To(), moveAction.getCardToMove());        
    }

    // STACKS HANDLING

    public void removeCardFromStack(int stackID, Card card){

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

    public void addCardInStack(int stackID, Card card){

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
