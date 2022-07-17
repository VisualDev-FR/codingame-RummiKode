package com.codingame.game;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import com.codingame.game.Config.Config;
import com.codingame.game.Summary.GameSummaryManager;
import com.codingame.game.action.*;
import com.codingame.game.card.Card;
import com.codingame.game.card.CardColors;
import com.codingame.game.stack.StackType;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;

import com.google.inject.Inject;

public class Referee extends AbstractReferee {

    @Inject private GameSummaryManager gameSummaryManager;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private Game game;
    @Inject private InputChecker checker;
    
    private static final int MAX_TURNS = 200;

    private int activePlayerId;

    final boolean PERFORM_UPDATE = true;

    int maxFrames;
    boolean gameOverFrame;    

    @Override
    public void init() {

        // Set configuration depending on game rules:
        //Config.setDefaultValueByLevel(LeagueRules.fromIndex(gameManager.getLeagueLevel()));
        activePlayerId = 0;

        // Override configuration with game parameters:
        if (System.getProperty("allow.config.override") != null) {
            computeConfiguration(gameManager.getGameParameters());
        }
        maxFrames = MAX_TURNS;

        try {
            gameManager.setFrameDuration(500);
            gameManager.setMaxTurns(MAX_TURNS);
            gameManager.setFirstTurnMaxTime(1000);
            gameManager.setTurnMaxTime(50);
            
            game.init(gameManager.getRandom());
            sendGlobalInfo();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Referee failed to initialize");
            abort();
        }

    }

    @Override
    public void gameTurn(int turn){

        if(!PERFORM_UPDATE) return;

        Player player = gameManager.getPlayer(activePlayerId);
        for (String line : game.getCurrentFrameInfoFor(player)) {
            player.sendInputLine(line);
        }

        player.execute();
        

        try {
            parseCommands(player, player.getOutputs(), game);
            if (player.isActive()) {
                game.performGameUpdate(player);
            }
        } catch (TimeoutException e) {
            deactivatePlayer(player, "Timeout!");
            gameSummaryManager.addPlayerTimeout(player);
            gameSummaryManager.addPlayerDisqualified(player);
        } catch (Exception e) {
            deactivatePlayer(player, e.getMessage());
            gameSummaryManager.addPlayerTimeout(player);
            gameSummaryManager.addPlayerDisqualified(player);
        } 
        
        setNextPhase(player);
        gameManager.addToGameSummary(gameSummaryManager.getSummary());

        //view.refreshCards(game);
        //view.refreshApplications(game);
        //view.refreshPlayersTooltips(game);

        if (game.isGameOver()) {
            gameOverFrame = true;
        }
       
    }

    public void deactivatePlayer(Player player, String message) {
        player.deactivate(escapeHTMLEntities(message));
        player.setScore(-1);
    }

    private String escapeHTMLEntities(String message) {
        return message
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }    

    private void abort() {
        System.err.println("Unexpected game end");
        gameManager.endGame();
    }    

    private void computeConfiguration(Properties gameParameters) {
        Config.apply(gameParameters);
    }    

    private void sendGlobalInfo() {
        for (Player player : gameManager.getActivePlayers()) {
            for (String line : game.getGlobalInfoFor(player)) {
                player.sendInputLine(line);
            }
        }
    }

    private void setNextPhase(Player activePlayer){
        // TODO: find a best way to decide if we switch player
        if(!activePlayer.canPlay(game)) switchToNextPlayer(activePlayer);
    }

    private void switchToNextPlayer(Player player) {

        if(player.hasToDraw()){
            player.drawCard(game);
            gameSummaryManager.drawCard(player, game.drawCards.size());
        }

        // move to next player

        activePlayerId = (activePlayerId + 1) % gameManager.getPlayerCount();
        gameManager.getPlayer(activePlayerId).update(game);
        if(game.isLastTurn()) {
            gameOverFrame = true;
        }
    }    

    public void parseCommands(Player player, List<String> lines, Game game){
        
        for (String command : lines) {
            try {
                parseCommand(player, command, game);
            } catch (InvalidInputException e) {
                gameSummaryManager.addPlayerBadCommand(player, e);
                gameSummaryManager.addPlayerDisqualified(player);
                deactivatePlayer(player, e.getMessage());
            } catch (GameRuleException e) {
                gameSummaryManager.addPlayerRuleViolation(player, e);
                gameSummaryManager.addPlayerDisqualified(player);
                deactivatePlayer(player, e.getMessage());
            }
        }
    }

    public void parseCommand(Player player, String command, Game game) throws GameRuleException, InvalidInputException {

        /* 
            WAIT
            TAKE <stackID> <cardCode>
            ADD <stackID> <cardCode>
            PUSH <cardCode1>, <cardCode2>...
            SPLIT <stackID> <cardCode_1> <cardCode_2>
            JOIN <stackID_1> <stackID_2>
            MOVE <stackID_From> <stackID_To> <cardCodeFrom>
         */

        if(checker.isWaitAction(command)){

            player.setAction(new WaitAction());

        }else if(checker.isTakeAction(command)){

            TakeAction action = parseTakeAction(player, command);
            player.setAction(action);
        
        }else if(checker.isAddAction(command)){

            AddAction action = parseAddAction(player, command);
            player.setAction(action);
        
        }else if(checker.isPushAction(command)){

            PushAction action = parsePushAction(player, command);            
            player.setAction(action);

        }else if(checker.isSplitAction(command)){

            SplitAction action = parseSplitAction(player, command);
            player.setAction(action);
        
        }else if(checker.isJoinAction(command)){

            JoinAction action = parseJoinAction(player, command);
            player.setAction(action);
        
        }else if(checker.isMoveAction(command)){

            MoveAction action = parseMoveAction(player, command);
            player.setAction(action);
        
        }else{

            throw new InvalidInputException("WAIT | TAKE | ADD | PUSH | SPLIT | JOIN | MOVE", command.split(" ")[0]);        
        }        
    }

    // ACTION PARSING

    private TakeAction parseTakeAction(Player player, String command) throws InvalidInputException, GameRuleException{

        //TAKE <stackID> <cardCode>

        checker.didPushFirstSequence(player, command);

        int stackID = -1;
        String strCard = "";

        try {            
            stackID = Integer.parseInt(command.split(" ")[1]);
            strCard = command.split(" ")[2];
        } catch (Exception e){
            throw new InvalidInputException(Action.TAKE_PATTERN, command);
        }

        Card cardToTake = getCardFromHashCode(strCard);

        checker.doesStackExist(game, command, stackID);
        checker.doesStackContains(game, command, stackID, cardToTake);

        return new TakeAction(stackID, cardToTake);
    }

    private AddAction parseAddAction(Player player, String command) throws InvalidInputException, GameRuleException{

        // ADD <stackID> <cardCode>

        checker.didPushFirstSequence(player, command);

        int stackID = -1;
        String strCard = "";

        try {
            stackID = Integer.parseInt(command.split(" ")[1]);
            strCard = command.split(" ")[2];
        } catch (Exception e) {
            throw new InvalidInputException(Action.ADD_PATTERN, command);
        }

        Card card = getCardFromHashCode(strCard);

        checker.doesHaveThisCard(player, command, card);
        checker.doesStackExist(game, command, stackID);
        checker.canAddInStack(game, command, stackID, card);

        return new AddAction(stackID, card);        
    }

    private PushAction parsePushAction(Player player, String command) throws InvalidInputException, GameRuleException {

        // PUSH <cardCode1>, <cardCode2>...

        String[] command_split = command.split(" ");
        TreeMap<String, Card> cards = new TreeMap<String, Card>();

        for(int i = 1; i < command_split.length; i++){

            Card newCard = getCardFromHashCode(command_split[i]);
            
            checker.doesHaveThisCard(player, command, newCard);

            if(cards.containsKey(newCard.getHashCode())){                    
                throw new GameRuleException(command, "A stack cannot contains two identical cards");    
            }else{
                cards.put(newCard.getHashCode(), newCard);
            }
        }

        List<Card> cardList = new ArrayList<Card>(cards.values());
        StackType type = getStackType(cardList, command);

        return new PushAction(cardList, type);        
    }

    private SplitAction parseSplitAction(Player player, String command) throws InvalidInputException, GameRuleException{
        
        //SPLIT <stackID> <cardCode_1> <cardCode_2>

        checker.didPushFirstSequence(player, command);

        int stackID = -1;
        String strCard1 = "";
        String strCard2 = "";

        try {            
            stackID = Integer.parseInt(command.split(" ")[1]); 
            strCard1 = command.split(" ")[2];
            strCard2 = command.split(" ")[3];           
        } catch (Exception e) {
            throw new InvalidInputException(Action.SPLIT_PATTERN , command);
        }

        Card card1 = getCardFromHashCode(strCard1);
        Card card2 = getCardFromHashCode(strCard2);

        checker.doesStackExist(game, command, stackID);
        checker.doesStackContains(game, command, stackID, card1);
        checker.doesStackContains(game, command, stackID, card2);

        if(game.stacks.get(stackID) == StackType.COLOR){
            throw new GameRuleException(command, "A color stack cannot be splited");
        }else if(Math.abs(card1.getNumber() - card2.getNumber()) != 1){
            throw new GameRuleException(command, "The two specified cards should have a consecutive index");
        }else{
            return new SplitAction(stackID, card1, card2);
        }        
    } 
    
    private JoinAction parseJoinAction(Player player, String command) throws InvalidInputException, GameRuleException{

        //JOIN <stackID_1> <stackID_2>

        checker.didPushFirstSequence(player, command);

        int stackID_1 = -1;
        int stackID_2 = -1;

        try {            
            stackID_1 = Integer.parseInt(command.split(" ")[1]);
            stackID_2 = Integer.parseInt(command.split(" ")[2]);
        } catch (Exception e) {
            throw new InvalidInputException(Action.JOIN_PATTERN, command);
        }        

        checker.doesStackExist(game, command, stackID_1);
        checker.doesStackExist(game, command, stackID_2);

        if(stackID_1 == stackID_2){
            throw new GameRuleException(command, "The two specified stacks must be different.");
        }else if(game.stacks.get(stackID_1) == StackType.COLOR || game.stacks.get(stackID_2) == StackType.COLOR){
            throw new GameRuleException(command, "A color stack cannot be merged with another");
        }else{ 
            
            int start1 = game.sequenceStacks.get(stackID_1).getFirstNumber();
            int end1 = game.sequenceStacks.get(stackID_1).getLastNumber();
            int start2 = game.sequenceStacks.get(stackID_2).getFirstNumber();
            int end2 = game.sequenceStacks.get(stackID_2).getLastNumber();

            if(start2 - end1 != 1 && start1 - end2 != 1){
                throw new GameRuleException(command, "The two specified stacks must be consecutive.");
            }else{
                return new JoinAction(stackID_1, stackID_2);
            }
        }        
    } 
    
    private MoveAction parseMoveAction(Player player, String command) throws InvalidInputException, GameRuleException{

        //MOVE <stackID_From> <stackID_To> <cardCodeFrom>

        checker.didPushFirstSequence(player, command);

        int stackFrom = -1;
        int stackTo = -1;
        String strCard = "";

        try {            
            stackFrom = Integer.parseInt(command.split(" ")[1]);
            stackTo = Integer.parseInt(command.split(" ")[2]);
            strCard = command.split(" ")[3];
        } catch (Exception e) {
            throw new InvalidInputException(Action.MOVE_PATTERN, command);
        }

        Card cardToMove = getCardFromHashCode(strCard);

        checker.doesStackExist(game, command, stackFrom);
        checker.doesStackExist(game, command, stackTo);
        checker.doesStackContains(game, command, stackFrom, cardToMove);
        checker.canAddInStack(game, command, stackTo, cardToMove);

        return new MoveAction(stackFrom, stackTo, cardToMove);
    }

    // PARSING SUB-FUNCTIONS

    public Card getCardFromHashCode(String strCard) throws InvalidInputException{

        int cardNumber = -1;
        String strCardColor = null;

        try{            
            cardNumber = Integer.parseInt(strCard.split("_")[0]);
            strCardColor = strCard.split("_")[1];            
        }catch (Exception e) {
            throw new InvalidInputException(String.format("Card %s is not valid", strCard));
        }

        if(cardNumber < 0 || cardNumber > Config.CARDS_MAX_VALUE){
            throw new InvalidInputException("The card number must be bewtween 0 and 12.");
        }else if(!checker.isCardColorValid(strCardColor)){
            throw new InvalidInputException("The card color is not valid");
        }else{
            return new Card(CardColors.valueOf(strCardColor.toUpperCase()), cardNumber);
        }        
    }

    private StackType getStackType(List<Card> cards, String command) throws GameRuleException{

        boolean checkSequence = true;
        boolean checkColor = true;

        for (int i = 0; i < cards.size() - 1; i++) {
            
            Card card_n = cards.get(i);
            Card card_n1 = cards.get(i+1);

            checkSequence = checker.isSequenceStack(card_n, card_n1) && checkSequence;
            checkColor = checker.isColorStack(card_n, card_n1) && checkColor;
        }

        if(checkSequence){
            return StackType.SEQUENCE;
        }else if(checkColor){
            return StackType.COLOR;
        }else{
            throw new GameRuleException(command, "the given cards cannot make a valid stack");
        }
    }

}
