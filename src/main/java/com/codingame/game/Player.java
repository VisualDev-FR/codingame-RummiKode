package com.codingame.game;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.google.inject.Inject;
import com.codingame.game.Config.Config;
import com.codingame.game.action.Action;
import com.codingame.game.card.*;
import com.codingame.game.stack.StackColor;
import com.codingame.game.stack.StackSequence;

public class Player extends AbstractMultiplayerPlayer {

    @Inject private GameSummaryManager gameSummaryManager;

    private Stack<String> cardsInHand;
    private int remainingActions;
    private Action action;
    private boolean mustDraw;
    private boolean pushedFirstStack;
    private String message;

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public void init(Game game){

        this.action = new Action(){};
        this.cardsInHand = new Stack<String>();
        this.mustDraw = true;

        for (int i = 0; i < Config.CARDS_PER_PLAYER; i++) {
            this.drawCard(game);
        }
    }

    public Stack<String> getCardCodes(){
        return this.cardsInHand;
    }

    public void init(){ // Testing constructor        
        this.cardsInHand = new Stack<String>();
        this.mustDraw = true;
    }    

    public void update(Game game){
        this.mustDraw = true;
        this.remainingActions = Config.ACTIONS_PER_PLAYER;
    }

    public void setLeftActions(int leftActions){
        this.remainingActions = leftActions;
    }

    public String getInfos(){
        
        String[] strCards = new String[this.cardsInHand.size()];

        for (int i = 0; i < strCards.length; i++) {
            strCards[i] = cardsInHand.get(i);
        }
        
        return String.join(";", strCards);    
    }

    public int cardsCount(){
        return this.cardsInHand.size();
    }

    public Card drawCard(Game game){
        if(game.getDrawCards().size() > 0){
            Card drawedCard = game.drawOneCard();
            addCardInHand(drawedCard);
            return drawedCard;
        }else{
            gameSummaryManager.anyCardLeftInCommonDraw(this);
            return null;
        }        
    }

    public void addCardInHand(Card card){
        this.cardsInHand.add(card.getHashCode());
    }

    public void removeCardInHand(Card card){
        assert this.cardsInHand.contains(card.getHashCode());
        cardsInHand.remove(card.getHashCode());
    }

    public void removeCardsInHand(List<Card> cards){
        for(Card card : cards){
            this.removeCardInHand(card);
        }
    }

    public boolean hasToDraw(){
        return mustDraw;
    }

    public void disableDraw(){
        this.mustDraw = false;
    }

    public void removeOnePlay(){
        remainingActions--;
    }

    public void setAction(Action action){        
        this.action = action;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    public boolean canPlay(Game game){
        return !this.action.isWait() && (this.remainingActions > 0 || this.canAdd(game) || this.canPush());
    }

    public boolean canAdd(Game game){

        for(StackSequence sequence : game.sequenceStacks.values()){
            for(String strCard : this.cardsInHand){
                if(sequence.canAdd(new Card(strCard))) return true;
            }
        }

        for(StackColor colorStack : game.colorStacks.values()){
            for(String strCard : this.cardsInHand){
                if(colorStack.canAdd(new Card(strCard))) return true;
            }
        }

        return false;
    }

    public boolean canPush(){
        
        for(String strCard : this.cardsInHand){
            
            if(this.canMakeSequenceStack(new Card(strCard)) || this.canMakeColorStack(new Card(strCard))){
                return true;
            }
        }
        return false;
    }

    public boolean canMakeSequenceStack(Card card){
        
        int cardNumber = card.getNumber();
        CardColors cardColor = card.getColor();
        
        boolean check_1 = this.hasThisCard(new Card(cardColor, cardNumber + 1)) && this.hasThisCard(new Card(cardColor, cardNumber - 1));
        boolean check_2 = this.hasThisCard(new Card(cardColor, cardNumber + 1)) && this.hasThisCard(new Card(cardColor, cardNumber + 2));
        boolean check_3 = this.hasThisCard(new Card(cardColor, cardNumber - 1)) && this.hasThisCard(new Card(cardColor, cardNumber - 2));
        
        return check_1 || check_2 || check_3;
    }

    public boolean canMakeColorStack(Card card){

        int cardNumber = card.getNumber();

        List<CardColors> missingColors = new ArrayList<CardColors>();

        for(CardColors color : CardColors.values()){
            if(color != card.getColor()){
                missingColors.add(color);
                //System.err.println("Added color : " + color.toString());
            }
        }

        int missingCount = 0;

        missingCount += this.hasThisCard(new Card(missingColors.get(0), cardNumber)) ? 1 : 0;
        missingCount += this.hasThisCard(new Card(missingColors.get(1), cardNumber)) ? 1 : 0;
        missingCount += this.hasThisCard(new Card(missingColors.get(2), cardNumber)) ? 1 : 0;

        return missingCount >= 2 ;    
    }

    public  boolean hasThisCard(Card card){
        return this.cardsInHand.contains(card.getHashCode());
    }

    public Action getAction(){
        return action;
    }

    public int actionsLeft(){
        return remainingActions;
    }

    public void pushFirstStack(){
        this.pushedFirstStack = true;
    }

    public boolean hasPushedFirstStack(){
        return pushedFirstStack;
    }
}
