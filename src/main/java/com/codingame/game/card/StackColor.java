package com.codingame.game.card;

public class StackColor extends CardStack{

    final int MAX_STACK_LENGHT = 4;

    int stackNumber;
    boolean[] colors;

    public StackColor(int number){
        this.stackNumber = number;
        this.colors = new boolean[MAX_STACK_LENGHT];
    }

    public boolean add(Card card){

        int colorRank = card.color.ordinal();

        if((card.number == stackNumber || card.number == 13) && colors[colorRank] == false){
            //this.cards.add(card);
            this.colors[colorRank] = true;
            return true;
        }else{
            return false;
        }
    }
}
