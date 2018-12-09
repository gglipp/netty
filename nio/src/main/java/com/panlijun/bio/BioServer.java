package com.panlijun.bio;

import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author panlijun
 */
public class BioServer {
    private static final Logger log = getLogger(BioServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                receiveMsg(clientSocket);

                answerMsg(clientSocket);
            } catch (IOException e) {
                log.error("error", e);
            }
        }
    }

    private static void receiveMsg(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        log.info("receiving client message:");
        String msg;
        while (clientSocket.getKeepAlive() && (msg = bufferedReader.readLine()) != null) {
            log.info(msg);
        }
    }

    private static void answerMsg(Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);

        log.info("answering client message:");
        printWriter.println("you message has already received. :)");


    }


}
