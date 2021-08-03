package client;

import config.GlobalConfig;
import gui.GuiFrame;
import message.SocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientThread.class);

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

    public void run() {
        customSocketClient.start();
        while (true) {
            if (!receivedMessage) {
                LOGGER.info("Waiting for socket message:");

                //Waits till message is received from socket thread
                SocketMessage socketMessage = customSocketClient.awaitSocketMessage();
                LOGGER.info("From thread : " + socketMessage);

                //Set ball init position
                guiFrame.setBallPosition(socketMessage);
                shapeInScope = true;
                receivedMessage = true;
            }

            if (shapeInScope) {
                boolean isMoved = guiFrame.moveBall();
                guiFrame.repaintFrame();
                if (!isMoved) {

                    //Reset to start waiting for new message from thread & not moved ball till then
                    shapeInScope = false;
                    receivedMessage = false;

                    //send last coordinate message to thread
                    SocketMessage socketMessage = new SocketMessage("Ball", guiFrame.getBallPosition());
                    boolean sentMessage = customSocketClient.sendSocketMessage(socketMessage);
                    LOGGER.info("To thread " + sentMessage + ", " + socketMessage);
                }
            }

            try {
                Thread.sleep(GlobalConfig.GUI_THREAD_SLEEP);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }


    }

    class CustomSocketClient {
        protected Socket socket;

        public CustomSocketClient() {
            super();
        }

        public void start() {
            try {
                socket = new Socket(GlobalConfig.HOSTNAME, GlobalConfig.PORT);
                LOGGER.info("Connected : " + socket);
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
                LOGGER.info("Waiting for socket message");
                while ((message = reader.readObject()) == null) {
                    System.out.print(".");
                }
                socketMessage = (SocketMessage) message;
            } catch (IOException io) {
                io.printStackTrace();
                System.exit(0);
            } catch (ClassNotFoundException ce) {
                ce.printStackTrace();
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
