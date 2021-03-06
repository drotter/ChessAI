package dk.dtu.ait.chess.at.chessAi;

import dk.dtu.ait.chess.at.chess.Board;
import dk.dtu.ait.chess.at.chess.Move;
import dk.dtu.ait.chess.at.chessAi.strategy.Strategy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 18.04.13
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class ChessAI {

    private int seconds;
    private Color color;
    private Strategy strategy;
    private long evaluations;
    private Move currentBest;

    private double start;

    public ChessAI(Strategy strategy, int seconds) {
        this.strategy = strategy;
        this.seconds = seconds;
        this.color = Color.BLACK;
    }

    public ChessAI(Strategy strategy) {
        this(strategy, 10);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Move getMove(Board board) {
        start = System.currentTimeMillis();
        Move m = new Move();
        currentBest = null;
        int i = 0;
        while (inTime()) {
            i++;
            this.move(board, i, m);
            if (inTime()) {
                currentBest = m;
            }
        }
        System.out.println("EVALUATIONS: " + evaluations);
        System.out.println("LEVEL: " + i);
        return currentBest;
    }

    public void move(Board board, int searchDepth, Move next) {
        evaluations = 0;
        this.max(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, searchDepth, next);
    }

    private boolean inTime()
    {
        //return true;
       return  (start + seconds * 1000) > (System.currentTimeMillis() - 50);
    }

    private int max(Board board, int alpha, int beta, int searchDepth, int maxSearchDepth, Move next) {
        if (board.isFinished() || searchDepth == maxSearchDepth) {
            evaluations++;
            return strategy.evaluateBoard(board, color);
        }
        Color c = (color == Color.BLACK ? Color.BLACK : Color.WHITE);

        //Adds the best move from previous iteration, to generate a bigger cut-off
        List<Move> childs = new ArrayList<Move>();
        if (searchDepth == 0 && currentBest != null) {
            childs.add(currentBest);
        }
        childs.addAll(board.getAllPossibleMoves(c));
        if(childs.isEmpty())
            return 10000;
        while (alpha < beta && inTime()) {
            if (childs == null || childs.isEmpty()) {
                break;
            }
            if (board.apply(childs.get(0))) {
                int v = min(board, alpha, beta, searchDepth + 1, maxSearchDepth, next);
                board.undo(childs.get(0));

                if (v > alpha) {
                    alpha = v;
                    if (searchDepth == 0) {
                        next.setNewField(childs.get(0).getNewField());
                        next.setOldField(childs.get(0).getOldField());
                        next.setNewFigure(childs.get(0).getNewFigure());
                        next.setOldFigure(childs.get(0).getOldFigure());
                        next.setSpecial(childs.get(0).getSpecial());
                    }
                }
            }
            childs.remove(0);
        }
        return alpha;
    }

    private int min(Board board, int alpha, int beta, int searchDepth, int maxSearchDepth, Move next) {
        if (board.isFinished() || searchDepth == maxSearchDepth) {
            evaluations++;
            return strategy.evaluateBoard(board, color);
        }
        Color c = (color == Color.BLACK ? Color.WHITE : Color.BLACK);
        List<Move> childs = board.getAllPossibleMoves(c);
        if(childs.isEmpty())
            return -10000;
        while (alpha < beta && inTime()) {
            if (childs.isEmpty()) {
                break;
            }
            if (board.apply(childs.get(0))) {
                int v = max(board, alpha, beta, searchDepth + 1, maxSearchDepth, next);
                board.undo(childs.get(0));

                if (v < beta) {
                    beta = v;
                }
            }
            childs.remove(0);
        }
        return beta;
    }
}
