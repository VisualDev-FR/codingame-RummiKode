package com.codingame.game.action;

import com.codingame.game.card.Card;

public class SplitAction extends Action {

    private int stackID;
    private Card card1;
    private Card card2;

    public SplitAction(int stackID, Card card1, Card card2) {
        this.stackID = stackID;
        this.card1 = card1;
        this.card2 = card2;
    }

    public int getStackID() {
        return this.stackID;
    }

    public Card getCard1() {
        return this.card1;
    }

    public Card getCard2() {
        return this.card2;
    }


    @Override
    public boolean isSplit(){
        return true; 
    }
    
}
