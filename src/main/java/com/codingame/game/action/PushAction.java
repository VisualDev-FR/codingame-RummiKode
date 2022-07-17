package com.codingame.game.action;

import java.util.List;

import com.codingame.game.card.Card;
import com.codingame.game.stack.StackType;
import com.google.inject.Inject;
import com.codingame.game.InputChecker;

public class PushAction extends Action{

    @Inject InputChecker checker;

    private List<Card> cards;
    private StackType type;

    @Override
    public boolean isPush(){
        return true;
    }

    public PushAction(List<Card> cards, StackType type) {
        this.cards = cards;
        this.type = type;
    }

    public List<Card> getCards(){
        return this.cards;
    }

    public StackType getType(){
        return this.type;
    }
}
