package main;

import asset.Colors;
import piece.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static main.Board.SQUARE_SIZE;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    public int FPS = 60;


    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    public static ArrayList<Piece> promotionPiece = new ArrayList<>();

    Piece activePiece, checkingP;
    public static Piece castlingPiece;


    //colors part of the chess
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;


    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //boolean
    boolean canMove;
    boolean isValidSquare;
    boolean promotion, gameOver,staleMate;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Colors.DARK_GRAY);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
//        setPiece();
//        testPromotion()
        testIllegal();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void checkCastling() {
        if (castlingPiece != null) {
            if (castlingPiece.col == 0) {
                castlingPiece.col += 3;
            } else if (castlingPiece.col == 7) {
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }

    public void changePlayer() {

        int opponentColor = (currentColor == WHITE) ? BLACK : WHITE;

        for (Piece p : pieces) {
            if (p.color == opponentColor) {
                p.twoStepped = false;
            }
        }

        currentColor = opponentColor;
        activePiece = null;
    }

    public void setPiece() {
        //White
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(WHITE, i, 6));
        }
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 4));
        pieces.add(new King(WHITE, 4, 7));

        //Black
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(BLACK, i, 1));
        }
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));

    }

    public void testPromotion() {
        pieces.add(new Pawn(WHITE, 5, 2));
        pieces.add(new Pawn(BLACK, 3, 6));
    }

    public void testIllegal() {
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new King(WHITE, 3, 7));
        pieces.add(new King(BLACK, 0, 3));
        pieces.add(new Bishop(BLACK, 1, 4));
        pieces.add(new Queen(BLACK, 4, 5));
    }

    public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    public boolean canPromote() {
        if (activePiece.pieceID == PieceID.PAWN) {
            if (currentColor == WHITE && activePiece.row == 0 || currentColor == BLACK && activePiece.row == 7) {
                promotionPiece.clear();
                promotionPiece.add(new Rook(currentColor, 9, 2));
                promotionPiece.add(new Knight(currentColor, 9, 3));
                promotionPiece.add(new Bishop(currentColor, 9, 4));
                promotionPiece.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void update() {

        if (promotion) {
            promoting();

        } else if(!gameOver && !staleMate) {
            if (mouse.pressed) {
                if (activePiece == null) {
                    int mouseColX = mouse.x / SQUARE_SIZE;
                    int mouseColY = mouse.y / SQUARE_SIZE;
//                System.out.println("Mouse Column X "+ mouseColX + "  Mouse Column Y " + mouseColY);
                    for (Piece p : simPieces) {
//                    if(p.color==currentColor){
//                        System.out.println(p);
//                    }

                        if (p.color == currentColor && p.col == mouseColX && p.row == mouseColY) {
//                        System.out.println(activePiece.toString());
                            activePiece = p;
                        }
                    }
                } else {
                    simulate();
                }
            }
            if (!mouse.pressed) {
                if (activePiece != null) {
                    if (isValidSquare) {


                        copyPieces(simPieces, pieces);
                        activePiece.updatePiecePosition();
                        //activePiece = null;
                        if (castlingPiece != null) {
                            castlingPiece.updatePiecePosition();
                        }

                        if (isKingInCheck() && isCheckMate()) {
                            gameOver=true;
                        } else if (isStaleMate() && !isKingInCheck()) {
                            staleMate=true;
                        } else{
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }


                    } else {
                        copyPieces(pieces, simPieces);
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }

    }

    private boolean isKingInCheck() {
        Piece king = getKing(true);
        if (activePiece.canMove(king.col, king.row)) {
            checkingP = activePiece;
            return true;
        } else {
            checkingP = null;
        }
        return false;
    }

    private boolean isCheckMate() {
        Piece king = getKing(true);
        if (kingCanMove(king)) {
            return false;
        } else {
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);


            if (colDiff == 0) {
                // checking piece attack vertically

                if (checkingP.row < king.row) {
                    // above the king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    //bellow the king

                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                // checking piece attack horizontally
                if (checkingP.col < king.col) {
                    // left the king
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) {
                    //right the king

                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                // diagonally

                if (checkingP.row < king.row) {
                    // above the king
                    if (checkingP.col < king.col) {
                        // left the king
                        for (int col = checkingP.col,row=checkingP.row; col < king.col; col++,row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        //right the king

                        for (int col = checkingP.col,row=checkingP.row; col > king.col; col--,row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                }
                if (checkingP.row > king.row) {
                    //bellow the king

                    if (checkingP.col < king.col) {
                        // left the king
                        for (int col = checkingP.col,row=checkingP.row; col < king.col; col++,row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        //right the king

                        for (int col = checkingP.col,row=checkingP.row; col > king.col; col--,row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                }

            } else {
                //checking piece is Horse
            }


        }
        return true;
    }

    private boolean isStaleMate(){
        int count =0;
        for(Piece piece:simPieces){
            if(piece.color!=currentColor){
                count++;
            }
        }
        if(count==1){
            if(!kingCanMove(getKing(true))){
                return true;
            }
        }

        return false;
    }

    private boolean kingCanMove(Piece king) {
        // if king can move to and square
        if (isValidMove(king, -1, -1)) return true;
        if (isValidMove(king, 0, -1)) return true;
        if (isValidMove(king, 1, -1)) return true;
        if (isValidMove(king, -1, 0)) return true;
        if (isValidMove(king, 1, 0)) return true;
        if (isValidMove(king, -1, 1)) return true;
        if (isValidMove(king, 0, 1)) return true;
        if (isValidMove(king, 1, 1)) return true;

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
        boolean isValidMove = false;
        king.col += colPlus;
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) {
            if (king.hittingPiece != null) {
                simPieces.remove(king.hittingPiece);
            }
            if (!isIllegal(king)) {
                isValidMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }


    private Piece getKing(boolean opponent) {
        Piece king = null;
        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.pieceID == PieceID.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.pieceID == PieceID.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isIllegal(Piece king) {
        if (king.pieceID == PieceID.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false);
        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promotionPiece) {
                if (piece.col == (mouse.x / SQUARE_SIZE) && piece.row == (mouse.y / SQUARE_SIZE)) {
                    piece.col = activePiece.col;
                    piece.row = activePiece.row;
                    piece.preCol = activePiece.preCol;
                    piece.preRow = activePiece.preRow;
                    piece.x = activePiece.x;
                    piece.y = activePiece.y;
                    simPieces.add(piece);
                    simPieces.remove(activePiece);
                    copyPieces(simPieces, pieces);
                    activePiece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    public void simulate() {
        canMove = false;
        isValidSquare = false;
        copyPieces(pieces, simPieces);


        //reset the castling
        if (castlingPiece != null) {
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }


        //piece position is hold update it position
        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        // check can move
        if (activePiece.canMove(activePiece.col, activePiece.row)) {
            canMove = true;

            if (activePiece.hittingPiece != null) {
//                removedPieces.add(activePiece.hittingPiece);
                simPieces.remove(activePiece.hittingPiece);
            }
            checkCastling();
            if (!isIllegal(activePiece) && !opponentCanCaptureKing()) {
                isValidSquare = true;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        board.draw(g2);

        //pieces
        for (Piece p : simPieces) {
            System.out.println("draw called ");
            p.draw(g2);
        }

        //draw remove piece
//        for (Piece p : removedPieces) {
//            p.drawOut(g2);
//        }
        if (activePiece != null) {
            if (canMove) {
                if (isIllegal(activePiece) || opponentCanCaptureKing()) {
                    g2.setColor(Color.RED);

                } else {
                    g2.setColor(Color.white);
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));
                g2.fillRect(activePiece.col * SQUARE_SIZE, activePiece.row * SQUARE_SIZE,
                        SQUARE_SIZE, SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1F));
            }
            activePiece.draw(g2);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //promotion piece
        if (promotion) {

            g2.setFont(new Font("Book Antiqua", Font.BOLD, 30));
            g2.setColor(Colors.SAND);
            g2.drawString("Promote Choose", 840, 150);

            for (int row = 2; row <= 5; row++) {
                g2.setColor(Colors.SAND);
                g2.fillRect(9 * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
            for (Piece piece : promotionPiece) {
                piece.draw(g2);
            }
        } else {
            //StaTus Message
            g2.setFont(new Font("Book Antiqua", Font.BOLD, 40));
            if (checkingP != null) {
                Piece checkKingPiece = new King(checkingP.color, 9, 3);
                checkKingPiece.draw(g2);
                g2.setColor(currentColor != WHITE ? Colors.WHITE : Colors.BLACK);
                g2.drawString("King is ", 900, 450);
                g2.drawString("in check", 900, 500);
            } else {
                g2.setColor(currentColor == WHITE ? Colors.WHITE : Colors.BLACK);
                g2.drawString((currentColor == WHITE ? "White's Turn" : "Black's Turn"), 840, currentColor == WHITE ? 700 : 100);
            }
        }

        if(gameOver){
            String s=(currentColor==WHITE)?"White Wins":"Black Wins";
            g2.setFont(new Font("Arial",Font.PLAIN,90));
            g2.setColor(Colors.MIDNIGHT_BLUE);
            g2.drawString(s,200,420);
        }
        if(staleMate){
            String s="StaleMate (Draw)";
            g2.setFont(new Font("Arial",Font.PLAIN,90));
            g2.setColor(Colors.RED);
            g2.drawString(s,200,420);
        }


    }

    @Override
    public void run() {

        double drawIntervals = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawIntervals;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }


    }
}
