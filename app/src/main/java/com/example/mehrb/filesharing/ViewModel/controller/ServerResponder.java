package com.example.mehrb.filesharing.ViewModel.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by mehrb on 1/18/2018.
 */

public class ServerResponder implements Runnable{
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

    ServerResponder(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            fillRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillRequest() throws IOException {
        request = dataInputStream.readUTF().split("\n");
    }

    /**
     *
     * @return the request
     *         SENDSIZE sends the size of the file
     *         SENDFILE sends the file stream
     */
    private String determineRequest() {
        if (request.length != 0) {
            return request[0];
        }
        return "NULL";
    }


    private void respondToReuest() {
        switch (determineRequest()) {
            case "SENDSIZE":

                break;
            case "SENDFILE":

                break;
            default:

        }
    }
}
