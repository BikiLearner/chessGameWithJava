package main;

import piece.Piece;

import java.util.ArrayList;
import java.util.Random;

public class SimpleChessAI {

    // Score a piece (used to score capture targets)
    public static int getChessScore(Piece piece) {
        if (piece == null) return 0;
        return switch (piece.pieceID) {
            case PAWN -> 1;
            case KNIGHT, BISHOP -> 3;
            case ROOK -> 5;
            case QUEEN -> 9;
            case KING -> 1000;
            default -> 0;
        };
    }

    // Make the AI move on GamePanel.simPieces (BLACK)
    public static void makeAiMove() {
        ArrayList<Piece> pieces = GamePanel.simPieces;
        ArrayList<Piece> movables = new ArrayList<>();

        // collect black pieces that can move
        for (Piece p : pieces) {
            if (p.color == GamePanel.BLACK) {
                if (checkCanPieceMove(p)) movables.add(p);
            }
        }

        // nothing to move -> do nothing
        if (movables.isEmpty()) return;

        // For each movable piece, find the best capture (highest-valued target) it can make.
        // Track the best (mover, targetValue, targetPiece, toCol, toRow).
        Piece bestMover = null;
        Piece bestTarget = null;
        int bestScore = Integer.MIN_VALUE;
        int bestToCol = -1, bestToRow = -1;

        for (Piece mover : movables) {
            // try capturing opponent pieces first
            for (Piece target : pieces) {
                if (target == mover) continue;
                if (target.color == mover.color) continue;
                if (mover.canMove(target.col, target.row)) {
                    int score = getChessScore(target);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMover = mover;
                        bestTarget = target;
                        bestToCol = target.col;
                        bestToRow = target.row;
                    }
                }
            }
        }

        // If no captures found, pick a random mover and perform its first legal move.
        if (bestMover == null) {
            Random rnd = new Random();
            // choose a random mover (or could pick highest-valued mover)
            bestMover = movables.get(rnd.nextInt(movables.size()));

            outer:
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (bestMover.canMove(c, r)) {
                        bestToCol = c;
                        bestToRow = r;
                        break outer;
                    }
                }
            }
            // if still no move found (shouldn't happen), return
            if (bestToCol == -1) return;
        }

        // --- perform the move on simPieces ---
        // save previous
        bestMover.preCol = bestMover.col;
        bestMover.preRow = bestMover.row;

        // if capturing, remove captured piece
        if (bestTarget != null) {
            // remove the exact target instance from simPieces
            GamePanel.simPieces.remove(bestTarget);
        } else {
            // If we landed on a square with a piece for some reason, remove it defensively
            for (Piece p : new ArrayList<>(GamePanel.simPieces)) {
                if (p != bestMover && p.col == bestToCol && p.row == bestToRow) {
                    GamePanel.simPieces.remove(p);
                    break;
                }
            }
        }

        // move mover to destination
        bestMover.col = bestToCol;
        bestMover.row = bestToRow;
        bestMover.x = bestMover.getX(bestMover.col);
        bestMover.y = bestMover.getY(bestMover.row);

        // mark as moved if you track that (optional; check your Piece fields)
        // bestMover.hasMoved = true;

        // done — GamePanel.simulate() will copy simPieces -> pieces and change player
    }

    private static boolean checkCanPieceMove(Piece piece) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (piece.canMove(c, r)) return true;
            }
        }
        return false;
    }
}
