package com.codingame.game.stack;

import com.codingame.game.card.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StackSequence extends CardStack{

    private CardColors stackColor;
    private int sequenceStart;
    private int sequenceEnd;


    public StackSequence(int stackID, CardColors color, int start, int end){
        
        this.cards = new HashMap<String, Card>();
        this.ID = stackID;
        this.type = StackType.SEQUENCE;
        this.stackColor = color;
        this.sequenceStart = start;
        this.sequenceEnd = end;

        for(int cardID = start; cardID <= end; cardID++){
            Card newCard = new Card(color, cardID);
            this.addIfBonus(newCard);
            this.cards.put(newCard.getHashCode(), newCard);
        }
    }

    public StackSequence(int stackID, List<Card> cards){
        
        this.cards = new HashMap<String, Card>();
        this.ID = stackID;
        this.type = StackType.SEQUENCE;
        this.stackColor = cards.get(0).getColor();
        this.sequenceStart = 15;
        this.sequenceEnd = 0;

        for(Card card : cards){

            this.addIfBonus(card);
            this.cards.put(card.getHashCode(), card);
            this.sequenceStart = Math.min(this.sequenceStart, card.getNumber());
            this.sequenceEnd = Math.max(this.sequenceEnd, card.getNumber());
        }
    }

    public StackSequence merge(StackSequence sequence){

        int start = Math.min(this.sequenceStart, sequence.sequenceStart);
        int end = Math.max(this.sequenceEnd, sequence.sequenceEnd);

        return new StackSequence(this.ID, this.stackColor, start, end);
    }
    
    public boolean addCard(Card card) throws Exception{

        if(card.isBonus()){
            // TODO: compute what we are supposed to do in this case
            return true;

        }else if(card.getNumber() == sequenceEnd + 1){
            this.addIfBonus(card);
            this.sequenceEnd = card.getNumber();
            this.cards.put(card.getHashCode(), card);
            return true;

        }else if(card.getNumber() == sequenceStart - 1){
            this.addIfBonus(card);
            this.sequenceStart = card.getNumber();
            this.cards.put(card.getHashCode(), card);
            return true;

        }else{
            throw new Exception(String.format("StackSequence.addCard : the card %s cannot be added to the stack", card.getHashCode()));
        }
    }

    public StackSequence[] split(int newID, int card1, int card2){

        if(card1 - sequenceStart >= 3 && sequenceEnd - card2 >= 3){

            StackSequence sequence_1 = new StackSequence(this.ID, stackColor, sequenceStart, card1);
            StackSequence sequence_2 = new StackSequence(newID, stackColor, card2, sequenceEnd);

            return new StackSequence[]{sequence_1, sequence_2};

        }else{
            return null;
        }
    }

    public List<StackSequence> remove(int newID, Card card) throws Exception{

        List<StackSequence> newSequences = new ArrayList<StackSequence>();

        if(card.getNumber() > sequenceStart && card.getNumber() < sequenceEnd){

            // is the card to remove, contained into the cardSequence ?

            newSequences.add(new StackSequence(this.ID, this.stackColor, sequenceStart, card.getNumber() - 1));
            newSequences.add(new StackSequence(newID, this.stackColor, card.getNumber() + 1, sequenceEnd));
            this.removeIfBonus(card);
        
        }else if(card.getNumber() == sequenceStart){

            // is the card to remove at the start of the sequence ?

            newSequences.add(new StackSequence(this.ID, this.stackColor, card.getNumber() + 1, sequenceEnd));
            this.removeIfBonus(card);
        
        }else if(card.getNumber() == sequenceEnd){

            // is the card to remove at the end of the sequence ?

            newSequences.add(new StackSequence(this.ID, this.stackColor, sequenceStart, card.getNumber() - 1));
            this.removeIfBonus(card);
        
        }else{
            throw new Exception(String.format("StackSequence.remove : the card %s is not contained in the sequence", card.getHashCode()));
        }
        

        return newSequences;            
    }
    
    public Card getCard(int index){

        if(index >= 0 && index + sequenceStart <= sequenceEnd){
            return new Card(stackColor, index + sequenceStart);
        }else{
            return null;
        }
    }

    public boolean canAdd(Card card){
        return card.getColor() == this.stackColor && (card.getNumber() == this.sequenceStart - 1 || card.getNumber() == this.sequenceEnd + 1 || card.isBonus());
    }

    public int getFirstNumber(){
        return sequenceStart;
    }

    public int getLastNumber(){
        return sequenceEnd;
    }
}   
