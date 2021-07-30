package gui;

import javax.swing.*;
import java.awt.*;

public class GuiFrame extends AbstractFrame {

    private final JFrame mainFrame;
    private final MainPanel mainPanel;
    private final String frameTitle;
    private final Dimension size;

    public GuiFrame(String frameTitle, Dimension size) {
        super();
        this.frameTitle = frameTitle;
        this.size = size;
        this.mainFrame = new JFrame();

        this.mainPanel = new MainPanel(size);

        initMainFrame();
    }


    private void initMainFrame() {
        mainFrame.add(mainPanel);

        mainFrame.setTitle(frameTitle);
        mainFrame.setSize(size);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }


    @Override
    public void repaintFrame() {
        mainFrame.repaint();
    }

    public void moveBall() {
        mainPanel.ball.move();
    }

    class MainPanel extends JPanel {
        private final Ball ball;

        public MainPanel(Dimension size) {
            super();
            this.setSize(size);
            this.ball = new Ball(this.getSize());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (ball != null && ball.isVisible()) {
                ball.draw(g);
            }
        }

    }
}
