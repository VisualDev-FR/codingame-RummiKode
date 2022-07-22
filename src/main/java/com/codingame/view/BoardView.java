package com.codingame.view;

public class BoardView {

    private int rows;
    private int columns;

    private CardView board[][];

    public BoardView(int row, int col){

        this.board = new CardView[row][col];
        this.rows = row;
        this.columns = col;
    }

    public void update(View view){
        updateDraws(view);
        updateStacks(view);
    }

    private void updateDraws(View view){

        for (int i = 0; i < view.getDraws().size() - 1; i++) {
            
            StackView playerDraw = view.getDraws().get(i);

            for(CardView cardView : playerDraw.getCardViews().values()){

                int[] cardCoords = view.getPlayerCoords(i);

                cardView.setCoords(cardCoords[0] - View.CARD_SIZE / 2, cardCoords[1] - View.CARD_SIZE / 2);
            }
        }
    }

    private void updateStacks(View view){

        int stackCount = 0;

        this.board = new CardView[this.rows][this.columns];

        for(StackView stack : view.getStacks().values()){

            CardView lastStackCard = stack.getCardViews().lastEntry().getValue();
            int[] freePosition = getEmptyPosition(stack.size() + 1, stackCount);

            stack.setPosition(freePosition[0], freePosition[1]);

            this.setOccupied(stack);
            this.setBlank(lastStackCard.getRow(), lastStackCard.getCol() + 1);

            stackCount++;
        }        
    }

    private int[] getEmptyPosition(int length, int stackCount){

        int row = stackCount % this.rows;
        int count = 0;

        while(count < 2 * this.rows){

            int nullCount = 0;

            for (int col = 0; col < board[0].length; col++) { 

                if(board[row][col] == null){
                    nullCount++;
                }else{
                    nullCount = 0;
                }

                if(nullCount > length){
                    return new int[]{row, col - length};
                }else{
                    row = row++ % this.rows;
                    count++;
                }
            }
        }

        //assert false : "no empty position available, []";
        return null; 
    }

    private void setOccupied(StackView stackView){

        for(CardView cardView : stackView.getCardViews().values()){
            setOccupied(cardView.getRow(), cardView.getCol(), cardView);
        }
    }

    private void setOccupied(int row, int col, CardView cardView){
        board[row][col] = cardView;
    }    
    
    private void setBlank(int row, int col){
        this.board[row][col] = CardView.getBlank();
    }
        
}
