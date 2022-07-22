package com.codingame.game.action;

import com.codingame.game.card.Card;

public class MoveAction extends Action {

    private int stackID_From;
    private int stackID_To;
    private Card cardToMove;
    private boolean doesMakeNewStack;
    private SplitAction splitAction;

    public MoveAction(int stackFrom, int stackTo, Card card){
        this.stackID_From = stackFrom;
        this.stackID_To = stackTo;
        this.cardToMove = card;
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

    public int getStackID_From() {
        return this.stackID_From;
    }

    public int getStackID_To() {
        return this.stackID_To;
    }

    public Card getCardToMove() {
        return this.cardToMove;
    }
    
    @Override
    public boolean isMove(){
        return true;
    }    
}
