package com.example.mehrb.filesharing.ViewModel.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mehrb on 1/18/2018.
 */

public class ServerRunnable implements Runnable {
    //
    // current server info
    //
    private ServerSocket serverSocket = null;
    private static final int SERVER_PORT = 56021;

    private boolean finished = false;

    //
    // thread manager
    //
    private ExecutorService executorService = null;

    @Override
    public void run() {
        try {
            initServer();
            waitForConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * initializes the server with SERVER_PORT
     */
    private void initServer() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setReceiveBufferSize(10);
        }
    }

    /**
     * waits for connection and then starts the connection manager thread
     */
    private void waitForConnection() throws IOException {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }

        while (!finished) {
            Socket clientSocket = serverSocket.accept();
            executorService.execute(new ServerResponder(clientSocket));
        }
    }

}
