package com.codingame.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.BitmapText;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.entities.Sprite;
import view.modules.DisplayOnHoverModule;

public class PlayerView {

    private GraphicEntityModule gem;
    private RoundedRectangle scoreBar;
    private BitmapText scoreMessage;
    private Sprite playerSprite;
    private Player player;
    private Group drawGroup;
    private Rectangle drawBackGround;    
    private List<CardView> cardViews;
    
    private int width;

    private final int MAX_SCORE_DISPLAY= 25;
    private final int SCORE_COLOR = 0xa80707;

    public PlayerView(GraphicEntityModule gem, Player player, int playersCount, int xCoord, int yCoord, int width, int height){

        this.cardViews = new ArrayList<CardView>();
        this.player = player;
        this.gem = gem;
        this.width = width;

        // init the displayHover entity

        this.drawBackGround = gem.createRectangle()
            .setFillColor(0x000000)
            .setAlpha(0.95);
        
        this.drawGroup = gem.createGroup();        
        this.drawGroup
            .setVisible(false)
            .setZIndex(View.Z_CARD)
            .add(drawBackGround);
        
        // init the player BackGround

        int backGroundOffset = 0;
        int backGroundSize = View.CARD_SIZE;

        this.gem.createRectangle()
            .setX(xCoord - backGroundSize / 2 - backGroundOffset / 2)
            .setY(yCoord - backGroundSize / 2 - backGroundOffset / 2)
            .setHeight(backGroundSize + backGroundOffset)
            .setWidth(backGroundSize + backGroundOffset)
            .setFillColor(View.BACK_COLOR)
            .setZIndex(View.Z_PLAYER);

        // init the player avatar sprite

        this.playerSprite = this.gem.createSprite().setImage(player.getAvatarToken()); //p.getAvatarToken()

        this.playerSprite.setX(xCoord - View.AVATAR_SIZE / 2);
        this.playerSprite.setY(yCoord - View.AVATAR_SIZE / 2);

        this.playerSprite.setBaseWidth(View.AVATAR_SIZE);
        this.playerSprite.setBaseHeight(View.AVATAR_SIZE);
        this.playerSprite.setZIndex(View.Z_PLAYER);

        // init the player name

        int textOffsetX = 10;

        gem.createBitmapText()
        .setFont("BRLNS_66")
        .setFontSize(36)
        .setText(player.getNicknameToken())
        .setMaxWidth(width - View.AVATAR_SIZE - textOffsetX * 2)
        .setX(xCoord + View.AVATAR_SIZE / 2 + textOffsetX)
        .setY(yCoord - View.AVATAR_SIZE / 2)
        .setZIndex(View.Z_PLAYER);

        // init the player score message

        scoreMessage = gem.createBitmapText()
            .setFont("BRLNS_66")
            .setFontSize(36)
            .setText(player.cardsCount() + " cards")
            .setMaxWidth(width - View.AVATAR_SIZE - textOffsetX * 2)
            .setX(xCoord + View.AVATAR_SIZE / 2 + textOffsetX)
            .setY(yCoord)
            .setZIndex(View.Z_PLAYER);

        // init the score bar

        this.scoreBar = this.gem.createRoundedRectangle();

        int scoreBarWidth = width - 5;
        int scoreBarHeight = 20;

        scoreBar            
        .setWidth(scoreBarWidth)
        .setHeight(scoreBarHeight)
        .setX(xCoord - View.AVATAR_SIZE / 2)
        .setY(yCoord + View.AVATAR_SIZE / 2 + 10)
        .setFillColor(SCORE_COLOR)
        .setZIndex(View.Z_PLAYER);

        this.setScore(14);
    }

    public Sprite getSprite(){
        return this.playerSprite;
    }

    public void setScore(int score){
        int computeWidth = this.width * score / MAX_SCORE_DISPLAY;
        scoreBar.setWidth(Math.min(this.width, computeWidth));
        scoreMessage.setText(score + " cards");
    }
    
    public Player getPlayer(){
        return this.player;
    }

    private int roundUp(double val){
        int res = (int) Math.ceil(val);
        return res;
    }

    public void refreshDisplayHover(GraphicEntityModule gem, DisplayOnHoverModule displayOnHoverModule, Collection<CardView> cardViews){

        // refresh the draw background size

        int backWidth = gem.getWorld().getWidth();
        int backHeight = View.GRID_SIZE * Math.max(1, roundUp((double) cardViews.size() / View.BOARD_COLUMNS)) + 20;
        int displayY = View.GRID_SIZE * (View.BOARD_ROWS + 1) - backHeight - 10;

        this.drawGroup.setY(displayY);
        this.drawBackGround
            .setWidth(backWidth)
            .setHeight(backHeight);

        // refresh the card sprites        
        
        resetCardViews();

        int cardIndex = 0;
        for (CardView cardView : cardViews) {

            int row = (int) Math.ceil(cardIndex / View.BOARD_COLUMNS);
            int col = cardIndex % (View.BOARD_COLUMNS);

            this.drawGroup.add(cardView.getSprite());
            this.cardViews.add(cardView);

            cardView.setZIndex(View.Z_CARD);     
            cardView.getSprite()
            .setX(col * View.GRID_SIZE + View.GRID_SIZE / 2 - View.CARD_SIZE / 2 + 10)
            .setY(row * View.GRID_SIZE + View.GRID_SIZE / 2 - View.CARD_SIZE / 2 + 10);
            cardView.show();

            cardIndex++;
        }
        
        displayOnHoverModule.setDisplayHover(this.playerSprite, this.drawGroup);
    }

    public void kill(DisplayOnHoverModule displayOnHoverModule, GraphicEntityModule gem){
        scoreBar.setWidth(0);
        scoreMessage.setText("Disqualified...");
        displayOnHoverModule.untrack(this.playerSprite);

        int crossWidth = 10;

        gem.createLine()
        .setLineWidth(crossWidth)
        .setLineColor(SCORE_COLOR)
        .setX(this.playerSprite.getX())
        .setY(this.playerSprite.getY())
        .setX2(this.playerSprite.getX() + View.AVATAR_SIZE)
        .setY2(this.playerSprite.getY() + View.AVATAR_SIZE)
        .setZIndex(View.Z_PLAYER + 1);

        gem.createLine()
        .setLineWidth(crossWidth)
        .setLineColor(SCORE_COLOR)
        .setX(this.playerSprite.getX() + View.AVATAR_SIZE)
        .setY(this.playerSprite.getY())
        .setX2(this.playerSprite.getX())
        .setY2(this.playerSprite.getY() + View.AVATAR_SIZE)
        .setZIndex(View.Z_PLAYER + 1);        
    }

    public void resetCardViews(){
        for(CardView cardView : cardViews){
            this.drawGroup.remove(cardView.getSprite());
            cardView.hide();
        }
        cardViews = new ArrayList<CardView>();
    }
}
