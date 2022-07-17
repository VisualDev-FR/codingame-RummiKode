package com.codingame.game.action;

import com.codingame.game.card.Card;

public class TakeAction extends Action{

    private int stackID;
    private Card cardToTake;

    public TakeAction(int stackID, Card cardToTake){
        this.stackID = stackID;
        this.cardToTake = cardToTake;
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
