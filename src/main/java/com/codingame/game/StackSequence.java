package com.codingame.game;

public class StackSequence extends CardStack{

    final int MAX_STACK_LENGHT = 14;

    CardColors stackColor;
    boolean[] numbers;

    public StackSequence(CardColors color){
        this.stackColor = color;
        this.numbers = new boolean[MAX_STACK_LENGHT];
    }
    
    public boolean add(Card card){

        if(card.color == stackColor && numbers[card.number] == false){
            //this.cards.add(card);
            this.numbers[card.number] = true;
            return true;
        }else{
            return false;
        }
    }

    public StackSequence[] split(Card card1, Card card2){

        if(canSplit(card1, card2)){

            StackSequence[] splittedSequences = new StackSequence[2];

            return splittedSequences;
        
        }else{
            return null;
        }
    }
    
    private boolean canSplit(Card card1, Card card2){ 

        return false;
    }
}
