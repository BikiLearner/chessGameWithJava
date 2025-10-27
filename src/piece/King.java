package piece;

import main.GamePanel;

public class King extends Piece {
    public King(int color, int col, int row) {
        super(color, col, row);
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
        }
        return false;
    }
}
