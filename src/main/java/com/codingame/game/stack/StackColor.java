package com.codingame.game.stack;

import com.codingame.game.Config.Config;
import com.codingame.game.card.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
        
        this.cards = new TreeMap<String, Card>();
        this.ID = stackID;
        this.type = StackType.COLOR;
        this.stackNumber = cards.get(0).getNumber();
        this.colors = new boolean[MAX_STACK_LENGHT];

        for(Card card : cards){
            //this.addIfBonus(card);
            this.cards.put(card.getHashCode(), card);
            this.colors[card.getColor().ordinal()] = true;
        }
    }

    public boolean addCard(Card card){

        int colorRank = card.getColor().ordinal();

        if(canAdd(card)){
            //this.addIfBonus(card);
            this.cards.put(card.getHashCode(), card);
            this.colors[colorRank] = true;
            return true;
        }else{
            //throw new Exception(String.format("StackColor.addCard : the card %s cannot be added to the stack", card.getHashCode()));
            assert false : String.format("StackColor.addCard : the card %s cannot be added to the stack", card.getHashCode());
            return false;
        }
    }

    public List<Card> getTakableCards(){

        List<Card> takableCards = new ArrayList<Card>();

        if(this.cardsCount() > 3){
            takableCards = new ArrayList<Card>(this.cards.values());
        }

        return takableCards;
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

    public boolean isValid(){

        List<Card> cards = new ArrayList<Card>(this.cards.values());
        List<CardColors> colors = new ArrayList<CardColors>();
        
        for (int i = 0; i < cards.size() - 1; i++){
            
            boolean checkNumber = cards.get(i + 1).getNumber() == (cards.get(i).getNumber());
            boolean checkColor = !colors.contains(cards.get(i).getColor());

            if(!checkNumber || !checkColor) return false;
        }

        return cards.size() >= Config.MIN_CARDS_TO_SPLIT;
    }
}
