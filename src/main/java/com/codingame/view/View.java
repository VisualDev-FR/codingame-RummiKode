package com.codingame.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codingame.game.Game;
import com.codingame.game.Player;
import com.codingame.game.action.AddAction;
import com.codingame.game.action.JoinAction;
import com.codingame.game.action.MoveAction;
import com.codingame.game.action.PushAction;
import com.codingame.game.action.SplitAction;
import com.codingame.game.action.TakeAction;
import com.codingame.game.card.*;
import com.codingame.game.stack.StackSequence;
import com.codingame.gameengine.module.entities.*;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import view.modules.DisplayOnHoverModule;

@Singleton
public class View {
    
    @Inject private DisplayOnHoverModule displayOnHoverModule;    
    @Inject private GraphicEntityModule gem;
    @Inject private TooltipModule tooltipModule;

    private int screenWidth;
    private int screenHeight;
    private int playersCount;
    private int playerWidth;

    final static int Z_BACK = 1;
    final static int Z_CARD = 2;
    final static int Z_PLAYER = 3;

    final static int BACK_COLOR = 0x3b3d40;    
    final static int DRAW_COLOR = 0x787b80;
    final static int GRID_COLOR = 0x00FF00;

    final static int GRID_SIZE = 76;
    final static int GRID_ROWS = 15;
    final static int GRID_COLUMNS = 26;
    final static int CARD_SIZE = 65;
    final static int SCORE_WIDTH = 30;

    final static int PLAYER_HEIGHT = 110;
    final static int AVATAR_SIZE = 110;
    final static int PLAYER_OFFSET = 20;

    final static int BOARD_ROWS = 11;
    final static int BOARD_COLUMNS = 25;

    private BoardView board;

    private Map<String, Integer> stackMap;
    private Map<String, Integer> drawMap;
    private Map<Integer, StackView> stacks;
    private Map<Integer, StackView> draws;
    private Map<Integer, PlayerView> playerViews;
    private Map<String, CardView> displayHoverCards;

    public void init(Game game){

        this.board = new BoardView(BOARD_ROWS, BOARD_COLUMNS);

        this.screenWidth = gem.getWorld().getWidth();
        this.screenHeight = gem.getWorld().getHeight();

        this.stackMap = new HashMap<String, Integer>();
        this.drawMap = new HashMap<String, Integer>();
        this.stacks = new HashMap<Integer, StackView>();
        this.draws = new HashMap<Integer, StackView>();
        this.displayHoverCards = new HashMap<String, CardView>();
         
        this.playerViews = new HashMap<Integer, PlayerView>();
        this.playersCount = game.getPlayers().size();

        initBackGround();
        //DisplayGrid();
        initSprites(game, game.getPlayers());
        initPlayers(game.getPlayers());
        updateDrawsDisplayHover();

        // ! \\ CODE AFTER THIS LINES       
    }

    public void update(Player player){

        if(player.isActive()){

            updateScoreBar(player);

            if (player.getAction().isMove()) {
                this.moveCard((MoveAction) player.getAction());
            }
            else if (player.getAction().isTake()) {
                this.takeCard(player);
            }
            else if (player.getAction().isAdd()) {
                this.addCard(player);
            }
            else if (player.getAction().isPush()) {
                this.pushStack(player);
            }
            else if (player.getAction().isSplit()) {
                this.splitStack((SplitAction) player.getAction());
            }
            else if (player.getAction().isJoin()) {
                this.joinStack((JoinAction) player.getAction());
            }
        }
        else{

            killPlayer(player);

        }        

        updateStacksToolTips();
        updateDrawsDisplayHover();
    }

    private void killPlayer(Player player){
        for(CardView cardView : this.draws.get(player.getIndex()).getCardViews().values()){
            cardView.hide();
        }
        this.draws.get(player.getIndex()).moveTo(this.draws.get(-1));
        playerViews.get(player.getIndex()).kill(displayOnHoverModule);
    }

    private void updateScoreBar(Player player){
        playerViews.get(player.getIndex()).setScore(player.cardsCount());
    }

    // INITIALIZE

    public void initBackGround(){

        // Background

        gem.createRectangle()
        .setZIndex(Z_BACK)
        .setHeight(screenHeight)
        .setWidth(screenWidth)
        .setFillColor(BACK_COLOR);
    }

    public void initSprites(Game game, List<Player> players){

        draws.put(-1, new StackView(gem, -1, null));

        for(Card card : game.getDrawCards()){

            int spriteIndex = drawMap.containsKey(String.format("%s %s", card.getHashCode(), 0)) ? 1 : 0;

            int spriteX = (screenWidth - CARD_SIZE) / 2;
            int spriteY = getPlayerCoords(0)[1] - CARD_SIZE / 2;   

            CardView cardView = new CardView(gem, card, spriteIndex);
            CardView displayHoverCard = new CardView(gem, card, spriteIndex);
            
            displayHoverCard.hide();
            displayHoverCards.put(cardView.getSpriteCode(), displayHoverCard);

            cardView.hide();
            cardView.setCoords(spriteX, spriteY);

            drawMap.put(cardView.getSpriteCode(), -1);
            draws.get(-1).addCardView(cardView);
        }

        for(Player player : players){

            draws.put(player.getIndex(), new StackView(gem, player.getIndex(), null));

            for(String strCard : player.getCardCodes()){

                Card card = new Card(strCard);

                int spriteIndex = drawMap.containsKey(String.format("%s %s", card.getHashCode(), 0)) ? 1 : 0;
                int[] playerCoords = getPlayerCoords(player.getIndex());

                CardView cardView = new CardView(gem, card, spriteIndex);
                CardView displayHoverCard = new CardView(gem, card, spriteIndex);
            
                displayHoverCard.hide();
                displayHoverCards.put(cardView.getSpriteCode(), displayHoverCard);
                
                cardView.setCoords(playerCoords[0] - CARD_SIZE / 2, playerCoords[1]  - CARD_SIZE / 2);

                drawMap.put(cardView.getSpriteCode(), player.getIndex());
                draws.get(player.getIndex()).addCardView(cardView);
            }
        }     
    }

    public void initPlayers(List<Player> players){

        this.playerWidth = (screenWidth - 2 * PLAYER_OFFSET) / this.playersCount;

        for(Player player : players){

            int[] playerCoords = getPlayerCoords(player.getIndex());

            PlayerView playerView = new PlayerView(gem, player, players.size(), playerCoords[0], playerCoords[1], this.playerWidth, PLAYER_HEIGHT);

            playerViews.put(player.getIndex(), playerView);
        }
    }

    // PLAYS VIEWER

    public void drawCard(Player player, Card card){

        updateScoreBar(player);

        assert card != null : "card is null :'( ";

        CardView cardView = this.draws.get(-1).getCardView(card);

        cardView.show();

        removeSpriteFromDraw(cardView.getSpriteCode(), -1);

        this.drawMap.put(cardView.getSpriteCode(), player.getIndex());
        this.draws.get(player.getIndex()).addCardView(cardView);        

        board.update(this);
    }

    public void pushStack(Player player){

        PushAction pushAction = (PushAction) player.getAction();

        int stackID = pushAction.getStackID();
        List<Card> cards = pushAction.getCards();

        int drawIndex = player.getIndex();

        this.stacks.put(stackID, new StackView(gem, pushAction.getStackID(), pushAction.getType()));

        for (int i = 0; i < cards.size(); i++){
            
            CardView cardView = this.draws.get(drawIndex).getCardView(cards.get(i));

            this.stackMap.put(cardView.getSpriteCode(), stackID);

            StackView stackView = this.stacks.get(stackID);            
            
            removeSpriteFromDraw(cardView.getSpriteCode(), drawIndex);

            stackView.addCardView(cardView);
        }

        board.update(this);
    }

    public void addCard(Player player){

        AddAction addAction = (AddAction) player.getAction();

        int stackID = addAction.getStackID();
        int drawIndex = player.getIndex();
        Card cardToAdd = addAction.getCardToAdd();
        
        CardView cardView = this.draws.get(drawIndex).getCardView(cardToAdd);
        StackView stackView = this.stacks.get(stackID);

        this.stackMap.put(cardView.getSpriteCode(), stackID);        

        removeSpriteFromDraw(cardView.getSpriteCode(), drawIndex);

        stackView.addCardView(cardView);

        board.update(this);
    }

    public void splitStack(SplitAction splitAction){

        int stackID = splitAction.getStackID();
        int newStackId = splitAction.getNewStackID();

        StackSequence stack1 = splitAction.getStack1();
        StackSequence stack2 = splitAction.getStack2();

        this.stacks.put(newStackId, new StackView(gem, stack2.getID(), stack2.getType()));

        for(CardView cardView : new ArrayList<CardView>(this.stacks.get(stackID).getCardViews().values())){

            String spriteCode = cardView.getSpriteCode();

            if(stack2.containsCard(cardView.getCard())){

                removeSpriteFromStack(spriteCode, stackID);

                this.stackMap.put(spriteCode, newStackId);
                this.stacks.get(newStackId).addCardView(cardView);
            }

            /* if(!stack1.containsCard(cardView.getCard())){
                removeSpriteFromStack(spriteCode, stackID);
            } */
        }

        System.err.println(stack1.toString());
        System.err.println(stack2.toString());

        board.update(this);
    }

    public void joinStack(JoinAction joinAction){

        int stackID = joinAction.getStackID_1();
        int oldStackID = joinAction.getStackID_2();

        StackView oldStack = this.stacks.get(oldStackID);

        for(CardView cardView : oldStack.getCardViews().values()){

            String spriteCode = cardView.getSpriteCode();

            this.stackMap.put(spriteCode, stackID);
            this.stacks.get(stackID).addCardView(cardView);
            
            removeSpriteFromStack(spriteCode, oldStackID);            
        }

        board.update(this);
    }

    public void moveCard(MoveAction moveAction){

        int stackFrom = moveAction.getStackID_From();
        int stackTo = moveAction.getStackID_To();
        Card cardToMove = moveAction.getCardToMove();

        CardView cardViewToMove = this.stacks.get(stackFrom).getCardView(cardToMove);

        String spriteCode = cardViewToMove.getSpriteCode();

        this.stackMap.put(spriteCode, stackTo);
        this.stacks.get(stackTo).addCardView(cardViewToMove);

        removeSpriteFromStack(spriteCode, stackFrom);        

        if(moveAction.doesMakeNewStack()){
            splitStack(moveAction.getSplitAction());
        }

        board.update(this);
    }

    public void takeCard(Player player){

        TakeAction takeAction = (TakeAction) player.getAction();

        int stackID = takeAction.getStackID();
        Card cardToTake = takeAction.getCardToTake();
        int drawIndex = player.getIndex();

        System.err.println("Stack " + stackID + " : " + this.stacks.get(stackID).toString() + " / " +  cardToTake.getHashCode());

        CardView cardViewToTake = this.stacks.get(stackID).getCardView(cardToTake);

        String spriteCode = cardViewToTake.getSpriteCode();

        if(takeAction.doesMakeNewStack()){
            splitStack(takeAction.getSplitAction());
        }

        removeSpriteFromStack(spriteCode, stackID);        

        this.drawMap.put(spriteCode, drawIndex);
        this.draws.get(drawIndex).addCardView(cardViewToTake);        

        board.update(this);
    }

    // SPRITE HANDLING

    public StackView getStackView(CardView cardView){

        // will be usefull for implementation of tiptools, in order to display the stackIDs at screen

        String spriteCode = cardView.getSpriteCode();

        if(this.stackMap.containsKey(spriteCode)){
            int stackID = stackMap.get(spriteCode);
            return this.stacks.get(stackID);
        }
        else if(this.drawMap.containsKey(spriteCode)){
            int stackID = drawMap.get(spriteCode);
            return this.draws.get(stackID);
        }
        else{
            assert false;
            return null;
        }
    }

    public void removeSpriteFromDraw(String spriteCode, int drawIndex){
        drawMap.remove(spriteCode);
        draws.get(drawIndex).removeCardView(spriteCode);
    }

    public void removeSpriteFromStack(String spriteCode, int stackID){
        System.err.printf("spriteCode = %s, stackId = %s\n", spriteCode, stackID);
        stackMap.remove(spriteCode);
        stacks.get(stackID).removeCardView(spriteCode);
    }

    public Map<Integer, StackView> getStacks(){
        return this.stacks;
    }

    public Map<Integer, StackView> getDraws(){
        return this.draws;
    }
    
    // DISPLAY ON-HOVER HANDLING

    private void updateStacksToolTips(){
        for(StackView stackView : this.stacks.values()){
            stackView.refreshTooltip(tooltipModule);
        }
    }

    private void updateDrawsDisplayHover(){

        for(PlayerView playerView : this.playerViews.values()){

            StackView drawView = this.draws.get(playerView.getPlayer().getIndex());

            List<CardView> displayHoverCards = getDisplayHoverCards(drawView.getCardViews().values());

            playerView.refreshDisplayHover(gem, displayOnHoverModule, displayHoverCards);
        }
    }

    private List<CardView> getDisplayHoverCards(Collection<CardView> cardViews){

        List<CardView> displayHoverCards = new ArrayList<CardView>();

        for(CardView cardView : cardViews){
            displayHoverCards.add(this.displayHoverCards.get(cardView.getSpriteCode()));
        }

        return displayHoverCards;
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

        int playerX = this.playerWidth * playerIndex + AVATAR_SIZE / 2 + PLAYER_OFFSET;
        int playerY = screenHeight - PLAYER_HEIGHT / 2 - AVATAR_SIZE / 2;

        return new int[] {playerX, playerY};
    }

    public void PrintTooltips(){

        for(StackView stackView : this.stacks.values()){
            for(CardView cardView : stackView.getCardViews().values()){
                System.err.printf("%s\n\n", tooltipModule.getTooltipText(cardView.getSprite()));
            }
        }

    }


}
