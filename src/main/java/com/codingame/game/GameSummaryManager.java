package com.codingame.game;

import com.codingame.game.card.Card;
import com.codingame.game.stack.StackType;
import com.codingame.gameengine.core.GameManager;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class GameSummaryManager {
    
    private List<String> lines;

    @Override
    public String toString(){
        return lines.stream().collect(Collectors.joining("\n"));
    }

    public GameSummaryManager() {
        this.lines = new ArrayList<>();
    }

    public String getSummary(){
        return String.join("\n", this.lines.toArray(new String[0]));
    }

    // SUMMARY HANDLING

    public void clear() {
        this.lines.clear();
    }

    private void add(String format, Object... args){
        lines.add(String.format(format, args));
    }

    public void addBlankLine(){
        lines.add("");
    }

    // GAME SUMMARY

    public void anyCardLeftInCommonDraw(Player player){
        this.add("any cards left in the common draw, player %s doesn't draw", player.getNicknameToken());
    }

    // PLAYING SUMMARY

    public void addLeftActions(Player player){
        if(player.actionsLeft() > 0){
            this.add("%s can still play %s actions", player.getNicknameToken(), player.actionsLeft());
        }        
    }

    public void addWin(Player player){
        this.add("%s wins. He has no remaining cards.", player.getNicknameToken());
    }

    public void drawCard(Player player, int cardsLeft){
        this.add("%s draws one card, cards left in current draw = %s", player.getNicknameToken(), cardsLeft);
    }

    public void pushStack(Player player, StackType stackType){
        this.add("%s pushes a new %s Stack.", player.getNicknameToken(), stackType.toString().toLowerCase());
    }

    public void takeCard(Player player, Card card, int cardStackID){
        this.add("%s takes card %s from stack %s", player.getNicknameToken(), card.getHashCode(), cardStackID);
    }

    public void addCardInStack(Player player, Card card, int cardStackID){
        this.add("%s adds card %s to stack %s.", player.getNicknameToken(), card.getHashCode(), cardStackID);
    }

    public void splitStack(Player player, int stackID, int newStackID){
        this.add("%s splits stack %s and create stack %s", player.getNicknameToken(), stackID, newStackID);
    }

    public void joinStack(Player player, int stackID, int oldStackID){
        this.add("%s merges stack %s into stack %s", player.getNicknameToken(), oldStackID, stackID);
    }

    public void moveCard(Player player, Card card, int stackFrom, int stackTo){
        this.add("%s moves card %s from stack %s to stack %s", player.getNicknameToken(), card.getHashCode(), stackFrom, stackTo);
    }

    public void wait(Player player){
        this.add("%s choose to do nothing", player.getNicknameToken());
    }


    // EXCEPTION SUMMARY

    public void addPlayerTimeout(Player player) {
        this.add(GameManager.formatErrorMessage(String.format("%s has not provided an action in time.", player.getNicknameToken())));
    }

    public void addPlayerDisqualified(Player player) {
        lines.add(String.format("%s was disqualified.",player.getNicknameToken()));
    }    


    public void addPlayerBadCommand(Player player, InvalidInputException invalidInputException) {
        lines.add(GameManager.formatErrorMessage(String.format(
            "%s provided invalid input. Expected '%s'\nGot '%s'",
            player.getNicknameToken(),
            invalidInputException.getExpected(),
            invalidInputException.getGot())));
    }

    public void addPlayerRuleViolation(Player player, GameRuleException gameRuleException) {
        lines.add(GameManager.formatErrorMessage(String.format(
            "%s provided incorrect input. %s",
            player.getNicknameToken(),
            gameRuleException.getErrorMessage())));
    }

}
