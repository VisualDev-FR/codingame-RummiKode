package com.codingame.game;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.google.inject.Inject;
import com.codingame.game.Config.Config;
import com.codingame.game.Summary.GameSummaryManager;
import com.codingame.game.action.Action;
import com.codingame.game.card.*;

public class Player extends AbstractMultiplayerPlayer {

    @Inject private GameSummaryManager gameSummaryManager;

    private Map<String, Card> cardsInHand;
    private int remainingActions;
    private Action action;
    private boolean mustDraw;
    private boolean pushedFirstSequence;
    //private List<String> possibleMoves;

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public void init(Game game){

        //this.possibleMoves = new ArrayList<String>();
        this.cardsInHand = new HashMap<String, Card>();
        this.mustDraw = true;

        while(cardsInHand.size() < Config.CARDS_PER_PLAYER){
            drawCard(game);
        }
    }

    public void update(Game game){
        this.mustDraw = true;
        this.remainingActions = Config.ACTIONS_PER_PLAYER;
        //this.computePossibleMoves(game);
    }

    /* private void computePossibleMoves(Game game){

        this.possibleMoves = new ArrayList<String>();        

        for(Card card : this.cardsInHand.values()){

            for(StackSequence sequenceStack : game.sequenceStacks.values()){

                List<Card> takableCards = sequenceStack.getTakableCards();

                for(Card takableCard : takableCards){

                    this.possibleMoves.add(String.format("%s %s %s", Action.TAKE.toString(), sequenceStack.getID(), takableCard.getHashCode()));
                }

                if(sequenceStack.canAdd(card)){

                    this.possibleMoves.add(String.format("%s %s %s", Action.ADD.toString(), sequenceStack.getID(), card.getHashCode()));
                }
            }
        }

        this.possibleMoves.add(Action.WAIT.toString());
        this.possibleMoves.add(Action.RANDOM.toString());

    } */

    public void setLeftActions(int leftActions){
        this.remainingActions = leftActions;
    }

    public void drawCard(Game game){
        if(game.drawCards.size() > 0){
            addCardInHand(game.drawCard());
        }else{
            gameSummaryManager.anyCardLeftInCommonDraw(this);
        }
        
        
    }

    public void addCardInHand(Card card){
        cardsInHand.put(card.getHashCode(), card);
    }

    public void removeCardInHand(Card card){
        cardsInHand.remove(card.getHashCode());
    }

    public void removeCardInHand(List<Card> cards){
        for(Card card : cards){
            this.removeCardInHand(card);
        }
    }

    public void printCards(){
        System.err.println(this.index);
        for(Card card : cardsInHand.values()){
            System.err.println(card.getHashCode());
        }
        System.err.println(" ");
    }

    public boolean hasToDraw(){
        return mustDraw;
    }

    public void removeOnePlay(){
        remainingActions--;
    }

    public void setAction(Action action){        
        this.action = action;
    }

    public boolean canPlay(Game game){
        //TODO: implement this function
        return this.remainingActions > 0; // || this.canAdd(game) || this.canPush();
    }

    public Action getAction(){
        return action;
    }

    public int actionsLeft(){
        return remainingActions;
    }

    public boolean hasPushedFirstSequence(){
        return pushedFirstSequence;
    }

    public Map<String, Card> getCards(){
        return cardsInHand;
    }

}
