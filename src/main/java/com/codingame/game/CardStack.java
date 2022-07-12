package com.codingame.game;

import java.util.*;

public class CardStack{

    public Map<String, Card> cards;

    public CardStack(){

        cards = new HashMap<String, Card>();
    }

    public int cardsSum(){

        int sum = 0;

        for(Card card : this.cards.values()){
            sum += card.number;
        }

        return sum;
    }

    public int cardsCount(){
        return cards.size();
    }
}
