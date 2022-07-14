package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

import com.codingame.game.card.*;

public class Player extends AbstractMultiplayerPlayer {

    private List<Card> cardsInHand;

    @Override
    public int getExpectedOutputLines() {
        // Returns the number of expected lines of outputs for a player

        // TODO: Replace the returned value with a valid number. Most of the time the value is 1. 
        return 1;
    }

    public void init(Game game){

        cardsInHand = new ArrayList<Card>();

        while(cardsInHand.size() < Config.CARDS_PER_PLAYER){

            cardsInHand.add(game.takeCard());
        }
    }

    public void printCards(){
        System.err.println(this.index);
        for(Card card : cardsInHand){
            System.err.println(card.getHashCode());
        }
        System.err.println(" ");
    }

    public List<Card> getCards(){return cardsInHand;}
}
