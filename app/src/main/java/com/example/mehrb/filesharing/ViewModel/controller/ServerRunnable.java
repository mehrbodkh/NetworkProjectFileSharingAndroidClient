package com.example.mehrb.filesharing.ViewModel.controller;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
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
    private String clientId;

    private boolean finished = false;

    //
    // thread manager
    //
    private ExecutorService executorService = null;

    //
    // main server info
    //
    private String mainServerIP;
    private static final int MAIN_SERVER_PORT = 47021;

    public ServerRunnable(String mainServerIP, String clientId) {
        this.mainServerIP = mainServerIP;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            initServer();
            initInMainServer();
            initTimer();
            addFiles();
            waitForConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFiles()
    {
        File folder = new File("/storage/emulated/0/FileSharing");


        if ( !folder.exists() )
        {
            Log.d("Files", "return");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append("ADDFILES")
                .append("\n")
                .append(clientId)
                .append("\n");

        for (int i = 0; i < folder.list().length; i++) {
            stringBuilder
                    .append(folder.list()[i])
                    .append("\n");
        }

        Socket socket = null;
        try {
            socket = new Socket(mainServerIP, MAIN_SERVER_PORT);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(stringBuilder.toString());
            socket.close();
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
     * sends the main server it's online status and resends every 30 seconds
     */
    private void initInMainServer() throws IOException {
        Socket socket = new Socket(mainServerIP, MAIN_SERVER_PORT);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("ADDME")
                .append("\n")
                .append(clientId)
                .append("\n");
        outputStream.writeUTF(stringBuilder.toString());
        socket.close();
    }

    private void initTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    socket = new Socket(mainServerIP, MAIN_SERVER_PORT);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder
                            .append("IMHERE")
                            .append("\n")
                            .append(clientId)
                            .append("\n");
                    outputStream.writeUTF(stringBuilder.toString());
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addFiles();

            }
        }, 30000, 30000);
    }

    /**
     * waits for connection and then starts the connection manager thread
     */
    private void waitForConnection() throws IOException {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }

        while (!finished) {
            Log.d("ServerRunnable", "Waiting...");
            Log.d("ServerIP", serverSocket.getInetAddress().getHostAddress());
            Log.d("ServerPort", serverSocket.getLocalPort() + "");
            Socket clientSocket = serverSocket.accept();
            Log.d("ServerRunnable", "Accepted");
            executorService.execute(new ServerResponder(clientSocket));
        }
    }

}
