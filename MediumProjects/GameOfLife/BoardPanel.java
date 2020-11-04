package life;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private int size;

    Board board;

    Generation gen;

    Generation prevGeneration;

    private boolean isStopped;

    public BoardPanel() {
        isStopped = false;
        board = new Board(10, 10, 10);
        size = 19;
    }

    public void clear() {
        Graphics2D g2 = (Graphics2D)  this.getGraphics();;
        g2.setColor(Color.white);
        g2.fillRect(50, 100, 200, 200);
        board = new Board(10, 10, 10);
        this.getParent().repaint();
    }

    public boolean updateAliveLabel(JLabel label) {
        if (gen != null) {
            if (!isStopped)
                label.setText("Alive: " + gen.countAlive);
            return true;
        }
        else
            return false;
    }

    public boolean updateGenerationLabel(JLabel label) {
        if (gen != null) {
            if (!isStopped)
                label.setText("Generation #" + gen.genNum);
            return true;
        }
        else
            return false;

    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.drawRect(50, 100, 200, 200);
        if (!isStopped) {
            gen = board.game();
        } else {
            gen = prevGeneration;
        }
        if (gen != null) {
            for (int i = 0, ioffset = 50; i < 10; i++, ioffset += size) {
                for (int j = 0, joffset = 100; j < 10; j++, joffset += size) {
                    g2.setColor(Color.black);
                    g2.drawRect(i + ioffset, j + joffset, size, size);
                    g2.setColor(Color.white);
                    if (gen.b[i][j] == Board.ALIVE)
                        g2.setColor(Color.black);
                    g2.fillRect(i + ioffset, j + joffset, size, size);
                }
            }
            prevGeneration = gen;
        }
    }

    public void stop() {
        isStopped = true;
    }

    public void play() {
        isStopped = false;
    }
}