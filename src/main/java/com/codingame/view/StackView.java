package com.codingame.view;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.codingame.game.card.Card;

public class StackView {

    private TreeMap<String, CardView> cardViews;

    public StackView() {
        this.cardViews = new TreeMap<String, CardView>();
    }

    public CardView getCardView(String spriteCode){
        return this.cardViews.get(spriteCode);
    }

    public CardView getCardView(Card card){

        if(this.cardViews.containsKey(String.format("%s %s", card.getHashCode(), 0))){
            return this.cardViews.get(String.format("%s %s", card.getHashCode(), 0));
        }
        else if(this.cardViews.containsKey(String.format("%s %s", card.getHashCode(), 1))){
            return this.cardViews.get(String.format("%s %s", card.getHashCode(), 1));
        }
        
        return null;
    }

    public void addCardView(CardView cardView){
        this.cardViews.put(cardView.getSpriteCode(), cardView);
    }

    public void removeCardView(CardView cardView){
        this.cardViews.remove(cardView.getSpriteCode());
    }

    public void removeCardView(String spriteCode){
        this.cardViews.remove(spriteCode);
    }

    public boolean containsCardView(String spriteCode){
        return this.cardViews.containsKey(spriteCode);
    }

    public TreeMap<String, CardView> getCardViews() {
        return this.cardViews;
    }

    public int size(){
        return this.cardViews.size();
    }
    
    public void setPosition(int startRow, int startCol){

        List<CardView> cards = new ArrayList<>(this.cardViews.values());

        for (int i = 0; i < this.size(); i++) {
            
            CardView cardView = cards.get(i);

            int row = startRow;
            int col = startCol + i;
            
            cardView.setPosition(row, col);
        }    
    }

    public List<int[]> getPositions(){
        
        List<int[]> positions = new ArrayList<int[]>();
        
        for (CardView cardView : this.cardViews.values()) {
            positions.add(new int[]{cardView.getRow(), cardView.getCol()});
        }
        
        return positions;
    }

    public int[] getPosition(){
        return new int[]{this.cardViews.firstEntry().getValue().getRow(), this.cardViews.firstEntry().getValue().getCol()};
    }
}
