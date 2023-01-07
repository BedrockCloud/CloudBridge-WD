package com.bedrockcloud.cloudbridge.network;

import java.net.Socket;
import com.bedrockcloud.cloudbridge.network.client.ClientRequest;
import dev.waterdog.waterdogpe.ProxyServer;

import java.io.IOException;
import java.net.ServerSocket;

public class NetworkManager extends Thread {
    private static final int BACKLOG_SIZE = 50;
    public ServerSocket serverSocket;

    public NetworkManager(final int Port) {
        try {
            this.serverSocket = new ServerSocket(Port, BACKLOG_SIZE);
        } catch (IOException e) {
            ProxyServer.getInstance().shutdown();
        }
    }

    @Override
    public void run() {
        starts();
    }

    public void starts() {
        while (true) {
            try {
                final Socket socket = this.serverSocket.accept();
                final ClientRequest clientRequest = new ClientRequest(socket);
                clientRequest.start();
            } catch (IOException e) {
                ProxyServer.getInstance().getLogger().error("", e);
            }
        }
    }
}
