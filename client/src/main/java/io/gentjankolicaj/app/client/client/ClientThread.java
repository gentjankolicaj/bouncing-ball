package io.gentjankolicaj.app.client.client;

import io.gentjankolicaj.app.client.config.GlobalConfig;
import io.gentjankolicaj.app.commons.gui.GuiFrame;
import io.gentjankolicaj.app.commons.message.SocketMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientThread extends Thread {


  private final GuiFrame guiFrame;

  private final CustomSocketClient customSocketClient;
  private boolean receivedMessage;
  private boolean shapeInScope;

  public ClientThread(GuiFrame guiFrame) {
    super();
    this.guiFrame = guiFrame;
    this.customSocketClient = new CustomSocketClient();
    this.setName("MOTION-THREAD");
  }

  @Override
  public void run() {
    customSocketClient.start();
    while (true) {
      try {
        if (!receivedMessage) {
          log.info("Waiting for socket message:");

          //Waits till message is received from socket
          SocketMessage socketMessage = customSocketClient.awaitSocketMessage();
          log.info("From socket : " + socketMessage);

          //Set ball init position
          guiFrame.setBallPosition(socketMessage);
          shapeInScope = true;
          receivedMessage = true;
        }

        if (shapeInScope) {
          boolean isMoved = guiFrame.moveBall();
          guiFrame.repaintFrame();
          if (!isMoved) {

            //Reset to start waiting for new message from io.gentjankolicaj.app.thread & not moved ball till then
            shapeInScope = false;
            receivedMessage = false;

            //send last coordinate message to io.gentjankolicaj.app.thread
            SocketMessage socketMessage = new SocketMessage("Ball", guiFrame.getBallPosition());
            boolean sentMessage = customSocketClient.sendSocketMessage(socketMessage);
            log.info("To " + sentMessage + ", " + socketMessage);
          }
        }
        Thread.sleep(GlobalConfig.GUI_THREAD_SLEEP);
      } catch (InterruptedException e) {
        log.error("Error on running thread ", e);
      }
    }
  }

  static class CustomSocketClient {

    protected Socket socket;

    public CustomSocketClient() {
      super();
    }

    public void start() {
      try {
        socket = new Socket(GlobalConfig.HOSTNAME, GlobalConfig.PORT);
        log.info("Connected : " + socket);
      } catch (Exception e) {
        System.exit(0);
        e.printStackTrace();
      }
    }


    public SocketMessage awaitSocketMessage() {
      SocketMessage socketMessage = null;
      try {
        //Loop till message is received
        ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
        Object message;
        log.info("Waiting for socket message");
        while ((message = reader.readObject()) == null) {
          log.info(".");
        }
        socketMessage = (SocketMessage) message;
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        System.exit(0);
      }
      return socketMessage;
    }


    public boolean sendSocketMessage(SocketMessage socketMessage) {
      boolean sent = false;
      try {
        ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
        writer.writeObject(socketMessage);

        //Flush forces bytes to be written
        writer.flush();
        sent = true;
      } catch (Exception e) {
        e.printStackTrace();
      }
      return sent;
    }
  }


}
