package com.codingame.game;
import java.util.List;
import java.util.Properties;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private Game game;
    
    private static final int MAX_TURNS = 200;

    int maxFrames;
    boolean gameOverFrame;    

    @Override
    public void init() {

        // Set configuration depending on game rules:
        //Config.setDefaultValueByLevel(LeagueRules.fromIndex(gameManager.getLeagueLevel()));

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

/*         for (Player player : gameManager.getActivePlayers()) {
            for (String line : game.getCurrentFrameInfoFor(player)) {
                player.sendInputLine(line);
            }
            
            player.execute();
        }

        for (Player player : gameManager.getActivePlayers()) {
            try {
                List<String> outputs = player.getOutputs();
                // Check validity of the player output and compute the new game state
            } catch (TimeoutException e) {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
            }
        }  */       
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
}
