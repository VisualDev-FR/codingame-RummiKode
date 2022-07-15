package com.codingame.game;
import java.util.Properties;

import com.codingame.game.Config.Config;
import com.codingame.game.Summary.GameSummaryManager;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {

    @Inject private GameSummaryManager gameSummaryManager;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private Game game;
    
    private static final int MAX_TURNS = 200;

    private int activePlayerId;

    final boolean PERFORM_UPDATE = false;

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
            //gameManager.setFrameDuration(500);
            //gameManager.setMaxTurns(MAX_TURNS);
            //gameManager.setFirstTurnMaxTime(1000);
            //gameManager.setTurnMaxTime(100);
            
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

        setNextAction(player);
        
/*         try {
            if (player.isActive()) {
                game.performGameUpdate(player);
            }
        } catch (TimeoutException e) {
            commandManager.deactivatePlayer(player, "Timeout!");
            gameSummaryManager.addPlayerTimeout(player);
            gameSummaryManager.addPlayerDisqualified(player);
        } catch (Exception e) {
            commandManager.deactivatePlayer(player, e.getMessage());
            gameSummaryManager.addPlayerTimeout(player);
            gameSummaryManager.addPlayerDisqualified(player);
        } 
        
        setNextPhase(player);
        gameManager.addToGameSummary(gameSummaryManager.getSummary());

        view.refreshCards(game);
        view.refreshApplications(game);
        view.refreshPlayersTooltips(game);

        if (game.isGameOver()) {
            gameOverFrame = true;
        }
        */
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

    private void setNextAction(Player activePlayer){
        if(!activePlayer.canPlay(game)) switchToNextPlayer(activePlayer);
    }

    private void switchToNextPlayer(Player player) {

        if(player.hasToDraw()){
            player.drawCard(game);
            gameSummaryManager.addDrawCard(player);
        }

        // move to next player

        activePlayerId = (activePlayerId + 1) % gameManager.getPlayerCount();
        gameManager.getPlayer(activePlayerId).reset();
        if(game.isLastTurn()) {
            gameOverFrame = true;
        }
    }    
}
