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

    final int GRID_SIZE = 76;
    final int GRID_ROWS = 15;
    final int GRID_COLUMNS = 26;
    final int CARD_SIZE = 65;
    final int STACK_SIZE = 110;
    final int BOARD_OFFSET = 20;
    final int DRAW_HEIGHT = 3;
    
    private boolean board[][];

    private Map<String, Integer> stackMap;
    private Map<String, Integer> drawMap;

    private Map<Integer, Map<String, Sprite>> stackSprite;
    private Map<Integer, Map<String, Sprite>> drawSprites;

    public void init(Game game){

        this.board = new boolean[GRID_ROWS][GRID_ROWS - DRAW_HEIGHT];

        this.screenWidth = gem.getWorld().getWidth();
        this.screenHeight = gem.getWorld().getHeight();

        this.stackMap = new HashMap<String, Integer>();
        this.drawMap = new HashMap<String, Integer>();

        this.stackSprite = new HashMap<Integer, Map<String, Sprite>>();
        this.drawSprites = new HashMap<Integer, Map<String, Sprite>>();

        //DisplayGrid();              
        initBackGround();
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

        drawSprites.put(-1, new HashMap<String, Sprite>());

        for(Card card : game.getDrawCards()){

            Sprite sprite = gem.createSprite().setImage(card.getImage());
    
            sprite.setBaseWidth(CARD_SIZE);
            sprite.setBaseHeight(CARD_SIZE);

            int spriteY = (screenHeight + (GRID_ROWS - DRAW_HEIGHT) * GRID_SIZE) / 2  - CARD_SIZE / 2;
            int spriteX = (screenWidth - CARD_SIZE) / 2;
            
            sprite.setX(spriteX);
            sprite.setY(spriteY);

            System.err.printf("drawCoords: [%s, %s]\n", spriteX, spriteY);
            
            int spriteIndex = drawMap.containsKey(getSpriteCode(card, 0)) ? 1 : 0;

            drawMap.put(getSpriteCode(card, spriteIndex), -1);
            drawSprites.get(-1).put(getSpriteCode(card, spriteIndex), sprite);
        }

        for(Player player : players){

            drawSprites.put(player.getIndex(), new HashMap<String, Sprite>());

            for(String strCard : player.getCardCodes()){

                Card card = new Card(strCard);

                Sprite sprite = gem.createSprite().setImage(card.getImage());
    
                sprite.setBaseWidth(CARD_SIZE);
                sprite.setBaseHeight(CARD_SIZE);

                int[] playerCoords = getPlayerCoords(player.getIndex());

                System.err.printf("player %S Coords : [%s, %s]\n", player.getIndex(), playerCoords[0], playerCoords[1]);
                
                
                sprite.setX(playerCoords[0] - CARD_SIZE / 2);
                sprite.setY(playerCoords[1] - CARD_SIZE / 2);
        
                int spriteIndex = drawMap.containsKey(getSpriteCode(card, 0)) ? 1 : 0;

                drawMap.put(getSpriteCode(card, spriteIndex), player.getIndex());
                drawSprites.get(player.getIndex()).put(getSpriteCode(card, spriteIndex), sprite);
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

        String spriteCode = getSpriteFromDraw(-1, card);
        Sprite sprite = getSprite(spriteCode);

        assert sprite != null;

        int[] playerCoords = getPlayerCoords(player.getIndex());

        this.drawMap.put(spriteCode, player.getIndex());
        this.drawSprites.get(player.getIndex()).put(spriteCode, sprite);
        removeSpriteFromDraw(spriteCode, -1);

        moveSprite(sprite, playerCoords[0] - CARD_SIZE / 2, playerCoords[1] - CARD_SIZE / 2);
    }

    public void pushStack(Player player, int stackID, PushAction pushAction){

        int[] startCoords = getHorizontalEmptySpace(pushAction.getCards().size());

        if(startCoords == null) return; // TODO: fix this

        List<Card> cards = pushAction.getCards();
        
        int row = startCoords[0];
        int col = startCoords[1];

        int drawIndex = player.getIndex();

        this.stackSprite.put(stackID, new HashMap<String, Sprite>());

        for (int i = 0; i < cards.size(); i++){
            
            String spriteCode = getSpriteFromDraw(drawIndex, cards.get(i));

            System.err.println(spriteCode + " " + drawSprites.get(drawIndex).keySet().toString());

            Sprite sprite = getSprite(spriteCode);

            this.stackMap.put(spriteCode, stackID);
            this.stackSprite.get(stackID).put(spriteCode, sprite);

            removeSpriteFromDraw(spriteCode, drawIndex);
            moveSpriteOnBoard(sprite, row, col + i);
        }
    }

    public void addCard(Player player, int stackID, AddAction addAction){

        /* String spriteCode = getSpriteCode(addAction.getCardToAdd(), player.getIndex());
        Sprite sprite = getSprite(spriteCode);

        this.stackMap.put(spriteCode, stackID);
        this.stackSprite.get(stackID).put(spriteCode, sprite);

        removeSpriteFromDraw(spriteCode, drawIndex);
        moveSpriteOnBoard(sprite, row, col + i); */
    }

    // SPRITE HANDLING

    public String getSpriteCode(Card card, int index){
        return String.format("%s %s", card.getHashCode(), index);
    }

    public void removeSpriteFromDraw(String spriteCode, int drawIndex){
        drawMap.remove(spriteCode);
        drawSprites.get(drawIndex).remove(spriteCode);
    }

    public void removeSpriteFromStack(String spriteCode, int stackID){
        stackMap.remove(spriteCode);
        stackSprite.get(stackID).remove(spriteCode);
    }

    public Sprite getSprite(String spriteCode){

        /* if(stackMap.containsKey(spriteCode)){
            return stackSprite.get(stackMap.get(spriteCode)).get(spriteCode);
        }
        else if(drawMap.containsKey(spriteCode)){
            return drawSprites.get(drawMap.get(spriteCode)).get(spriteCode);
        }
        else{
            assert false : String.format("sprite %s not found.", spriteCode);
            return null;
        } */

        for(Map<String, Sprite> map : this.stackSprite.values()){

            if(map.containsKey(spriteCode)){
                return map.get(spriteCode);
            }
        }

        for(Map<String, Sprite> map : this.drawSprites.values()){

            if(map.containsKey(spriteCode)){
                return map.get(spriteCode);
            }
        } 
        
        assert false : String.format("sprite %s not found.", spriteCode);
        return null;        
    }

    public String getSpriteFromDraw(int drawIndex, Card card){

        if(this.drawSprites.get(drawIndex).containsKey(getSpriteCode(card, 0))){
            return getSpriteCode(card, 0);
        }
        else if(this.drawSprites.get(drawIndex).containsKey(getSpriteCode(card, 1))){
            return getSpriteCode(card, 1);
        }
        else{
            assert false : String.format("card %s not present in draw %s : %s", card.getHashCode(), drawIndex, drawSprites.get(drawIndex).keySet().toString());
            return null;
        }        
    }

    public String getSpriteFromStack(Card card, int stackID){

        if(this.stackSprite.get(stackID).containsKey(getSpriteCode(card, 0))){
            return getSpriteCode(card, 0);
        }
        else if(this.stackSprite.get(stackID).containsKey(getSpriteCode(card, 1))){
            return getSpriteCode(card, 1);
        }
        else{
            assert false : "card not present in stack";
            return null;
        }
    }

    public void moveSpriteOnBoard(Sprite sprite, int row, int col){

        int[] coords = getCardPosition(row, col);

        sprite.setX(coords[0]);
        sprite.setY(coords[1]);

        board[row][col] = true;
    }

    public void moveSprite(Sprite sprite, int x, int y){
        sprite.setX(x);
        sprite.setY(y);
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

    public int[] getCardPosition(int row, int col){

        int cardX = col * GRID_SIZE + GRID_SIZE / 2 - CARD_SIZE / 2;
        int cardY = row * GRID_SIZE + GRID_SIZE / 2 - CARD_SIZE / 2;

        return new int[]{cardX, cardY};
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

    public int[] getHorizontalEmptySpace(int length){

        length++; // in order to have one blank space between two cards

        for (int col = 0; col < board[0].length - length; col++) {
            
            for (int row = 0; row < board.length; row++) {
                
                boolean emptySpace = true;

                for (int k = 0; k <= length; k++){

                    int colTest = (col + k) % GRID_COLUMNS;

                    emptySpace = emptySpace && board[row][colTest] == false;

                    //System.err.printf("[%s, %s] = %s\n", row, col, draw[emptyCell[0]][emptyCell[1]] == null);
                }

                if(emptySpace){
                    return new int[]{row, col};
                }/* else{
                    col = (col + length) % GRID_COLUMNS;
                } */
            }
        }

        return null;
    }

}
