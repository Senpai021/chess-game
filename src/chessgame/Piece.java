/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org>
 */

package chessgame;

import java.util.LinkedList;
import java.util.List;

public class Piece {
    int xp, yp;
    int x, y;
    PieceType pieceType;
    boolean isWhite;
    LinkedList<Piece> pieces;

    public Piece(int xp, int yp, PieceType pieceType, boolean isWhite, LinkedList<Piece> pieces) {
        this.xp = xp;
        this.yp = yp;
        x = xp * 64;
        y = yp * 64;
        this.pieceType = pieceType;
        this.isWhite = isWhite;
        this.pieces = pieces;
        pieces.add(this);
    }

    @Override
    public String toString() {
        return "Piece{" +
                "xp=" + xp +
                ", yp=" + yp +
                ", x=" + x +
                ", y=" + y +
                ", pieceType=" + pieceType +
                ", isWhite=" + isWhite +
                '}';
    }

    public void move(int xp, int yp) {
        // Check if the move is within the board boundaries
        if (xp < 0 || xp >= 8 || yp < 0 || yp >= 8) {
            // Invalid move, return to original position
            return;
        }

        // Check if there is a piece of the same color at the target position
        boolean sameColorPieceExists = pieces.stream()
                .anyMatch(p -> p.xp == xp && p.yp == yp && p.isWhite == this.isWhite);

        if (sameColorPieceExists) {
            // Invalid move, return to original position
            return;
        }

        // Collect pieces to be removed (opposite color)
        List<Piece> piecesToRemove = pieces.stream()
                .filter(p -> p.xp == xp && p.yp == yp && p.isWhite != this.isWhite)
                .toList();

        // Remove the collected pieces
        piecesToRemove.forEach(Piece::takePiece);

        // Update the position of the current piece
        this.xp = xp;
        this.yp = yp;
        x = xp * 64;
        y = yp * 64;
    }

    public void takePiece() {
        pieces.remove(this);
    }
}
