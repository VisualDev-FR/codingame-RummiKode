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

    public StackSequence mergeWith(StackSequence sequence){

        TreeMap<String, Card> newCards = new TreeMap<String, Card>();

        newCards.putAll(this.cards);
        newCards.putAll(sequence.getCards());

        return new StackSequence(this.ID, new ArrayList<Card>(newCards.values()));
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
        }else{
            card1 = card_2;
            card2 = card_1;
        }

        if(card1 - sequenceStart >= 3 && sequenceEnd - card2 >= 3){

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

        }else{
            assert false : "";
            return null;
        }
    }

    public List<StackSequence> remove(int newID, Card cardToRemove){

        List<StackSequence> newSequences = new ArrayList<StackSequence>();

        if(cardToRemove.getNumber() > sequenceStart && cardToRemove.getNumber() < sequenceEnd){

            // is the card to remove, contained into the cardSequence ?

            List<Card> cards_1 = new ArrayList<Card>();
            List<Card> cards_2 = new ArrayList<Card>();

            for(Card card : this.cards.values()){
                if(card.getNumber() < cardToRemove.getNumber()){
                    cards_1.add(card);
                }else if(card.getNumber() > cardToRemove.getNumber()){
                    cards_2.add(card);
                }
            }            

            newSequences.add(new StackSequence(this.ID, cards_1));
            newSequences.add(new StackSequence(newID, cards_2));

        }else if(cardToRemove.getNumber() == sequenceStart){

            // is the card to remove at the start of the sequence ?
            
            List<Card> newCards = new ArrayList<Card>();

            for(Card card : this.cards.values()){
                if(card.getNumber() > sequenceStart){
                    newCards.add(card);
                }
            }

            newSequences.add(new StackSequence(this.ID, newCards));

        }else if(cardToRemove.getNumber() == sequenceEnd){

            // is the card to remove at the end of the sequence ?

            List<Card> newCards = new ArrayList<Card>();

            for(Card card : this.cards.values()){
                if(card.getNumber() < sequenceEnd){
                    newCards.add(card);
                }
            }

            newSequences.add(new StackSequence(this.ID, newCards));

        }else{
            //throw new Exception(String.format("StackSequence.remove : the card %s is not contained in the sequence", cardToRemove.getHashCode()));
            assert false : String.format("StackSequence.remove : the card %s is not contained in the sequence", cardToRemove.getHashCode());
        }
        

        return newSequences;            
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
