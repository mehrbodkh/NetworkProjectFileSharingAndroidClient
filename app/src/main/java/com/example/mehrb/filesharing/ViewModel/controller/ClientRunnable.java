package com.example.mehrb.filesharing.ViewModel.controller;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by mehrb on 1/23/2018.
 */

public class ClientRunnable implements Runnable{
    private int serverPort;
    private String serverIp;

    private Socket serverSocket;

    private String clientId;

    private String fileName;
    private int fileSize;

    private String[] request;

    DataInputStream dataInputStream = null;

    public ClientRunnable(String fileName, String serverIp, int serverPort) {
        this.fileName = fileName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            sendFileSizeRequest();
            sendFileRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileSizeRequest() throws IOException {
        Log.d("SERVER", serverIp + serverPort);
        serverSocket = new Socket(serverIp, serverPort);
        Log.d("Waiiint", "war2");
        DataOutputStream dataOutputStream = new DataOutputStream(serverSocket.getOutputStream());

        Log.d("Waiiint", "war3");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("SENDSIZE")
                .append("\n")
                .append(fileName)
                .append("\n");

        dataOutputStream.writeUTF(stringBuilder.toString());
        Log.d("Waiiint", "war");
        receiverFileSize(new DataInputStream(serverSocket.getInputStream()));
    }

    private void receiverFileSize(DataInputStream dataInputStream) throws IOException {
        Log.d("DOWNLOADER", "download started");
        String[] response = dataInputStream.readUTF().split("\n");
        Log.d("FILER", response[0]);
        if (response.length > 1 && response[0].equals("SENDSIZERESPONSE")) {
            fileSize = Integer.parseInt(response[1]);
            Log.d("FILER", fileSize + "");
        }
        serverSocket.close();
    }


    private void sendFileRequest() throws IOException {
        if (fileSize != 0) {
            serverSocket = new Socket(serverIp, serverPort);
            DataOutputStream dataOutputStream = new DataOutputStream(serverSocket.getOutputStream());

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder
                    .append("SENDFILE")
                    .append("\n")
                    .append(fileName)
                    .append("\n");

            dataOutputStream.writeUTF(stringBuilder.toString());
            receiveFile(new DataInputStream(serverSocket.getInputStream()));
        }
    }

    private void receiveFile(DataInputStream dataInputStream) throws IOException {
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {

            // receive file
            byte [] mybytearray  = new byte [fileSize];
            InputStream is = dataInputStream;
            fos = new FileOutputStream("/storage/emulated/0/FileSharing/" + fileName);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , fileSize);
            bos.flush();
        }
        finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
            if (serverSocket != null) serverSocket.close();
        }
    }
}
