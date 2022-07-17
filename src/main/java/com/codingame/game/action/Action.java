package com.codingame.game.action;

/* public enum Action {
    WAIT, RANDOM, TAKE, ADD, PUSH, SPLIT, JOIN, MOVE
}
 */
public abstract class Action {

    public static final String WAIT_PATTERN =  "WAIT";
    public static final String TAKE_PATTERN =  "TAKE <stackID> <cardCode>";
    public static final String ADD_PATTERN =   "ADD <stackID> <cardCode>";
    public static final String PUSH_PATTERN =  "PUSH <cardCode1>, <cardCode2>...";
    public static final String SPLIT_PATTERN = "SPLIT <stackID> <cardCode_1> <cardCode_2>";
    public static final String JOIN_PATTERN =  "JOIN <stackID_1> <stackID_2>";
    public static final String MOVE_PATTERN =  "MOVE <stackID_From> <stackID_To> <cardCodeFrom>";

    public static final Action NO_ACTION = new Action() {};

    public boolean isWait()     { return false; }
    public boolean isTake()     { return false; }
    public boolean isAdd()      { return false; }
    public boolean isPush()     { return false; }
    public boolean isSplit()    { return false; }
    public boolean isJoin()     { return false; }
    public boolean isMove()     { return false; }
}
