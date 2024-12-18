package chessgame;

import java.util.LinkedList;

public class Piece {
    int xp;
    int yp;
    PieceType pieceType;
    boolean isWhite;
    LinkedList<Piece> pieces;

    public Piece(int xp, int yp, PieceType pieceType, boolean isWhite, @org.jetbrains.annotations.NotNull LinkedList<Piece> pieces) {
        this.xp = xp;
        this.yp = yp;
        this.pieceType = pieceType;
        this.isWhite = isWhite;
        this.pieces = pieces;
        pieces.add(this);
    }

    public void move(int xp, int yp) {
        pieces.stream().filter(
                p ->
                        p.xp == xp &&
                        p.yp == yp &&
                        p.isWhite == isWhite
        ).forEachOrdered(
                Piece::takePiece
        );
        this.xp = xp;
        this.yp = yp;
    }

    public void takePiece() {
        pieces.remove(this);
    }
}
