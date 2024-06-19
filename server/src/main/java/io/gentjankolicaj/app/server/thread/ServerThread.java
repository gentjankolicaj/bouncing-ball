package io.gentjankolicaj.app.server.thread;

import io.gentjankolicaj.app.commons.gui.GuiFrame;
import io.gentjankolicaj.app.commons.message.SocketMessage;
import io.gentjankolicaj.app.server.config.GlobalConfig;
import io.gentjankolicaj.app.server.exception.ServerRuntimeException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
  private final GuiFrame guiFrame;

  protected ServerSocket serverSocket;
  protected List<Socket> socketClients;

  public ServerThread(GuiFrame guiFrame) {
    super();
    this.guiFrame = guiFrame;
    this.socketClients = new ArrayList<>();
    this.setName("MOTION-THREAD");
  }


  @Override
  public void run() {
    //Start thread socket & manage new incoming clients
    startServer();

    //Start socket manager to manage new sockets
    new SocketManager().start();

    Socket clientSocket = null;
    int clientSocketIndex = 0;
    int clientSocketNumber = socketClients.size();

    guiFrame.setBallPosition(0, 150);
    boolean inScope = true;

    int emptyAttemptCounter = 0;
    int emptyAttemptMax = 100;
    SocketMessage positionMessage = null;

    while (clientSocketNumber > 0) {

      if (inScope) {
        inScope = guiFrame.moveBall();
        guiFrame.repaintFrame();

      }
      if (!inScope) {
        clientSocket = socketClients.get(clientSocketIndex);
          if (clientSocket != null && !clientSocket.isClosed()) {
              if (positionMessage == null) {
                  positionMessage = new SocketMessage("Ball", guiFrame.getBallPosition());
              }
              boolean sentMessage = sendSocketMessage(clientSocket, positionMessage);

              LOGGER.info("To client : {} ", positionMessage);
              if (sentMessage) {
                  positionMessage = awaitSocketMessage(clientSocket);
                  if (positionMessage == null) {
                      throw new ServerRuntimeException("From client message is null ");
                  }

                  emptyAttemptCounter = 0;

              } else {
                  throw new ServerRuntimeException("Message not sent to client " + clientSocket);
              }
          } else {
              emptyAttemptCounter++;
          }

        //decisions for upcoming loop
        clientSocketNumber = socketClients.size();
        if (clientSocketNumber == 1) {
          //Set serverGui shape details for upcoming loop
          inScope = true;
          guiFrame.setBallPosition(positionMessage);
        } else {
          clientSocketIndex++;

          //Protection for upcoming loop client index
          //Set to 0 if maximum is reached
          // inScope true=> shapes appears in serverGUI in upcoming io.gentjankolicaj.app.thread
          if (clientSocketIndex >= clientSocketNumber) {
            clientSocketIndex = 0;

            //Set serverGui shape details
            inScope = true;
            guiFrame.setBallPosition(positionMessage);
          }
        }
      }

        if (emptyAttemptCounter >= emptyAttemptMax) {
            throw new ServerRuntimeException(
                "Consecutive empty attempts reached.Max " + emptyAttemptMax);
        }

      try {
        Thread.sleep(GlobalConfig.GUI_THREAD_SLEEP);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }


  }


  public void startServer() {
    try {
      serverSocket = new ServerSocket(GlobalConfig.PORT);

      //Accept new socket client
      Socket client = serverSocket.accept();
      LOGGER.info("INITIAL--> New socket : {} ", client);

      socketClients.add(client);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void closeServer() {
    try {
      this.socketClients = null;
      this.serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean sendSocketMessage(Socket client, SocketMessage socketMessage) {
    boolean sent = false;
    try {
      ObjectOutputStream writer = new ObjectOutputStream(client.getOutputStream());
      writer.writeObject(socketMessage);
      //Forces any buffered bytes to be written
      writer.flush();
      sent = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sent;
  }

  public SocketMessage awaitSocketMessage(Socket client) {
    SocketMessage socketMessage = null;
    try {
      //Loop till message is received
      ObjectInputStream reader = new ObjectInputStream(client.getInputStream());
      Object message;
      LOGGER.info("Waiting for socket message");
      while ((message = reader.readObject()) == null) {
        LOGGER.info(".");
      }
      socketMessage = (SocketMessage) message;

    } catch (IOException | ClassNotFoundException e) {
      LOGGER.error("Awaiting socket error ", e);
    }
    return socketMessage;

  }


  class SocketManager extends Thread {

    final Logger NESTED_LOGGER = LoggerFactory.getLogger(SocketManager.class);

    public SocketManager() {
      super();
      this.setName("SocketManager");
    }

    @Override
    public void run() {
      NESTED_LOGGER.info("Started socket manager.");
      try {
        //Set socket accept timeout
        synchronized (serverSocket) {
          serverSocket.setSoTimeout(GlobalConfig.SERVER_SOCKET_TIMEOUT);
          NESTED_LOGGER.info("ServerSocket timeout set : " + GlobalConfig.SERVER_SOCKET_TIMEOUT);
        }

      } catch (SocketException e) {
        NESTED_LOGGER.error("Socket error : ", e);
      }

      while (true) {
        try {
          //Synchronize of intrinsic lock
          synchronized (serverSocket) {
            Socket newSocket = serverSocket.accept();

            //Synchronize of intrinsic lock
            synchronized (socketClients) {

              //clean from null & closed sockets
              for (int i = 0; i < socketClients.size(); i++) {
                Socket tmp = socketClients.get(i);
                if (tmp == null || tmp.isClosed()) {
                  socketClients.remove(i);
                }
              }
              //add new socket
              socketClients.add(newSocket);
              NESTED_LOGGER.info("CLIENT_MANAGER--> New socket {} ", newSocket);
            }
          }
          //Sleep before try to get again intrinsic lock
          Thread.sleep(GlobalConfig.SOCKET_MANAGER_THREAD_SLEEP);
        } catch (SocketTimeoutException se) {
          NESTED_LOGGER.error(se.getMessage());
        } catch (InterruptedException | IOException e) {
          NESTED_LOGGER.error(e.getMessage());
        }
      }
    }
  }

}
