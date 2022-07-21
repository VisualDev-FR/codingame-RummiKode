package com.codingame.game.action;

import com.codingame.game.card.Card;

public class TakeAction extends Action{

    private int stackID;
    private int newStackID;
    private Card cardToTake;
    private boolean doesMakeNewStack;

    public TakeAction(int stackID, Card cardToTake){
        this.stackID = stackID;
        this.cardToTake = cardToTake;
        this.doesMakeNewStack = false;
        this.newStackID = -1;
    }

    public boolean doesMakeNewStack(){
        return this.doesMakeNewStack;
    }

    public void setNewStackID(int newStackID){
        this.newStackID = newStackID;
        this.doesMakeNewStack = true;
    }

    public int getNewStackID(){
        return this.newStackID;
    }

    public int getStackID() {
        return this.stackID;
    }

    public Card getCardToTake() {
        return this.cardToTake;
    }

    @Override
    public boolean isTake(){
        return true;
    }
}
