package com.codingame.view;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.BitmapText;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.entities.Sprite;

public class PlayerView {

    private GraphicEntityModule gem;
    private RoundedRectangle scoreBar;
    private BitmapText scoreMessage;
    
    private int width;

    private final int MAX_SCORE_DISPLAY= 25;
    private final int SCORE_COLOR = 0xa80707;

    public PlayerView(GraphicEntityModule gem, Player player, int playersCount, int xCoord, int yCoord, int width, int height){

        this.gem = gem;
        this.width = width;
        
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

        Sprite playerSprite = this.gem.createSprite().setImage(player.getAvatarToken()); //p.getAvatarToken()

        playerSprite.setX(xCoord - View.AVATAR_SIZE / 2);
        playerSprite.setY(yCoord - View.AVATAR_SIZE / 2);

        playerSprite.setBaseWidth(View.AVATAR_SIZE);
        playerSprite.setBaseHeight(View.AVATAR_SIZE);
        playerSprite.setZIndex(View.Z_PLAYER);

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

        //this.setScore(player.cardsCount());
        this.setScore(14);
    }

    public void setScore(int score){
        int computeWidth = this.width * score / MAX_SCORE_DISPLAY;
        scoreBar.setWidth(Math.min(this.width, computeWidth));
        scoreMessage.setText(score + " cards");
    }
    
}
