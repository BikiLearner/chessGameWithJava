package piece;

import main.Board;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;

public class Piece {

    // Your existing fields...
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingPiece;

    @Override
    public String toString() {

        return "Piece{" +
                ", x=" + x +
                ", y=" + y +
                ", col=" + col +
                ", row=" + row +
                ", preCol=" + preCol +
                ", preRow=" + preRow +
                '}';
    }

    // Your constructor
    public Piece(int color, int col, int row) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.y = getY(row);
        this.x = getX(col);
        preCol = col;
        preRow = row;
    }

    /**
     * Loads a PNG image from the classpath safely.
     *
     * @param imagePath the path (without ".png") inside your resources
     * @return BufferedImage or a fallback placeholder if not found.
     */
    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        String fullPath = imagePath.endsWith(".png") ? imagePath : imagePath + ".png";

        try (InputStream is = getClass().getResourceAsStream(fullPath)) {

            if (is == null) {
                System.err.println("⚠️ Image not found: " + fullPath);
                return getPlaceholderImage(64, 64); // return fallback image
            }

            image = ImageIO.read(is);
            if (image == null) {
                System.err.println("❌ Failed to decode image: " + fullPath);
                return getPlaceholderImage(64, 64);
            }

        } catch (IOException e) {
            System.err.println("🚫 Error reading image file: " + fullPath);
            e.printStackTrace();
            image = getPlaceholderImage(64, 64);
        } catch (Exception e) {
            System.err.println("💥 Unexpected error loading image: " + fullPath);
            e.printStackTrace();
            image = getPlaceholderImage(64, 64);
        }

        return image;
    }

    /**
     * Generates a simple placeholder image (gray checkerboard)
     * to show when the real image fails to load.
     */
    private BufferedImage getPlaceholderImage(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        try {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(0, 0, width - 1, height - 1);
            g.drawString("N/A", width / 4, height / 2);
        } finally {
            g.dispose();
        }
        return placeholder;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getX(int col) {
        return col * main.Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * main.Board.SQUARE_SIZE;
    }

    public void updatePiecePosition() {
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);


    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        return (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    public void drawOut(Graphics2D g2) {
        g2.drawImage(image, 900, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece p : GamePanel.simPieces) {
            if (p.col == targetCol && p.row == targetRow && p != this) {
                return p;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingPiece = getHittingP(targetCol, targetRow);
        if (hittingPiece == null) {
            return true;
        } else {
            if (hittingPiece.color != this.color) {
                return true;
            } else {
                hittingPiece = null;
            }
        }
        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        // checking left
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece p : GamePanel.simPieces) {
                if (p.col == c && p.row == targetRow) {
                    hittingPiece = p;
                    return true;
                }
            }
        }

        //checking for right
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece p : GamePanel.simPieces) {
                if (p.col == c && p.row == targetRow) {
                    hittingPiece = p;
                    return true;
                }
            }
        }

        //for up
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece p : GamePanel.simPieces) {
                if (p.row == r && p.col == targetCol) {
                    hittingPiece = p;
                    return true;
                }
            }
        }

        //for down
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece p : GamePanel.simPieces) {
                if (p.row == r && p.col == targetCol) {
                    hittingPiece = p;
                    return true;
                }
            }
        }


        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        if (targetRow < preRow) {
            //up left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }

            // Up right
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }


        }
        if (targetRow > preRow) {
            // Down left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
            // Down right
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
