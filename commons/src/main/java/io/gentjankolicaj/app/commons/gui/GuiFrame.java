package io.gentjankolicaj.app.commons.gui;

import io.gentjankolicaj.app.commons.message.SocketMessage;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

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

  public boolean moveBall() {
    return mainPanel.ball.move();
  }

  public void setBallPosition(int x, int y) {
    this.mainPanel.ball.setInitPosition(x, y);
  }

  public int[] getBallPosition() {
    int[] pos = new int[2];
    pos[0] = mainPanel.ball.getX();
    pos[1] = mainPanel.ball.getY();
    return pos;
  }

  public void setBallPosition(SocketMessage socketMessage) {
    this.mainPanel.ball.setInitPosition(socketMessage);
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
