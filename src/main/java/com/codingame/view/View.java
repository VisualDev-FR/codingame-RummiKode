package com.codingame.view;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.card.*;


import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class View {
    @Inject private GraphicEntityModule gem;

    private int screenWidth;
    private int screenHeight;

    final int BACK_COLOR = 0x3b3d40;    
    final int DRAW_COLOR = 0x787b80;
    final int GRID_COLOR = 0x00FF00;

    final int GRID_SIZE = 76;
    final int GRID_ROWS = 15;
    final int GRID_COLUMNS = 26;
    final int CARD_SIZE = 65;
    final int BOARD_OFFSET = 20;

    final int DRAW_WIDTH = 4;

    private static Sprite board[][];
    private static Sprite draw[][];

    List<List<Sprite>> stacks;

    public void init(){

        board = new Sprite[GRID_ROWS][GRID_COLUMNS - DRAW_WIDTH];
        draw = new Sprite[GRID_ROWS][DRAW_WIDTH];

        stacks = new ArrayList<List<Sprite>>();

        screenWidth = gem.getWorld().getWidth();
        screenHeight = gem.getWorld().getHeight();

        DisplayBackGround();

        StackSequence sequence = new StackSequence(CardColors.BLUE, 0, 12);
    }

    public void MoveStack(){

    }

    public void PushStackSequence(StackSequence sequence){

        // we search a cell wich has n cells available below, to display the full stackSequence 

        int[] startCoord = getVerticalEmptySpace(sequence.cardsCount(), board);

        List<Sprite> stack = new ArrayList<Sprite>();

        for (int i = 0; i <= sequence.cardsCount(); i++){

            int[] cardPosition = getBoardPosition(startCoord[0] + i, startCoord[1]);

            Card card = sequence.getCard(i);

            stack.add(displayCard(card, cardPosition[0], cardPosition[1]));            
        }

        stacks.add(stack);
    }

    public int[] getVerticalEmptySpace(int length, Sprite[][] space){

        for (int col = 0; col < space[0].length; col++) {
            
            for (int row = 0; row <= space.length - length; row++) {
                
                boolean emptySpace = true;

                for (int k = 0; k <= length; k++){

                    int rowTest = (row + k) % GRID_ROWS;

                    emptySpace = emptySpace && space[rowTest][col] == null;

                    //System.err.printf("[%s, %s] = %s\n", row, col, draw[emptyCell[0]][emptyCell[1]] == null);
                }

                if(emptySpace) return new int[]{row, col};
            }
        }

        return null;
    }

    public void addInDraw(Card card, int row, int col){

        int[] cardPosition = getCardPosition(row, col);

        draw[row][col] = displayCard(card, cardPosition[0], cardPosition[1]);
    }

    public void addInBoard(Card card, int row, int col){
        
        int[] cardPosition = getBoardPosition(row, col);

        board[row][col] = displayCard(card, cardPosition[0], cardPosition[1]);
    }

    public Sprite displayCard(Card card, int row, int col){

        Sprite sprite = gem.createSprite().setImage(card.getImage());

        sprite.setY(row);
        sprite.setX(col);

        sprite.setBaseHeight(CARD_SIZE);
        sprite.setBaseWidth(CARD_SIZE);
        
        return sprite;
    }

    public boolean isCellEmpty(int row, int col){

        return board[row][col] != null && draw[row][col] != null; 
    }

    public void DisplayBackGround(){

        // Background

        gem.createRectangle()
        .setHeight(screenHeight)
        .setWidth(screenWidth)
        .setFillColor(BACK_COLOR);

        // DRAW

        gem.createRectangle()
        .setHeight(screenHeight)
        .setWidth((DRAW_WIDTH) * GRID_SIZE)
        .setFillColor(DRAW_COLOR);
    }

    public void addInDraw(List<Card> cards){

        System.err.println("drawSize = " + cards.size());
        
        for(Card card : cards){

            int[] emptyCell = getVerticalEmptySpace(1, draw);

            //System.err.printf("[%s, %s] = %s\n", emptyCell[0], emptyCell[1], draw[emptyCell[0]][emptyCell[1]] == null);

            addInDraw(card, emptyCell[0], emptyCell[1]);            
        }
    }

    public void DisplayAllCards(){

        for(CardColors color : CardColors.values()){

            int cardCol = color.ordinal();

            for(int i = 0; i < 14; i++){
        
                addInDraw(new Card(color, i), i, cardCol);
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

    public int[] getBoardPosition(int row, int col){

        int cardCol = col + DRAW_WIDTH;

        int[] cardPosition = getCardPosition(row, cardCol);

        return new int[]{cardPosition[0], cardPosition[1] + BOARD_OFFSET};
    }
}
