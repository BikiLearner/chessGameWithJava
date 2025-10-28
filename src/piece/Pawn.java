package piece;

import main.GamePanel;

public class Pawn extends Piece {
    public Pawn(int color, int col, int row) {
        super(color, col, row,"Pyaada");
        pieceID=PieceID.PAWN;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-pawn.png");
        } else {
            image = getImage("/piece/black-pawn.png");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            /// checking it color and setting it direction
            int moveValue;
            if (color == GamePanel.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }

            hittingPiece = getHittingP(targetCol, targetRow);

            if (targetCol == preCol && targetRow == preRow + moveValue && hittingPiece == null) {
                return true;
            }

            if (targetCol == preCol && targetRow == preRow + (moveValue * 2)
                    && hittingPiece == null && !isMoved && !pieceIsOnStraightLine(targetCol, targetRow)) {
                return true;

            }

            // capturing pieces

            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingPiece != null && hittingPiece.color != color) {
                return true;
            }


            // en passant


            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==targetCol && piece.row==preRow && piece.twoStepped){
                        hittingPiece=piece;
                        return true;
                    }
                }
            }



        }
        return false;
    }
}
