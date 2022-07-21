package com.codingame.game.action;

import com.codingame.game.card.Card;
import com.codingame.game.stack.StackSequence;

public class SplitAction extends Action {

    private int stackID;
    private int newStackID;
    private Card card1;
    private Card card2;
    private StackSequence stack1;
    private StackSequence stack2;

    public SplitAction(int stackID, int newStackID, Card card1, Card card2) {
        this.stackID = stackID;
        this.card1 = card1;
        this.card2 = card2;
    }

    public void setStacks(StackSequence[] stacks){
        this.stack1 = stacks[0];
        this.stack2 = stacks[1];
    }

    public StackSequence getStack1(){
        return this.stack1;
    }

    public StackSequence getStack2(){
        return this.stack2;
    }

    public int getStackID() {
        return this.stackID;
    }

    public int getNewStackID(){
        return this.newStackID;
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
