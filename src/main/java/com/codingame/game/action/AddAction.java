package com.codingame.game.action;

import com.codingame.game.card.Card;

public class AddAction extends Action {

    private int stackID;
    private Card cardToAdd;

    public AddAction(int stackID, Card cardToAdd) {
        this.stackID = stackID;
        this.cardToAdd = cardToAdd;
    }

    public int getStackID() {
        return this.stackID;
    }

    public Card getCardToAdd() {
        return this.cardToAdd;
    }

    @Override
    public boolean isAdd(){
        return true;
    }
    
}
