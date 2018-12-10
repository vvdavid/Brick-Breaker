package main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JOptionPane;

public class Gameplay extends javax.swing.JPanel implements Runnable {

    private final int width = 900;
    private int height = 600;

    private int barX, barY, barWidth = 120, barHeight = 20;
    private Rectangle bar;

    private int ballX, ballY, ballDiameter = 30;
    private Rectangle ballRectangle;
    private int ballXDir = 0, ballYDir = 0;

    private final Block[][] blocks = new Block[3][8];

    private boolean play = true;
    private int score = 0;

    public Gameplay() {
        //init
        initComponents();
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        addListeners();
        setBlankCursor();
        //locate bar above bottom border
        barY = getHeight() - 100;
        barX = (width / 2) - (barWidth / 2);
        bar = new Rectangle(barX, barY, barWidth, barHeight);
        //locate ball on bar
        ballX = (width / 2) - (ballDiameter / 2);
        ballY = barY - ballDiameter;
        //initialize ball rectangle
        ballRectangle = new Rectangle(ballX, ballY, ballDiameter, ballDiameter);
        //initialize all blocks
        initializeBlocks();
        //start moving ball
        Thread moveBall = new Thread(this);
        moveBall.start();
    }

    @Override
    public void run() {
        while (play) {
            //move ball to next position
            ballX += ballXDir;
            ballY += ballYDir;
            //update bar and ball rectangle
            bar.setBounds(barX, barY, barWidth, barHeight);
            ballRectangle.setBounds(ballX, ballY, ballDiameter, ballDiameter);
            //check for all posible collisions 
            checkBorders();
            checkCollisions();
            //win... or not
            checkWin();
            //repaint:v
            repaint();
            //wait one monitor refresh
            sleep(1000 / /*Hertz*/ 60);
        }
        JOptionPane.showMessageDialog(this, "Juego terminado\nPuntuación: " + score, "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private boolean checkWin() {
        for (Block[] row : blocks) {
            for (Block block : row) {
                if (block.enabled) {
                    return false;
                }
            }
        }
        play = false;
        return true;
    }

    private void checkCollisions() {
        //check bar collision
        if (bar.intersects(ballRectangle)) {
//            checkCollision(bar);
            if (collidesInY(ballRectangle, bar)) {
                ballYDir = -ballYDir;
            } else {
                ballXDir = -ballXDir;
            }
            ballXDir = ThreadLocalRandom.current().nextInt(-5, 6);
            ballYDir = -ThreadLocalRandom.current().nextInt(5, 6);
            return;
        }
        //check for collision on all blocks
        for (Block[] row : blocks) {
            for (Block block : row) {
                checkCollision(block);
            }
        }
    }

    private void checkCollision(Block block) {
        Rectangle actualRectangle = block.rectangle;
        //first check if the block is enabled and intersects the ball
        if (block.enabled && actualRectangle.intersects(ballRectangle)) {
            //then, check where it was hit to know wich ball direction to invert
            Point ballUpLeft = new Point(ballRectangle.x, ballRectangle.y);
            Point ballUpRight = new Point(ballRectangle.x + ballDiameter, ballRectangle.y);
            Point ballDownLeft = new Point(ballRectangle.x, ballRectangle.y + ballDiameter);
            Point ballDownRight = new Point(ballRectangle.x + ballDiameter, ballRectangle.y + ballDiameter);
            //check sides, since they're more likely to be hit
//            System.out.println("actualRectangle = " + actualRectangle);
//            System.out.println("ballUpLeft = " + ballUpLeft);
//            System.out.println("ballUpRight = " + ballUpRight);
//            System.out.println("");
            if (actualRectangle.contains(ballUpLeft) && actualRectangle.contains(ballDownLeft)) {
                ballXDir = -ballXDir;
            } else if (actualRectangle.contains(ballUpRight) && actualRectangle.contains(ballDownRight)) {
                ballXDir = -ballXDir;
            } else if (actualRectangle.contains(ballUpLeft) && actualRectangle.contains(ballUpRight)) {
                ballYDir = -ballYDir;
            } else if (actualRectangle.contains(ballDownLeft) && actualRectangle.contains(ballDownRight)) {
                ballYDir = -ballYDir;
            } //portion of the ball was not contained by block, now check only corners

            //finally, since the block was hit, disable it and increment score (TODO)
            block.enabled = false;
            score += 50;
        }
    }

    static public boolean collidesInY(Rectangle small, Rectangle big) {
        Point smallPoint = small.getLocation();
        Point bigPoint = big.getLocation();

        if (smallPoint.getY() <= big.getY()
                || bigPoint.getY() + big.height <= smallPoint.getY()) {
            return true;
        } else {
            return false;
        }
    }

    private void checkBorders() {
        //if ball is beyond left border, don't need on right
        if (ballX < 0) {
            ballX = 0;
            ballXDir = -ballXDir;
            //if it wasn't on left, check right
        } else if (ballX + ballDiameter >= width) {
            ballX = width - ballDiameter - 1;
            ballXDir = -ballXDir;
        }
        //ball beyond upper border
        if (ballY < 0) {
            ballY = 0;
            ballYDir = -ballYDir;

            //ball beyond bottom border
        } else if (ballY >= height) {
            play = false;
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);

        paintBackground(g);
        paintScore(g);
        paintBar(g);
        paintBall(g);
        paintBlocks(g);
    }

    private void paintBlocks(Graphics g) {
        for (Block[] row : blocks) {
            for (Block block : row) {
                if (block.enabled) {
                    g.fillRect(block.x, block.y, block.width, block.heigth);
                }
            }
        }
    }

    private void paintBall(Graphics g) {
        g.fillOval(ballX, ballY, ballDiameter, ballDiameter);
//        g.drawRect(ballX, ballY, ballDiameter, ballDiameter);
    }

    private void paintScore(Graphics g) {
        g.drawString("Score: " + score, 10, 20);
    }

    private void paintBar(Graphics g) {
        Color previous = g.getColor();
        Color randomColor = new Color(
                ThreadLocalRandom.current().nextInt(0, 255),
                ThreadLocalRandom.current().nextInt(0, 255),
                0);
        g.setColor(randomColor);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(previous);
    }

    private void paintBackground(Graphics g) {
        Color previous = g.getColor();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(previous);
    }

    private void addListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setNewXBar(e);
            }

            private void setNewXBar(MouseEvent e) {
                int x = e.getX();
                if (x > getWidth() - barWidth) {
                    x = getWidth() - barWidth;
                }
                barX = x;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ballXDir = ThreadLocalRandom.current().nextInt(-5, 6);
                ballYDir = -ThreadLocalRandom.current().nextInt(5, 6);
                removeMouseListener(this);
            }
        });
    }

    private void setBlankCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);
    }

    private void initializeBlocks() {
        final int xStart = 50;
        int actualX = 50, actualY = 50;
        int xGap, yGap;
        xGap = yGap = 20;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                blocks[i][j] = new Block(actualX, actualY);
                actualX += blocks[i][j].width + xGap;
            }
            actualX = xStart;

            actualY += blocks[i][0].heigth + yGap;
        }
    }

    private void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
