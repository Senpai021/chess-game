package chessgame;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public static void main(String[] args) throws IOException {
        LinkedList<Piece> pieces = new LinkedList<>();
        BufferedImage all = ImageIO.read(new File("C:\\Users\\stefa\\IdeaProjects\\PlayableChessBot\\src\\chessgame\\assets\\chess.png"));
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

        JFrame frame = getJFrame(pieces, imgs);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Calculate the correct size including insets
        frame.pack();
        Insets insets = frame.getInsets();
        int width = 512 + insets.left + insets.right + 100; // Extra width for FPS
        int height = 512 + insets.top + insets.bottom;
        frame.setSize(width, height);

        frame.setVisible(true);
    }

    private static @NotNull JFrame getJFrame(LinkedList<Piece> pieces, Image[] imgs) {
        JFrame frame;
        frame = new JFrame("Chess Game");
        JPanel pn = new JPanel() {
            private long lastTime = System.nanoTime();
            private int frames = 0;
            private int fps = 0;

            @Override
            public void paint(Graphics g) {
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
                        g.setColor(white ? Color.WHITE : Color.GRAY.darker());
                        g.fillRect(x * 64, y * 64, 64, 64);
                        white = !white;
                    }
                    white = !white;
                }
                for (Piece p : pieces) {
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
                    g.drawImage(imgs[ind], p.xp * 64, p.yp * 64, this);
                }

                // Draw FPS counter outside the board area
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("FPS: " + fps, 520, 20); // Adjusted position
            }
        };
        frame.add(pn);
        return frame;
    }

    private static void addPieces(int[][] positions, PieceType pieceType, Boolean isWhite, LinkedList<Piece> pieces) {
        for (int[] pos : positions) {
            boolean color = (isWhite != null) ? isWhite : pos[1] > 1; // Determine color based on parameter or row
            new Piece(pos[0], pos[1], pieceType, color, pieces);
        }
    }
}