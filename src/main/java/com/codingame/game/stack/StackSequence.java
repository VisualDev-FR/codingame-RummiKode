package com.codingame.game.stack;

import com.codingame.game.Config.Config;
import com.codingame.game.card.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class StackSequence extends CardStack{

    private CardColors stackColor;
    private int sequenceStart;
    private int sequenceEnd;

    public StackSequence(int stackID, List<Card> cards){
        
        this.cards = new TreeMap<String, Card>();
        this.ID = stackID;
        this.type = StackType.SEQUENCE;
        this.stackColor = cards.get(0).getColor();
        this.sequenceStart = 99;
        this.sequenceEnd = -1;

        for(Card card : cards){

            //this.addIfBonus(card);
            this.cards.put(card.getHashCode(), card);
            this.sequenceStart = Math.min(this.sequenceStart, card.getNumber());
            this.sequenceEnd = Math.max(this.sequenceEnd, card.getNumber());
        }
    }

    public List<Card> getTakableCards(){

        List<Card> takableCards = new ArrayList<Card>();

        if(this.cardsCount() > 3){
            takableCards.add(new ArrayList<Card>(this.cards.values()).get(0));
            for(int i = 3; i < this.cardsCount() - 3; i++){
                takableCards.add(new ArrayList<Card>(this.cards.values()).get(i));
            }            
            takableCards.add(new ArrayList<Card>(this.cards.values()).get(this.cardsCount() - 1));
        }

        return takableCards;
    }

    public void mergeWith(StackSequence sequence){
        this.cards.putAll(sequence.getCards());
    }
    
    public void addCard(Card card){

        this.sequenceStart = Math.min(this.sequenceStart, card.getNumber());
        this.sequenceEnd = Math.max(this.sequenceEnd, card.getNumber());
        this.cards.put(card.getHashCode(), card);
    }

    public StackSequence[] split(int newID, int card_1, int card_2){

        int card1 = -1;
        int card2 = -1;

        if(card_1 < card_2){
            card1 = card_1;
            card2 = card_2;
        }else if(card_1 > card_2){
            card1 = card_2;
            card2 = card_1;
        }else{
            assert false : "cant split, the two cards index are the same";
        }

        List<Card> cards_1 = new ArrayList<Card>();
        List<Card> cards_2 = new ArrayList<Card>();

        for(Card card : this.cards.values()){
            if(card.getNumber() <= card1){
                cards_1.add(card);
            }else if(card.getNumber() >= card2){
                cards_2.add(card);
            }
        }            

        StackSequence sequence_1 = new StackSequence(this.ID, cards_1);
        StackSequence sequence_2 = new StackSequence(newID, cards_2);

        return new StackSequence[]{sequence_1, sequence_2};

    }

    public boolean canSplit(int card_1, int card_2){

        int card1 = -1;
        int card2 = -1;

        if(card_1 < card_2){
            card1 = card_1;
            card2 = card_2;
        }else if(card_1 > card_2){
            card1 = card_2;
            card2 = card_1;
        }else{
            return false;
        }

        return card1 - sequenceStart >= Config.MIN_CARDS_TO_SPLIT && sequenceEnd - card2 >= Config.MIN_CARDS_TO_SPLIT && Math.abs(card_1 - card_2) == 1;
    }

    public void remove(Card cardToRemove){
        this.cards.remove(cardToRemove.getHashCode());        
    }

    public boolean canAdd(Card card){
        return card.getColor() == this.stackColor && (card.getNumber() == this.sequenceStart - 1 || card.getNumber() == this.sequenceEnd + 1 || card.isBonus());
    }

    public boolean canRemove(Card card){
        
        boolean canRemove = false;
        int minCards = Config.MIN_CARDS_TO_SPLIT;
        
        if(card.getNumber() > this.sequenceStart && card.getNumber() < this.sequenceEnd){

            // if the cardNumber is between two sequence bounds, the card can only be removed if the two new stacks contain at least <Config.MIN_CARDS_TO_SPLIT> cards
            
            canRemove = card.getNumber() - this.sequenceStart > minCards && this.sequenceEnd - card.getNumber() > minCards; 
        
        }else if(card.getNumber() == this.sequenceStart || card.getNumber() == this.sequenceEnd){

            // if the cardNumber equals one of the two bounds of the stack, the card can only be removed if the cardsCount is greater than <Config.MIN_CARDS_TO_SPLIT>  

            canRemove = this.cardsCount() > minCards;

        }

        // else, it means that the card is not into the stack, so we return false

        return canRemove;
    }

    public int getFirstNumber(){
        return sequenceStart;
    }

    public int getLastNumber(){
        return sequenceEnd;
    }
}   
