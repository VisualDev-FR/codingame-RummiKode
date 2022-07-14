package com.codingame.game;

import java.util.*;

import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.view.View;
import com.google.inject.Inject;
import com.codingame.game.card.*;


public class Game {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private View view;

    public Random random;
    public Stack<Card> drawCards;

    public void init(Random secureRandom){

        random = secureRandom;

        init_drawCards();

        view.init();

        for(Player player : gameManager.getActivePlayers()) {
            player.init(this);            
        }

        view.addInDraw(gameManager.getActivePlayers().get(0).getCards());

        System.err.println("drawCount = " + drawCards.size());
    }

    public List<Card> getPlayerCard(int playerIndex){
        return gameManager.getActivePlayers().get(playerIndex).getCards();
    }

    public List<String> getCurrentFrameInfoFor(Player player) {
        
        List<String> lines = new ArrayList<>();

        String[] cards = (String[]) player.getCards().toArray();

        //lines.add(Integer.toString(Config.PLAYERS_COUNT));
        lines.add(String.join(" ", cards));

        return lines;
    }
    
    public void init_drawCards(){

        drawCards = new Stack<Card>();

        Stack<Card> drawTemp = new Stack<Card>();
        
        for(CardColors color : CardColors.values()){

            for(int i = 0; i < 13; i++){
        
                drawTemp.add(new Card(color, i));
                drawTemp.add(new Card(color, i));
            }
        }

        List<CardColors> bonusColors = Arrays.asList(CardColors.values().clone());

        int indexBonus_1 = random.nextInt(bonusColors.size() - 1);
        int indexBonus_2 = indexBonus_1;

        while(indexBonus_1 == indexBonus_2){
            indexBonus_2 = random.nextInt(bonusColors.size());
        }

        drawTemp.add(new Card(bonusColors.get(indexBonus_1), 13));
        drawTemp.add(new Card(bonusColors.get(indexBonus_2), 13));

        while(drawTemp.size() > 0){

            int cardIndex = random.nextInt(drawTemp.size());

            drawCards.push(drawTemp.get(cardIndex));
            drawTemp.remove(cardIndex);
        }


    }

    public Card takeCard(){

        Card card = drawCards.get(random.nextInt(drawCards.size()));

        drawCards.remove(card);

        return card; 
    }

    public List<String> getGlobalInfoFor(Player player) {
        List<String> lines = new ArrayList<>();

        return lines;
    }    

    public void PRINT_DRAW_CARDS(){
        for(Card card : drawCards){
            System.err.println(card.getHashCode());
        }
    }
}
