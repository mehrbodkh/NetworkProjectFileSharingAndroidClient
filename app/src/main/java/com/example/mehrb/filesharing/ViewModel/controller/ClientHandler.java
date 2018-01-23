package com.example.mehrb.filesharing.ViewModel.controller;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Created by mehrb on 1/23/2018.
 */

public class ClientHandler implements Runnable {
    private String serverIp;
    private int serverPort;
    private String fileName;
    private String clientId;

    private Socket socket;

    public ClientHandler(String fileName, String serverIp, int serverPort, String clientId) {
        this.fileName = fileName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientId = clientId;


    }

    private void sendRequest() throws IOException {
        socket = new Socket(serverIp, serverPort);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        StringBuilder stringBuilder = null;
        DataInputStream dataInputStream = null;

        socket = new Socket(serverIp, serverPort);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        stringBuilder = new StringBuilder();

        stringBuilder
                .append("REQUESTFILEOWNER")
                .append("\n")
                .append(clientId)
                .append("\n")
                .append(fileName)
                .append("\n");

        dataOutputStream.writeUTF(stringBuilder.toString());

        dataInputStream = new DataInputStream(socket.getInputStream());
        String[] response = dataInputStream.readUTF().split("\n");

        serverPort = 0;
        serverIp = "0";
        Log.d("FILEOWE", response[0]);
        if (response.length >= 3) {
            serverIp = response[2];
            serverPort = Integer.parseInt(response[3]);
        }
        socket.close();
    }

    @Override
    public void run() {

        try {
            sendRequest();
            Log.d("IP", serverIp);
            Executors.newCachedThreadPool().execute(new ClientRunnable(fileName, serverIp, 56021));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
