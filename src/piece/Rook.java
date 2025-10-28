package piece;

import main.GamePanel;

public class Rook extends Piece{
    public Rook(int color,int col,int row){
        super(color,col,row,"Haathi");

        pieceID=PieceID.ROOK;

        if(color== GamePanel.WHITE){
            image=getImage("/piece/white-rook.png");
        }else {
            image=getImage("/piece/black-rook.png");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol,targetRow)) {
            boolean addValueSquare = targetCol==preCol && targetRow!=preRow ;
            boolean multiplyValueSquare = targetRow==preRow && targetCol!=preCol ;

            if (addValueSquare  || multiplyValueSquare){
                if( isValidSquare(targetCol,targetRow) && !pieceIsOnStraightLine(targetCol,targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
