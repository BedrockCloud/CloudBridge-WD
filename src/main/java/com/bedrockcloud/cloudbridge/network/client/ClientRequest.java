package com.bedrockcloud.cloudbridge.network.client;

import com.bedrockcloud.cloudbridge.CloudBridge;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientRequest extends Thread
{
    private static final int BUFFER_SIZE = 1024;
    private final Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientRequest(final Socket socket) {
        this.socket = socket;
        try {
            this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE));
            this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), BUFFER_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public void run() {
        while (!this.socket.isClosed()) {
            String line = null;
            try {
                line = this.dataInputStream.readLine();
                if (line == null) {
                    close();
                    return;
                }
                JSONObject packet = CloudBridge.getPacketHandler().handleJsonObject(CloudBridge.getPacketHandler().getPacketNameByRequest(line), line);
                if (packet != null) {
                    CloudBridge.getPacketHandler().handleCloudPacket(packet, this);
                }
            } catch (IOException e) {
                close();
            }
        }
    }

    private void close() {
        if (this.socket != null) {
            try {
                this.socket.close();
                ProxyServer.getInstance().shutdown();
            } catch (IOException e) {
            }
        }
    }
}
