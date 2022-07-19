package com.codingame.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codingame.game.Game;
import com.codingame.game.Player;
import com.codingame.game.action.AddAction;
import com.codingame.game.action.PushAction;
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

    final static int GRID_SIZE = 76;
    final static int GRID_ROWS = 15;
    final static int GRID_COLUMNS = 26;
    final static int CARD_SIZE = 65;
    final static int STACK_SIZE = 110;
    final static int BOARD_OFFSET = 20;
    final static int DRAW_HEIGHT = 3;
    final static int BOARD_COLUMNS = 26;
    final static int BOARD_ROWS = 11;
    
    private boolean board[][];

    private Map<String, Integer> stackMap;
    private Map<String, Integer> drawMap;

    private Map<Integer, StackView> stacks;
    private Map<Integer, StackView> draws;

    public void init(Game game){

        this.board = new boolean[BOARD_ROWS][BOARD_COLUMNS];

        this.screenWidth = gem.getWorld().getWidth();
        this.screenHeight = gem.getWorld().getHeight();

        this.stackMap = new HashMap<String, Integer>();
        this.drawMap = new HashMap<String, Integer>();

        this.stacks = new HashMap<Integer, StackView>();
        this.draws = new HashMap<Integer, StackView>();

        initBackGround();
        //DisplayGrid();
        initSprites(game, game.getPlayers());
        initDraws(game.playersCount());

        // ! \\ CODE AFTER THIS LINES
    }

    // INITIALIZE

    public void initBackGround(){

        // Background

        gem.createRectangle()
        .setHeight(screenHeight)
        .setWidth(screenWidth)
        .setFillColor(BACK_COLOR);

        // DRAW

        gem.createRectangle()
        .setY((GRID_ROWS - DRAW_HEIGHT) * GRID_SIZE)
        .setHeight((DRAW_HEIGHT + 1) * GRID_SIZE)
        .setWidth(screenWidth)
        .setFillColor(DRAW_COLOR);
    }

    public void initSprites(Game game, List<Player> players){

        draws.put(-1, new StackView());

        for(Card card : game.getDrawCards()){

            int spriteIndex = drawMap.containsKey(String.format("%s %s", card.getHashCode(), 0)) ? 1 : 0;

            int spriteX = (screenWidth - CARD_SIZE) / 2;
            int spriteY = (screenHeight + (GRID_ROWS - DRAW_HEIGHT) * GRID_SIZE) / 2  - CARD_SIZE / 2;   

            CardView cardView = new CardView(gem, card, spriteIndex);

            cardView.setCoords(spriteX, spriteY);

            drawMap.put(cardView.getSpriteCode(), -1);
            draws.get(-1).addCardView(cardView);
        }

        for(Player player : players){

            draws.put(player.getIndex(), new StackView());

            for(String strCard : player.getCardCodes()){

                Card card = new Card(strCard);

                int spriteIndex = drawMap.containsKey(String.format("%s %s", card.getHashCode(), 0)) ? 1 : 0;
                int[] playerCoords = getPlayerCoords(player.getIndex());

                CardView cardView = new CardView(gem, card, spriteIndex);

                cardView.setCoords(playerCoords[0], playerCoords[1]);

                drawMap.put(cardView.getSpriteCode(), player.getIndex());
                draws.get(player.getIndex()).addCardView(cardView);
            }
        }     
    }

    public void initDraws(int playersCount){

        Sprite bluesStack = gem.createSprite().setImage("blue_stack.png");

        int[] bluePlayerCoords = getPlayerCoords(0);

        bluesStack.setX(bluePlayerCoords[0] - STACK_SIZE / 2);
        bluesStack.setY(bluePlayerCoords[1] - STACK_SIZE / 2);

        bluesStack.setBaseWidth(STACK_SIZE);
        bluesStack.setBaseHeight(STACK_SIZE);

        Sprite yellowStack = gem.createSprite().setImage("yellow_stack.png");

        int[] yellowPlayerCoords = getPlayerCoords(1);

        yellowStack.setX(yellowPlayerCoords[0] - STACK_SIZE / 2);
        yellowStack.setY(yellowPlayerCoords[1] - STACK_SIZE / 2);

        yellowStack.setBaseWidth(STACK_SIZE);
        yellowStack.setBaseHeight(STACK_SIZE);  
        
        Sprite drawStack = gem.createSprite().setImage("draw_stack.png");

        drawStack.setX((screenWidth / 2) - STACK_SIZE / 2);
        drawStack.setY(yellowPlayerCoords[1] - STACK_SIZE / 2);

        drawStack.setBaseWidth(STACK_SIZE);
        drawStack.setBaseHeight(STACK_SIZE);        

    }

    // PLAYS VIEWER

    public void drawCard(Player player, Card card){

        if(card == null) return;

        int[] playerCoords = getPlayerCoords(player.getIndex());

        CardView cardView = this.draws.get(-1).getCardView(card);
        
        cardView.setCoords(playerCoords[0], playerCoords[1]);

        this.drawMap.put(cardView.getSpriteCode(), player.getIndex());
        this.draws.get(player.getIndex()).addCardView(cardView);
        
        removeSpriteFromDraw(cardView.getSpriteCode(), -1);
    }

    public void pushStack(Player player, int stackID, PushAction pushAction){

        int[] startPosition = getEmptyPosition(pushAction.getCards().size());

        if(startPosition == null) return; // TODO: fix this

        List<Card> cards = pushAction.getCards();

        int drawIndex = player.getIndex();

        this.stacks.put(stackID, new StackView());

        for (int i = 0; i < cards.size(); i++){
            
            CardView cardView = this.draws.get(drawIndex).getCardView(cards.get(i));

            this.stackMap.put(cardView.getSpriteCode(), stackID);

            StackView stackView = this.stacks.get(stackID);
            
            stackView.addCardView(cardView);
            stackView.setPosition(startPosition[0], startPosition[1]);

            setOccupied(stackView.getPositions());

            removeSpriteFromDraw(cardView.getSpriteCode(), drawIndex);
        }
    }

    public void addCard(Player player, int stackID, AddAction addAction){

        // TODO: implement this function

        int drawIndex = player.getIndex();
        Card cardToAdd = addAction.getCardToAdd();
        
        CardView cardView = this.draws.get(drawIndex).getCardView(cardToAdd);
        StackView stackView = this.stacks.get(stackID);

        this.stackMap.put(cardView.getSpriteCode(), stackID);

        int[] positionBeforeAdd = stackView.getPosition();
        
        stackView.addCardView(cardView);
        stackView.setPosition(positionBeforeAdd[0], positionBeforeAdd[1]);

        removeSpriteFromDraw(cardView.getSpriteCode(), drawIndex);
    }

    // SPRITE HANDLING

    public void removeSpriteFromDraw(String spriteCode, int drawIndex){
        drawMap.remove(spriteCode);
        draws.get(drawIndex).removeCardView(spriteCode);
    }

    public void removeSpriteFromStack(String spriteCode, int stackID){
        stackMap.remove(spriteCode);
        stacks.get(stackID).removeCardView(spriteCode);
    }

    // GRID HANDLING

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

    public int[] getPlayerCoords(int playerIndex){

        int playerY = (screenHeight + (GRID_ROWS - DRAW_HEIGHT) * GRID_SIZE) / 2;

        if(playerIndex == 0){
            return new int[]{GRID_SIZE,  playerY};
        }
        else if(playerIndex == 1){
            return new int[]{(GRID_COLUMNS * GRID_SIZE - GRID_SIZE * 2), playerY};
        }
        return null;
    }

    public int[] getEmptyPosition(int length){        
        
        for (int col = 0; col < board[0].length; col++) {   
            
            for (int row = 0; row < board.length; row++) {
                
                boolean emptySpace = true;

                for (int k = 0; k <= length; k++){

                    int colTest = col + k;

                    if(colTest < board[0].length){

                        emptySpace = emptySpace && board[row][colTest] == false;

                        System.err.printf("[%s, %s] = %s\n", row, col, board[row][colTest] == false);
                        
                    }else{
                        emptySpace = false;
                        break;
                    }

                }

                if(emptySpace){ return new int[]{row, col + (col > 0 ? 1 : 0)};}
            }
        }

        return null;
    }

    public void setFree(List<int[]> positions){
        for(int[] position : positions){
            setFree(position[0], position[1]);
        }
    }

    public void setOccupied(List<int[]> positions){
        for(int[] position : positions){
            setOccupied(position[0], position[1]);
        }
    }

    public void setFree(int row, int col){
        board[row][col] = false;
    }

    public void setOccupied(int row, int col){
        board[row][col] = true;
    }


}
