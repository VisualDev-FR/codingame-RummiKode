package com.codingame.game.action;

public class JoinAction extends Action {

    private int stackID_1;
    private int stackID_2;

    public JoinAction(int stackID_1, int stackID_2) {
        this.stackID_1 = stackID_1;
        this.stackID_2 = stackID_2;
    }

    public int getStackID_1() {
        return this.stackID_1;
    }

    public int getStackID_2() {
        return this.stackID_2;
    }
    
    @Override
    public boolean isJoin(){
        return true;
    }    
}
