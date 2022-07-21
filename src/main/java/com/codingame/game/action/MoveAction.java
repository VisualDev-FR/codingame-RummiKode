package com.codingame.game.action;

import com.codingame.game.card.Card;

public class MoveAction extends Action {

    private int stackID_From;
    private int stackID_To;
    private Card cardToMove;
    private boolean doesMakeNewStack;
    private int newStackID; 

    public MoveAction(int stackFrom, int stackTo, Card card){
        this.stackID_From = stackFrom;
        this.stackID_To = stackTo;
        this.cardToMove = card;
        this.doesMakeNewStack = false;
        this.newStackID = - 1;
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
