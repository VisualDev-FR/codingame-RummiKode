package com.codingame.game.card;

public class Card{

    protected CardColors color;
    protected int number;
    protected boolean isBonus;

    public Card(CardColors color_, int number_){
        this.color = color_;
        this.number = number_;
        this.isBonus = number_ == 13;
    }

    public Card(){
        // Constructor created for BonusCard.java inherit        
    }

    public Card clone(){
        return new Card(this.color, this.number);
    }

    public CardColors getColor(){
        return this.color;
    }

    public int getNumber(){
        return this.number;
    }

    public boolean isBonus(){
        return isBonus;
    }

    public String getImage(){
        return String.format("%s_%s.png", this.color.toString().toLowerCase(), number == 13 ? "none" : number);
    }

    public String getHashCode(){
        return String.format("%s_%s", this.number, this.color.toString());
    }

    public String toString(){
        return String.format("%s_%02d", this.color.toString(), this.number);
    }
}
