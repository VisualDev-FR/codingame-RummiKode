package com.codingame.game.Summary;

import com.codingame.game.Player;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class GameSummaryManager {
    
    private List<String> lines;

    public GameSummaryManager() {
        this.lines = new ArrayList<>();
    }

    public String getSummary(){
        return String.join("\n", this.lines.toArray(new String[0]));
    }

    public void clear() {
        this.lines.clear();
    }

    private void add(String format, Object... args){
        lines.add(String.format(format, args));
    }

    public void addDrawCard(Player player){
        this.add("player %s draws one card", player.getNicknameToken());
    }


}
