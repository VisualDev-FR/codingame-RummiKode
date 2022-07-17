package com.codingame.game.ExeptionManager;

//@SuppressWarnings("serial")
public class GameRuleException extends Exception {
    private final String errorMessage;
    private final String command;

    public GameRuleException(String command, String errorMessage) {
        super("Invalid Input: Got '" + command + "'\nbut " + errorMessage);
        this.errorMessage = errorMessage;
        this.command = command;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCommand() { return command; }

}
