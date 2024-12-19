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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class ChessGame {
    private static final int[][] KING_POSITIONS = {{4, 7}, {4, 0}};
    private static final int[][] QUEEN_POSITIONS = {{3, 7}, {3, 0}};
    private static final int[][] ROOK_POSITIONS = {{0, 7}, {7, 7}, {0, 0}, {7, 0}};
    private static final int[][] BISHOP_POSITIONS = {{2, 7}, {5, 7}, {2, 0}, {5, 0}};
    private static final int[][] KNIGHT_POSITIONS = {{1, 7}, {6, 7}, {1, 0}, {6, 0}};
    private static final int[][] PAWN_POSITIONS_WHITE = {{0, 6}, {1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6}, {6, 6}, {7, 6}};
    private static final int[][] PAWN_POSITIONS_BLACK = {{0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1}};
    public static LinkedList<Piece> pieces = new LinkedList<>();
    public static Piece selectedPiece;
    public static JFrame frame;
    private static int offsetx, offsety;

    public static void main(String[] args) throws IOException {
        BufferedImage all = ImageIO.read(new File("src/chessgame/assets/chess.png"));
        Image[] imgs = new Image[12];
        int ind = 0;
        for (int y = 0; y < 400; y += 200) {
            for (int x = 0; x < 1200; x += 200) {
                imgs[ind] = all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                ind++;
            }
        }

        // Create pieces based on initial setup
        addPieces(KING_POSITIONS, PieceType.KING, null, pieces);
        addPieces(QUEEN_POSITIONS, PieceType.QUEEN, null, pieces);
        addPieces(ROOK_POSITIONS, PieceType.ROOK, null, pieces);
        addPieces(BISHOP_POSITIONS, PieceType.BISHOP, null, pieces);
        addPieces(KNIGHT_POSITIONS, PieceType.KNIGHT, null, pieces);
        addPieces(PAWN_POSITIONS_WHITE, PieceType.PAWN, true, pieces);
        addPieces(PAWN_POSITIONS_BLACK, PieceType.PAWN, false, pieces);

        frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel pn = new JPanel() {
            private long lastTime = System.nanoTime();
            private int frames = 0;
            private int fps = 0;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g); // Call the superclass method to ensure proper painting
                long currentTime = System.nanoTime();
                frames++;
                if (currentTime - lastTime >= 1_000_000_000) {
                    fps = frames;
                    frames = 0;
                    lastTime = currentTime;
                }

                boolean white = true;
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        g.setColor(white ? new Color(235, 235, 208) : new Color(119, 148, 85));
                        g.fillRect(x * 64, y * 64, 64, 64);
                        white = !white;
                    }
                    white = !white;
                }

                // Draw all pieces except the selected one
                for (Piece p : pieces) {
                    if (p != selectedPiece) {
                        int ind = switch (p.pieceType) {
                            case KING -> 0;
                            case QUEEN -> 1;
                            case BISHOP -> 2;
                            case KNIGHT -> 3;
                            case ROOK -> 4;
                            case PAWN -> 5;
                        };
                        if (!p.isWhite) {
                            ind += 6;
                        }
                        g.drawImage(imgs[ind], p.x, p.y, this);
                    }
                }

                // Draw the selected piece on top
                if (selectedPiece != null) {
                    int ind = switch (selectedPiece.pieceType) {
                        case KING -> 0;
                        case QUEEN -> 1;
                        case BISHOP -> 2;
                        case KNIGHT -> 3;
                        case ROOK -> 4;
                        case PAWN -> 5;
                    };
                    if (!selectedPiece.isWhite) {
                        ind += 6;
                    }
                    g.drawImage(imgs[ind], selectedPiece.x, selectedPiece.y, this);
                }

                // Draw FPS counter outside the board area
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("FPS: " + fps, 520, 20); // Adjusted position

                frame.repaint();
            }
        };

        frame.add(pn);
        frame.setBounds(10, 10, 612, 612); // Set the size of the frame
        frame.setVisible(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedPiece = getPiece(e.getX(), e.getY());
                if (selectedPiece != null) {
                    offsetx = e.getX() - selectedPiece.x;
                    offsety = e.getY() - selectedPiece.y;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedPiece != null) {
                    Insets insets = frame.getInsets();
                    int mouseX = e.getX() - insets.left;
                    int mouseY = e.getY() - insets.top;

                    // Calculate the target square based on the mouse release position
                    int targetXp = mouseX / 64;
                    int targetYp = mouseY / 64;

                    // Ensure the target position is within bounds
                    if (targetXp < 0) targetXp = 0;
                    if (targetXp > 7) targetXp = 7;
                    if (targetYp < 0) targetYp = 0;
                    if (targetYp > 7) targetYp = 7;

                    // Move the piece to the target position
                    selectedPiece.move(targetXp, targetYp);

                    // Snap the piece to the center of the square
                    selectedPiece.x = targetXp * 64;
                    selectedPiece.y = targetYp * 64;

                    // Print the coordinates of the piece
                    System.out.println("Piece released at: " + targetXp + ":" + targetYp);

                    frame.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null) {
                    selectedPiece.x = e.getX() - offsetx; //offset to ensure the piece is on the cursor
                    selectedPiece.y = e.getY() - offsety; //offset to ensure the piece is on the cursor
                    frame.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    private static void addPieces(int[][] positions, PieceType pieceType, Boolean isWhite, LinkedList<Piece> pieces) {
        for (int[] pos : positions) {
            boolean color = (isWhite != null) ? isWhite : pos[1] > 1; // Determine color based on parameter or row
            new Piece(pos[0], pos[1], pieceType, color, pieces);
        }
    }

    public static Piece getPiece(int x, int y) {
        Insets insets = frame.getInsets();
        int xp = (x - insets.left) / 64;
        int yp = (y - insets.top) / 64;
        for (Piece p : pieces) {
            if (p.xp == xp && p.yp == yp) {
                return p;
            }
        }
        return null; // Return null if no piece is found
    }
}