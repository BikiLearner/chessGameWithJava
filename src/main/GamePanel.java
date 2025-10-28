package main;

import asset.Colors;
import piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    public int FPS = 60;


    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    public static ArrayList<Piece> removedPieces = new ArrayList<>();

    Piece activePiece;
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

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Colors.CHARCOAL);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        setPiece();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void checkCastling(){
        if(castlingPiece!=null) {
            if(castlingPiece.col ==0){
                castlingPiece.col+=3;
            }else if(castlingPiece.col==7){
                castlingPiece.col-=2;
            }
            castlingPiece.x= castlingPiece.getX(castlingPiece.col);
        }
    }

    public void changePlayer(){
        currentColor=(currentColor==WHITE)?BLACK:WHITE;
        activePiece=null;
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

    public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    private void update() {
        if (mouse.pressed) {
            if (activePiece == null) {
                int mouseColX = mouse.x / Board.SQUARE_SIZE;
                int mouseColY = mouse.y / Board.SQUARE_SIZE;
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
                if(isValidSquare){


                    copyPieces(simPieces,pieces);
                    activePiece.updatePiecePosition();
                    //activePiece = null;
                    if(castlingPiece!=null){
                        castlingPiece.updatePiecePosition();
                    }

                    changePlayer();
                }else {
                    copyPieces(pieces,simPieces);
                    activePiece.resetPosition();
                    activePiece = null;
                }



            }
        }

    }

    public void simulate() {
        canMove=false;
        isValidSquare=false;
        copyPieces(pieces,simPieces);


        //reset the castling
        if(castlingPiece!=null){
            castlingPiece.col=castlingPiece.preCol;
            castlingPiece.x= castlingPiece.getX(castlingPiece.col);
            castlingPiece=null;
        }


        //piece position is hold update it position
        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        // check can move
        if(activePiece.canMove(activePiece.col,activePiece.row)){
            canMove=true;

            if(activePiece.hittingPiece!=null){
//                removedPieces.add(activePiece.hittingPiece);
                simPieces.remove(activePiece.hittingPiece);
            }
            checkCastling();
            isValidSquare=true;
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
        for (Piece p : removedPieces) {
            p.drawOut(g2);
        }
        if (activePiece != null) {
            if(canMove){
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));
                g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1F));
            }
            activePiece.draw(g2);
        }

        //StaTus Message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua",Font.BOLD,40));
        g2.setColor(Colors.MAGENTA);

        g2.drawString((currentColor == WHITE ? "White's Turn" : "Black's Turn"), 840, 500);

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
