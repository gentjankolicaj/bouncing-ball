package server;

import config.GlobalConfig;
import gui.GuiFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
    private final GuiFrame guiFrame;

    protected ServerSocket serverSocket;
    protected List<Socket> socketClients;

    private boolean inScope;

    public ServerThread(GuiFrame guiFrame) {
        super();
        this.guiFrame = guiFrame;
        this.socketClients = new ArrayList<>();
    }


    @Override
    public void run() {
        //Start server socket & manage new incoming clients
        startServer();

        int clientSocketIndex = 0;
        int clientSocketNumber = socketClients.size();
        Socket client = null;

        guiFrame.setBallPosition(0, 150);
        inScope = true;

        while (clientSocketNumber > 0) {
            if (inScope) {
                boolean isMoved = guiFrame.moveBall();
                guiFrame.repaintFrame();
                if (!isMoved) {
                    inScope = false;

                    client = socketClients.get(clientSocketIndex);
                    if (clientSocketNumber != 1)
                        clientSocketIndex++;

                    if (client != null && !client.isClosed()) {
                        SocketMessage writeSocketMessage = new SocketMessage("Ball", guiFrame.getBallPosition());
                        boolean sentMessage = sendSocketMessage(client, writeSocketMessage);
                        LOGGER.info("Message sent : " + writeSocketMessage);
                        if (sentMessage) {
                            SocketMessage readSocketMessage = awaitSocketMessage(client);
                            if (readSocketMessage == null)
                                throw new RuntimeException("Message received null ");

                            inScope = true;
                            guiFrame.setBallPosition(readSocketMessage);

                        } else
                            throw new RuntimeException("Message not sent to socket " + client);
                    }
                }
            }

            try {
                Thread.sleep(GlobalConfig.THREAD_SLEEP);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }


    }


    public void startServer() {
        try {
            serverSocket = new ServerSocket(GlobalConfig.PORT);
            Socket client = serverSocket.accept();
            LOGGER.info("-->Found new " + client);

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
            while (true) {
                Object message = reader.readObject();
                if (message != null) {
                    socketMessage = (SocketMessage) message;
                    break;
                }
            }

        } catch (IOException io) {
            io.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return socketMessage;

    }


    class ClientManager extends Thread {
        private final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

        public ClientManager() {
            super();
            this.setName("ClientManager");
        }

        public void run() {
            while (true) {
                try {
                    synchronized (serverSocket) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
