package com.bedrockcloud.cloudbridge.network.client;

import com.bedrockcloud.cloudbridge.CloudBridge;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class ClientRequest extends Thread
{
    private static final int BUFFER_SIZE = 1024;
    private final DatagramPacket datagramPacket;
    private final DatagramSocket datagramSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientRequest(final DatagramPacket datagramPacket, final DatagramSocket datagramSocket) {
        this.datagramPacket = datagramPacket;
        this.datagramSocket = datagramSocket;
        this.dataInputStream = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(datagramPacket.getData()), BUFFER_SIZE));
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(new ByteArrayOutputStream(), BUFFER_SIZE));
    }

    public DatagramPacket getDatagramPacket() {
        return this.datagramPacket;
    }

    public DatagramSocket getSocket() {
        return this.datagramSocket;
    }

    @Override
    public void run() {
        try {
            // Wait for a request from the client
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            getSocket().receive(packet);

            // Read and process the request
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(packet.getData()), BUFFER_SIZE));
            String line = dis.readLine();
            System.out.println(line);
            if (line == null) {
                return;
            }

            CloudBridge.getPacketHandler().handleCloudPacket(CloudBridge.getPacketHandler().handleJsonObject(CloudBridge.getPacketHandler().getPacketNameByRequest(line), line));

            // Send a response to the client
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(baos, BUFFER_SIZE));
            dos.write(baos.toByteArray());
            dos.flush();
            byte[] data = baos.toByteArray();
            DatagramPacket responsePacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
            getSocket().send(responsePacket);
        } catch (IOException e) {
            // Handle the exception
        }
    }
}