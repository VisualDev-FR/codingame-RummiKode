package com.codingame.game.action;

import com.codingame.game.card.Card;

public class TakeAction extends Action{

    private int stackID;
    private Card cardToTake;
    private boolean doesMakeNewStack;
    private SplitAction splitAction;

    public TakeAction(int stackID, Card cardToTake){
        this.stackID = stackID;
        this.cardToTake = cardToTake;
        this.doesMakeNewStack = false;
    }

    public boolean doesMakeNewStack(){
        return this.doesMakeNewStack;
    }

    public void setSplitAction(SplitAction splitAction){
        this.splitAction = splitAction;
        this.doesMakeNewStack = true;
    }

    public SplitAction getSplitAction(){
        return this.splitAction;
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
