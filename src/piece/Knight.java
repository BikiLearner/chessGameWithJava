package piece;

import main.GamePanel;

public class Knight extends Piece{
    public Knight(int color,int col,int row){
        super(color,col,row);
        if(color== GamePanel.WHITE){
            image=getImage("/piece/white-knight.png");
        }else {
            image=getImage("/piece/black-knight.png");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
//            int addValueSquare = Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow);
            int multiplyValueSquare = Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow);

            if ( multiplyValueSquare == 2){
                if( isValidSquare(targetCol,targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
