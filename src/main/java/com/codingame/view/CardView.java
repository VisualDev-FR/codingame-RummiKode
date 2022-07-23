package com.codingame.view;

import com.codingame.game.card.Card;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;

public class CardView {

    private String spriteCode;
    private Sprite sprite;
    private Card card;
    private int spriteIndex;

    private int row;
    private int col;

    public CardView(GraphicEntityModule gem, Card card, int spriteIndex) {        
        
        this.card = card;
        this.spriteIndex = spriteIndex;
        this.spriteCode = String.format("%s %s", this.card.getHashCode(), this.spriteIndex);

        int[] spriteCoords = this.getCardCoords(row, col);
        
        this.sprite = gem.createSprite()
            .setImage(this.card.getImage())
            .setBaseHeight(View.CARD_SIZE)
            .setBaseWidth(View.CARD_SIZE)
            .setX(spriteCoords[0])
            .setY(spriteCoords[1]);
    }

    private CardView(){
        // For filling board with blank cardViews
    }

    public static CardView getBlank(){
        return new CardView();
    }

    public void setPosition(int row, int col) {
        
        this.row = row;
        this.col = col;

        int[] spriteCoords = this.getCardCoords(row, col);
        
        this.setCoords(spriteCoords[0], spriteCoords[1]);
    }

    public void show(){
        this.sprite.setVisible(true);
    }

    public void hide(){
        this.sprite.setVisible(false);
    }

    public void setCoords(int x, int y) {
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    public Sprite getSprite(){
        return this.sprite;
    }
    
    public String getSpriteCode(){
        return this.spriteCode;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Card getCard() {
        return card;
    }    

    private int[] getCardCoords(int row, int col){

        int cardX = col * View.GRID_SIZE + View.GRID_SIZE / 2 - View.CARD_SIZE / 2;
        int cardY = row * View.GRID_SIZE + View.GRID_SIZE / 2 - View.CARD_SIZE / 2;

        return new int[]{cardX, cardY};
    }    
}
