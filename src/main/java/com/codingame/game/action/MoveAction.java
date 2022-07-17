package com.codingame.game.action;

import com.codingame.game.card.Card;

public class MoveAction extends Action {

    private int stackID_From;
    private int stackID_To;
    private Card cardToMove;

    public MoveAction(int stackFrom, int stackTo, Card card){
        this.stackID_From = stackFrom;
        this.stackID_To = stackTo;
        this.cardToMove = card;
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
