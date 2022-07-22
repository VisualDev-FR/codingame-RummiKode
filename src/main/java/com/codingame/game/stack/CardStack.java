package com.codingame.game.stack;

import com.codingame.game.card.*;

import java.util.*;

public class CardStack{

    protected int ID;
    protected TreeMap<String, Card> cards;
    protected StackType type;
    protected int bonusValue;
    protected int bonusCardCount;

    public int cardsSum(){

        int sum = 0;

        for(Card card : this.cards.values()){
            sum += card.getNumber();
        }

        return sum;
    }

    public void remove(Card card){
        cards.remove(card.getHashCode());
    }

    public int getID(){
        return ID;
    }

    public StackType getType(){
        return this.type;
    }

    public String getInputs(){

        List<String> inputs = new ArrayList<String>();
        
        for(Card card : this.cards.values()){
            inputs.add(card.getHashCode());
        }
        
        return String.format("%s %s %s", this.ID, this.getType().ordinal(), String.join(" ", inputs.toArray(new String[0])));
    }

    protected void removeIfBonus(Card card){
        if(card.isBonus()) this.bonusCardCount--;
    }

    public boolean containsBonus(){
        return this.bonusCardCount > 0;
    }

    public int cardsCount(){
        return this.cards.size();
    }

    public Map<String, Card> getCards(){
        return this.cards;
    }

    public String getString(){

        return String.join(" ", this.cards.keySet().toArray(new String[0]));
    }

    public boolean containsCard(Card card){
        return this.cards.containsKey(card.getHashCode());
    }

    @Override
    public String toString(){
        return String.format("%s : %s (%s)", this.ID, this.cards.keySet().toString(), this.type.toString());
    }
}
