package uk.ac.cam.ds780.oop.gol;

import javax.swing.*;

import static java.awt.Color.*;

/**
 * Created by serem on 04-Jan-17.
 * This is the Game Panel from GUILife, the one that visually represents the world.
 */
public class GamePanel extends JPanel {
    private World mWorld = null;

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Paint the background white
        g.setColor(WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if(mWorld != null) {
            double cellSize = Double.min((double)this.getWidth() / mWorld.getWidth(), (double)this.getHeight() /
                    mWorld.getHeight());
            g.setColor(LIGHT_GRAY);
            for (int y = 0; y <= mWorld.getHeight(); y++)
                g.drawLine(0,
                        (int)Math.round(cellSize * y),
                        (int)Math.round(mWorld.getWidth()*cellSize),
                        (int)Math.round(cellSize * y));
            for (int x = 0; x <= mWorld.getWidth(); x++)
                g.drawLine((int)Math.round(x * cellSize),
                        0,
                        (int)Math.round(x * cellSize),
                        (int)Math.round(mWorld.getHeight()*cellSize));

            g.setColor(BLACK);
            for(int row = 0; row < mWorld.getHeight(); row++)
                for(int col = 0; col < mWorld.getWidth(); col++)
                    if(mWorld.getCell(col, row))
                        g.fillRect((int)Math.round(col*cellSize),
                                (int)Math.round(row*cellSize),
                                (int)Math.round((col+1)*cellSize-col*cellSize),
                                (int)Math.round((row+1)*cellSize-row*cellSize));

            g.drawString("Generation: " + mWorld.getGenerationCount(), 10, this.getHeight()-20);
        }
    }

    public void display(World w) {
        mWorld = w;
        repaint();
    }
}
