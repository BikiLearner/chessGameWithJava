package piece;

import main.GamePanel;

public class Bishop extends Piece{
    public Bishop(int color,int col,int row){
        super(color,col,row,"Oont");

        pieceID=PieceID.BISHOP;
        if(color== GamePanel.WHITE){
            image=getImage("/piece/white-bishop.png");
        }else {
            image=getImage("/piece/black-bishop.png");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            int cols = Math.abs(targetCol-preCol);
            int rows = Math.abs(targetRow-preRow);


            if (cols==rows  ){
                if( isValidSquare(targetCol,targetRow) && !pieceIsOnDiagonalLine(targetCol,targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
