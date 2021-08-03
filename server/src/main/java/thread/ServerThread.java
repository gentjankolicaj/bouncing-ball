package thread;

import config.GlobalConfig;
import gui.GuiFrame;
import message.SocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
        this.setName("MOTION-THREAD");
    }


    @Override
    public void run() {
        //Start thread socket & manage new incoming clients
        startServer();

        //Start socket manager to manage new sockets
        new SocketManager().start();

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
                    clientSocketNumber = socketClients.size();
                    if (clientSocketNumber != 1) {
                        clientSocketIndex++;

                        //Protection for upcoming loop client index
                        //Set to 0 if maximum is reached
                        clientSocketIndex = clientSocketIndex >= clientSocketNumber ? 0 : clientSocketIndex;
                    }

                    if (client != null && !client.isClosed()) {

                        SocketMessage writeSocketMessage = new SocketMessage("Ball", guiFrame.getBallPosition());
                        boolean sentMessage = sendSocketMessage(client, writeSocketMessage);
                        LOGGER.info("To client : " + writeSocketMessage);
                        if (sentMessage) {
                            SocketMessage readSocketMessage = awaitSocketMessage(client);
                            if (readSocketMessage == null)
                                throw new RuntimeException("From client message is null ");

                            inScope = true;
                            guiFrame.setBallPosition(readSocketMessage);

                        } else
                            throw new RuntimeException("Message not sent to client " + client);
                    }
                }
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

            //Accept new socket client;
            Socket client = serverSocket.accept();
            LOGGER.info("INITIAL--> New socket : " + client);

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
                System.out.print(".");
            }
            socketMessage = (SocketMessage) message;

        } catch (IOException io) {
            io.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return socketMessage;

    }


    class SocketManager extends Thread {
        private final Logger LOGGER = LoggerFactory.getLogger(SocketManager.class);

        public SocketManager() {
            super();
            this.setName("SocketManager");
        }

        public void run() {
            LOGGER.info("Started socket manager.");
            try {
                //Set socket accept timeout
                synchronized (serverSocket) {
                    serverSocket.setSoTimeout(GlobalConfig.SERVER_SOCKET_TIMEOUT);
                    LOGGER.info("ServerSocket timeout set : " + GlobalConfig.SERVER_SOCKET_TIMEOUT);
                }

            } catch (SocketException e) {
                e.printStackTrace();
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
                            LOGGER.info("CLIENT_MANAGER--> New socket " + newSocket);
                        }
                    }

                    //Sleep before try to get again intrinsic lock
                    Thread.sleep(GlobalConfig.SOCKET_MANAGER_THREAD_SLEEP);
                } catch (SocketTimeoutException se) {
                    LOGGER.error(se.getMessage());
                } catch (InterruptedException ie) {
                    LOGGER.error(ie.getMessage());
                } catch (IOException io) {
                    LOGGER.error(io.getMessage());
                }
            }
        }
    }

}
