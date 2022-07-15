package com.codingame.game;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.game.Config.Config;
import com.codingame.game.action.Action;
import com.codingame.game.card.*;

public class Player extends AbstractMultiplayerPlayer {

    private Map<String, Card> cardsInHand;
    private int remainingActions;
    private Action action;
    private boolean mustDraw;
    //private List<String> possibleMoves; //TODO : caculer les actions possibles

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

    public void reset(){
        //this.possibleMoves = new ArrayList<String>();
        this.mustDraw = true;
        this.remainingActions = Config.ACTIONS_PER_PLAYER;
    }

    public void drawCard(Game game){
        addCardInHand(game.drawCard());
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
        
        if(action == Action.WAIT){
            this.remainingActions = 0;
        }else if(action != Action.PUSH && action != Action.ADD){
            this.removeOnePlay();
        }else{
            this.mustDraw = false;
        }
    }

    public boolean canPlay(Game game){
        return this.remainingActions > 0 || this.canAdd(game) || this.canPush();
    }

    public boolean canAdd(Game game){
        // can the player add one of his cards in one or several stacks ?

        

        return true;
    }

    public boolean canPush(){
        //TODO: implémenter la fonction
        return true;
    }

    public Action getAction(){
        return action;
    }

    public int actionsLeft(){
        return remainingActions;
    }

    public Map<String, Card> getCards(){
        return cardsInHand;
    }

}
