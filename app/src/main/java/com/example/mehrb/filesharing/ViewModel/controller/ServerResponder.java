package com.example.mehrb.filesharing.ViewModel.controller;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by mehrb on 1/18/2018.
 */

public class ServerResponder implements Runnable {
    //
    // client socket
    //
    private Socket clientSocket;

    //
    // client streams
    //
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    //
    // client request and response
    //
    private String[] request;
    private String response;
    private String fileName;
    private int fileSize;

    ServerResponder(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.dataInputStream = new DataInputStream(clientSocket.getInputStream());
            this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            fillRequest();
            respondToRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * finds the requests
     *
     * @throws IOException for readUTF
     */
    private void fillRequest() throws IOException {
        request = dataInputStream.readUTF().split("\n");
        Log.d("FIRSTFILE", request[0]);
        if (request.length >= 1) {
            fileName = request[1];
        }
    }

    /**
     * @return the request
     * SENDSIZE sends the size of the file
     * SENDFILE sends the file stream
     */
    private String determineRequest() {
        if (request.length != 0) {
            return request[0];
        }
        return "NULL";
    }


    /**
     * sends appropriate response to the request
     */
    private void respondToRequest() throws IOException {
        switch (determineRequest()) {
            case "SENDSIZE":
                sendFileSize();
                break;
            case "SENDFILE":
                sendFile();
                break;
            case "SENDSIZERESPONSE":
                if (request.length >= 2) {
                    fileSize = Integer.parseInt(request[2]);
                }
                break;
            case "SENDFILERESPONSE":
//                receiveFile();
                break;
            default:
        }

        sendResponse();
    }

    /**
     * sends the file size to the client
     */
    private void sendFileSize() {
        String fileAddress = "/storage/emulated/0/FileSharing/" + fileName;
        File file = new File(fileAddress);

        if (file.exists()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append("SENDSIZERESPONSE")
                    .append("\n")
                    .append(file.length())
                    .append("\n");

            response = stringBuilder.toString();
        }

        else {
            response = "SENDSIZERESPONSE\n0\n";
        }
    }

    private void sendFile() throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;

        Socket sock = null;

        try {
            // send file
            File myFile = new File("/storage/emulated/0/FileSharing/" + fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
        } catch (IOException ex) {
        } finally {
            if (bis != null) bis.close();
            if (os != null) os.close();
            if (sock != null) sock.close();
        }


    }



    private void sendResponse() {
        if (clientSocket.isConnected()) {
            try {
                if (response != null && response.length() > 0) {
                    dataOutputStream.writeUTF(response);
                } else {
                    dataOutputStream.writeUTF("NULL");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
