package com.codingame.game.action;

import java.util.List;

import com.codingame.game.card.Card;
import com.codingame.game.stack.StackType;

public class PushAction extends Action{

    private List<Card> cards;
    private StackType type;
    private int stackID;

    @Override
    public boolean isPush(){
        return true;
    }

    public PushAction(List<Card> cards, StackType type, int stackID) {
        this.cards = cards;
        this.type = type;
        this.stackID = stackID;
    }

    public int getStackID(){
        return this.stackID;
    }

    public List<Card> getCards(){
        return this.cards;
    }

    public StackType getType(){
        return this.type;
    }
}
