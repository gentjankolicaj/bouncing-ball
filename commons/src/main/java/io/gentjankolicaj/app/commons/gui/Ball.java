package io.gentjankolicaj.app.commons.gui;


import io.gentjankolicaj.app.commons.message.SocketMessage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Ball {

  private static final Logger LOGGER = LoggerFactory.getLogger(Ball.class);
  private final int width = 70;
  private final int height = 70;

  private final int dx = 2;

  private final int RAND_INTERVAL;
  private final Dimension frameDimension;

  private int x = -width;
  private int y = -height;

  private boolean inScope;
  private BufferedImage img;

  public Ball(Dimension frameDimension) {
    super();
    this.frameDimension = frameDimension;
    this.RAND_INTERVAL = frameDimension.height / 60;
    try {
      this.img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("70.png"));
    } catch (IOException ioException) {

    }
  }

  public void setInitPosition(int x, int y) {
    this.x = x;
    this.y = y;
    this.inScope = true;
  }

  public void setInitPosition(SocketMessage socketMessage) {
    this.x = getInitX(socketMessage);
    this.y = setInitY(socketMessage);
    this.inScope = true;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  private int getInitX(SocketMessage socketMessage) {
    return frameDimension.width - socketMessage.getX();
  }

  private int setInitY(SocketMessage socketMessage) {
    if (socketMessage.getY() > frameDimension.height) {
      return frameDimension.height;
    }
    if (socketMessage.getY() < 0) {
      return 0;
    } else {
      return socketMessage.getY();
    }
  }

  public void draw(Graphics g) {
    Graphics2D graphics2D = (Graphics2D) g;
    if (img == null) {
      Ellipse2D.Double circle = new Ellipse2D.Double(x, y, width, height);
      graphics2D.setColor(Color.RED);
      graphics2D.fill(circle);
    } else {
      graphics2D.drawImage(img, x, y, null);
    }
  }

  public boolean isVisible() {
    Rectangle2D rectangle2D = new Rectangle2D.Double(0, 0, frameDimension.getWidth(),
        frameDimension.getHeight());
    return rectangle2D.contains(x, y, width, height);
  }

  public boolean move() {
    if (inScope) {
      BallPosition ballPosition = getBallPosition(x + dx, getRandY(y), frameDimension);
      if (ballPosition.equals(BallPosition.INSIDE)) {
        x = x + dx;
        y = getRandY(y);
      } else if (ballPosition.equals(BallPosition.DOWN_OUTSIDE)) {
        x = x + dx;
        y = y - (int) (Math.random() * RAND_INTERVAL);
      } else if (ballPosition.equals(BallPosition.UP_OUTSIDE)) {
        x = x + dx;
        y = y + (int) (Math.random() * RAND_INTERVAL);
      } else if (ballPosition.equals(BallPosition.RIGHT_OUTSIDE)) {
        x = x + dx;
        y = getRandY(y);
        inScope = false;
      } else {
        inScope = false;
      }
    }

    if (inScope) {
      LOGGER.info("Moved " + this);
    } else {
      LOGGER.info("Not-moved " + this);
    }

    return inScope;
  }

  private BallPosition getBallPosition(int x, int y, Dimension frameDimension) {
    int Ax = x, Ay = y, Bx = Ax + width, By = Ay + height;
    if (contains(frameDimension, Ax, Ay, Bx, By)) {
      return BallPosition.INSIDE;
    } else if (By > frameDimension.getHeight()) {
      return BallPosition.DOWN_OUTSIDE;
    } else if (By < 0) {
      return BallPosition.UP_OUTSIDE;
    } else if (Bx > frameDimension.getWidth()) {
      return BallPosition.RIGHT_OUTSIDE;
    } else if (Bx < 0) {
      return BallPosition.LEFT_OUTSIDE;
    } else {
      return BallPosition.OUTSIDE;
    }
  }

  protected boolean contains(Dimension frameDimension, int Ax, int Ay, int Bx, int By) {
    if (!(frameDimension == null) && !(Bx <= 0.0D) && !(By <= 0.0D)) {
      return Ax >= 0 && Ay >= 0 && Bx <= frameDimension.getWidth() && By <= frameDimension.height;
    } else {
      return false;
    }
  }

  protected int getRandY(int y) {
    int randVal = (int) (Math.random() * 2);
    if (randVal == 1) {
      return y + (int) (Math.random() * RAND_INTERVAL);
    } else {
      return y - (int) (Math.random() * RAND_INTERVAL);
    }
  }


  @Override
  public String toString() {
    return "Ball{" +
        "width=" + width +
        ", height=" + height +
        ", x=" + x +
        ", y=" + y +
        '}';
  }
}
