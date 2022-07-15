package com.codingame.game.stack;

import com.codingame.game.card.*;
import java.util.HashMap;
import java.util.List;

public class StackColor extends CardStack{

    final int MAX_STACK_LENGHT = 4;

    int stackNumber;    
    
    boolean[] colors;

    /* public StackColor(int stackID, int number){
        
        this.cards = new HashMap<String, Card>();
        this.ID = stackID;
        this.type = StackType.COLOR;
        this.stackNumber = number;
        this.colors = new boolean[MAX_STACK_LENGHT];
    } */

    public StackColor(int stackID, List<Card> cards){
        
        this.cards = new HashMap<String, Card>();
        this.ID = stackID;
        this.type = StackType.COLOR;
        this.stackNumber = cards.get(0).getNumber();
        this.colors = new boolean[MAX_STACK_LENGHT];

        for(Card card : cards){
            this.addIfBonus(card);
            this.cards.put(card.getHashCode(), card);
            this.colors[card.getColor().ordinal()] = true;
        }
    }

    public boolean addCard(Card card) throws Exception{

        int colorRank = card.getColor().ordinal();

        if(canAdd(card)){
            this.addIfBonus(card);
            this.cards.put(card.getHashCode(), card);
            this.colors[colorRank] = true;
            return true;
        }else{
            throw new Exception(String.format("StackColor.addCard : the card %s cannot be added to the stack", card.getHashCode()));
        }
    }

    public void remove(Card card){
        this.removeIfBonus(card);
        this.cards.remove(card.getHashCode());
        this.colors[card.getColor().ordinal()] = false;
    }

    public boolean canAdd(Card card){

        int colorRank = card.getColor().ordinal();

        return (card.getNumber() == stackNumber || card.getNumber() == 13) && colors[colorRank] == false;
    }
}
