package com.codingame.game.card;

public class Card{

    public CardColors color;
    public int number;

    public Card(CardColors color_, int number_){
        this.color = color_;
        this.number = number_;
    }

    public Card clone(){
        return new Card(this.color, this.number);
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
