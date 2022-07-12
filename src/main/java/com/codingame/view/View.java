package com.codingame.view;

import com.codingame.game.Card;
import com.codingame.game.CardColors;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class View {
    @Inject private GraphicEntityModule gem;

    private int screenWidth;
    private int screenHeight;
    
    final int DRAW_COLOR = 0x00FF00;
    final int GRID_COLOR = 0x00FF00;

    final int GRID_SIZE = 76;
    final int GRID_ROWS = 15;
    final int GRID_COLUMNS = 26;    
    final int CARD_SIZE = 65;

    final int DRAW_WIDTH = 4;

    public void init(){

        screenWidth = gem.getWorld().getWidth();
        screenHeight = gem.getWorld().getHeight();
        //int gameZoneWidth = 2220;
        //int gameZoneHeight = 1080;

        //DisplayGrid();
        DisplayDraw();
        DisplayAllCards();
   
    }    

    public void DisplayDraw(){

        gem.createRectangle()
        .setHeight(screenHeight)
        .setWidth((DRAW_WIDTH) * GRID_SIZE);
    }

    public void DisplayAllCards(){

        for(CardColors color : CardColors.values()){

            int cardCol = color.ordinal();

            for(int i = 0; i < 14; i++){

                Card card = new Card(color, i);

                Sprite sprite = gem.createSprite().setImage(card.getImage());    

                int[] cardPosition = getCardPosition(i, cardCol);
        
                sprite.setY(cardPosition[0]);
                sprite.setX(cardPosition[1]);

                sprite.setBaseHeight(CARD_SIZE);
                sprite.setBaseWidth(CARD_SIZE);
            }
        }             
    }

    public void DisplayGrid(){

        for(int i = 0; i < GRID_COLUMNS; i++){

            DrawVerticalGrid(GRID_SIZE * i);
        }

        for (int j = 0; j < GRID_ROWS; j++) {

            DrawHorizontalGrid(GRID_SIZE * j);
        }        
    }

    public void DrawHorizontalGrid(int row){

        gem.createLine()
        .setLineWidth(1)
        .setLineColor(GRID_COLOR)
        .setX(0)
        .setY(row)
        .setX2(screenWidth)
        .setY2(row);
    }

    public void DrawVerticalGrid(int col){

        gem.createLine()
        .setLineWidth(1)
        .setLineColor(GRID_COLOR)
        .setX(col)
        .setY(0)
        .setX2(col)
        .setY2(screenHeight);
    }

    public int[] getCardPosition(int row, int col){

        int cardRow = row * GRID_SIZE + GRID_SIZE / 2 - CARD_SIZE / 2;
        int cardCol = col * GRID_SIZE + GRID_SIZE / 2 - CARD_SIZE / 2;

        return new int[]{cardRow, cardCol};
    }
}
