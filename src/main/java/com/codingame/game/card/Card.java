package com.codingame.game.card;

public class Card{

    protected CardColors color;
    protected int number;
    protected boolean isBonus;

    public Card(CardColors color_, int number_){
        this.color = color_;
        this.number = number_;
        this.isBonus = false;
    }

    public Card(String strCard){
        
        // 08_BLACK | 08_blAck | 8_BLACK | 8_blAcK

        this.color = CardColors.valueOf(strCard.split("_")[1].toUpperCase());
        this.number = Integer.parseInt(strCard.split("_")[0]);
        this.isBonus = false;
    }

    public Card(CardColors cardColor){
        this.color = cardColor;
        this.number = -1;
        this.isBonus = true;
    }

    public void setBonusNumber(int bonusNumber) throws Exception{

        if(this.isBonus()){
            this.number = bonusNumber;
        }else{
            throw new Exception(String.format("Card.setBonus() : The card %s is not a bonusCard", this.getHashCode()));
        }
    }

    public void resetBonus() throws Exception{

        if(this.isBonus()){
            this.number = -1;
        }else{
            throw new Exception(String.format("Card.resetBonus() : The card %s is not a bonusCard", this.getHashCode()));
        }
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
        return String.format("%s_%s.png", this.color.toString().toLowerCase(), this.isBonus() ? "none" : number);
    }

    public String getHashCode(){
        return String.format("%02d_%s", this.number, this.color.toString());
    }

    public String toString(){
        return String.format("%s_%02d", this.color.toString(), this.number);
    }
}
