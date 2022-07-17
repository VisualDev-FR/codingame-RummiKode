package com.codingame.game.ExeptionManager;

import com.codingame.game.Game;
import com.codingame.game.Player;
import com.codingame.game.card.Card;
import com.codingame.game.card.CardColors;
import com.codingame.game.stack.StackType;

public class InputChecker {

    // ACTION CHECKS

    public boolean isWaitAction(String strAction){
        return strAction.startsWith("WAIT");
    }

    public boolean isTakeAction(String strAction){
        return strAction.startsWith("TAKE");
    }

    public boolean isAddAction(String strAction){
        return strAction.startsWith("ADD");
    }

    public boolean isPushAction(String strAction){
        return strAction.startsWith("PUSH");
    }

    public boolean isSplitAction(String strAction){
        return strAction.startsWith("SPLIT");
    }

    public boolean isJoinAction(String strAction){
        return strAction.startsWith("JOIN");
    }

    public boolean isMoveAction(String strAction){
        return strAction.startsWith("MOVE");
    }

    // CARD CHECKS

    public boolean isCardColorValid(String strCardColor){

        if(strCardColor.toUpperCase().equals(CardColors.BLACK.toString().toUpperCase())){
            return true;
        }else if(strCardColor.toUpperCase().equals(CardColors.BLUE.toString().toUpperCase())){
            return true;
        }else if(strCardColor.toUpperCase().equals(CardColors.GREEN.toString().toUpperCase())){
            return true;
        }else if(strCardColor.toUpperCase().equals(CardColors.YELLOW.toString().toUpperCase())){
            return true;
        }else{
            return false;
        }        
    }
    
    public boolean isSequenceStack(Card card1, Card card2){
        return card1.getColor() == card2.getColor() && card1.getNumber() == card2.getNumber() - 1;
    }

    public boolean isColorStack(Card card1, Card card2){
        return card1.getColor() != card2.getColor() && card1.getNumber() == card2.getNumber();
    }

    // EXEPTION THROWER

    public void canAddInStack(Game game, String command, int stackID, Card card) throws GameRuleException{
        
        boolean canAdd = false;

        if(game.stacks.get(stackID) == StackType.SEQUENCE){
            canAdd = game.sequenceStacks.get(stackID).canAdd(card);
        }else{
            canAdd = game.colorStacks.get(stackID).canAdd(card);
        }        

        if(!canAdd){
            throw new GameRuleException(command, String.format("The card %s cannot be added to the stack %s.", card.getHashCode(), stackID));
        }
    }

    public void didPushFirstSequence(Player player, String command) throws GameRuleException{
        if(!player.hasPushedFirstSequence()){
            throw new GameRuleException(command, String.format("Player %s cannot %s, because he did not push a stack yet.", player.getNicknameToken(), command.split(" ")[0]));
        }
    }

    public void doesStackExist(Game game, String command, int stackID) throws GameRuleException{
        if(!game.stacks.containsKey(stackID)){
            throw new GameRuleException(command, String.format("The stack %s doesn't exist.", stackID));
        }       
    }

    public void doesStackContains(Game game, String command, int stackID, Card card) throws GameRuleException{
        
        if(game.stacks.get(stackID) == StackType.SEQUENCE){

            if(!game.sequenceStacks.get(stackID).containsCard(card)){
                throw new GameRuleException(command, String.format("The stack %s doesn't contains the card %s.", stackID, card.getHashCode()));
            }

        }else if(game.stacks.get(stackID) == StackType.COLOR){

            if(!game.colorStacks.get(stackID).containsCard(card)){
                throw new GameRuleException(command, String.format("The stack %s doesn't contains the card %s.", stackID, card.getHashCode()));
            }
        }
    }

    public void doesHaveThisCard(Player player, String command, Card card) throws GameRuleException{
        if(!player.getCards().containsKey(card.getHashCode())){
            throw new GameRuleException(command, String.format("Player %s doesn't have a card %s in hand.", player.getNicknameToken(), card.getHashCode()));
        }
    }
}
