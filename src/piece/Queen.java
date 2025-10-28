package piece;

import main.GamePanel;

public class Queen extends Piece {
    public Queen(int color, int col, int row) {
        super(color, col, row,"Rani");

        pieceID=PieceID.QUEEN;
        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white-queen.png");
        } else {
            image = getImage("/piece/black-queen.png");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
//
            int cols = Math.abs(targetCol - preCol);
            int rows = Math.abs(targetRow - preRow);

            boolean addValueSquare = targetCol == preCol && targetRow != preRow;
            boolean multiplyValueSquare = targetRow == preRow && targetCol != preCol;
            if (cols == rows || addValueSquare || multiplyValueSquare) {
                if (isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
