package piece;

import main.GamePanel;

public class King extends Piece {
    public King(int color, int col, int row) {
        super(color, col, row,"Raja");
        pieceID=PieceID.KING;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-king.png");
        } else {
            image = getImage("/piece/black-king.png");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            int addValueSquare = Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow);
            int multiplyValueSquare = Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow);

            if (addValueSquare == 1 || multiplyValueSquare == 1){
                if( isValidSquare(targetCol,targetRow)){
                    return true;
                }
            }

            //castling
            if(!isMoved){
                //right
                if(targetCol==preCol+2 && targetRow==preRow && !pieceIsOnStraightLine(targetCol,targetRow)){
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.col == preCol + 3 && piece.row == preRow && !piece.isMoved) {
                            GamePanel.castlingPiece = piece;
                            return true; // stop once we find it
                        }
                    }

                }

                //left part
                if(targetCol==preCol-2 && targetRow==preRow && !pieceIsOnStraightLine(targetCol,targetRow)){
                    Piece[] p=new Piece[2];
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.col == preCol - 3 && piece.row == targetRow) {
                            p[0]=piece;
                        }

                        if (piece.col == preCol - 4 && piece.row == targetRow) {
                            p[1]=piece;
                        }

                        if(p[0]==null && p[1]!=null && !p[1].isMoved){
                            GamePanel.castlingPiece = piece;
                            return true;
                        }
                    }
                }


            }
        }
        return false;
    }
}
