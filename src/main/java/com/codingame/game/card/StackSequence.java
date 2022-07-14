package com.codingame.game.card;

public class StackSequence extends CardStack{

    final int MAX_STACK_LENGHT = 14;

    private CardColors stackColor;
    private int sequenceStart;
    private int sequenceEnd;

    public StackSequence(CardColors color, int start, int end){
        this.stackColor = color;
        this.sequenceStart = start;
        this.sequenceEnd = end;
    }    
    
    public boolean add(int cardNumber){

        if(cardNumber == sequenceEnd + 1){
            sequenceEnd = cardNumber;
            return true;
        }else if(cardNumber == sequenceStart - 1){
            sequenceStart = cardNumber;
            return true;
        }else{
            return false;
        }
    }

    public StackSequence[] split(int card1, int card2){

        if(card1 - sequenceStart >= 3 && sequenceEnd - card2 >= 3){

            StackSequence sequence_1 = new StackSequence(stackColor, sequenceStart, card1);
            StackSequence sequence_2 = new StackSequence(stackColor, card2, sequenceEnd);

            return new StackSequence[]{sequence_1, sequence_2};

        }else{
            return null;
        }
    }

    public Card getCard(int index){

        if(index >= 0 && index + sequenceStart <= sequenceEnd){
            return new Card(stackColor, index + sequenceStart);
        }else{
            return null;
        }
    }

    public int cardsCount(){
        return sequenceEnd - sequenceStart;
    }
}
