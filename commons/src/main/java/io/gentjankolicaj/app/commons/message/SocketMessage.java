package io.gentjankolicaj.app.commons.message;

import java.io.Serializable;

public class SocketMessage implements Serializable {

  private String type;
  private int x;
  private int y;

  public SocketMessage() {
  }

  public SocketMessage(String type, int x, int y) {
    this.type = type;
    this.x = x;
    this.y = y;
  }

  public SocketMessage(String type, int[] position) {
    super();
    this.type = type;
    this.x = position[0];
    this.y = position[1];
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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


  @Override
  public String toString() {
    return "SocketMessage{" +
        "type='" + type + '\'' +
        ", x=" + x +
        ", y=" + y +
        '}';
  }
}
