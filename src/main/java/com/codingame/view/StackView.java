package com.codingame.view;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.codingame.game.card.Card;
import com.codingame.game.stack.StackType;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;


public class StackView {

    private TreeMap<String, CardView> cardViews;
    private int stackID;
    private StackType type;

    public StackView(GraphicEntityModule gem, int stackID, StackType type) {
        this.cardViews = new TreeMap<String, CardView>();        
        this.stackID = stackID;
        this.type = type;
    }

    public int getID(){
        return this.stackID;
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
        
        assert false : "card not Found";
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

    public void refreshTooltip(TooltipModule tooltipModule){
        for(CardView cardView : this.cardViews.values()){
            String text = String.format("Card : %s\nStack ID : %s\nStack type : %s\nCards count : %s", cardView.getCard().getHashCode(), this.stackID, this.type.toString(), this.size());
            tooltipModule.setTooltipText(cardView.getSprite(), text);
        }        
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

    public String toString(){
        return this.cardViews.keySet().toString();
    }
}
