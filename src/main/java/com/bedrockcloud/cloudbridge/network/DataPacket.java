package com.bedrockcloud.cloudbridge.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import dev.waterdog.waterdogpe.ProxyServer;
import org.json.simple.JSONValue;
import com.bedrockcloud.cloudbridge.config.Config;
import org.json.simple.JSONObject;
import java.util.HashMap;
import java.util.Map;

import java.net.*;
import java.io.*;

public class DataPacket {
    public Map<String, Object> data;
    public String packetName;

    public DataPacket() {
        this.data = new HashMap<String, Object>();
    }

    public String getPacketName() {
        return this.packetName;
    }

    public void addValue(final String key, final String value) {
        this.data.put(key, value);
    }

    public void addValue(final String key, final int value) {
        this.data.put(key, value);
    }

    public void handle(final JSONObject jsonObject) {
    }

    public String encode() {
        this.addValue("packetName", this.getPacketName());
        final Config config = new Config("./cloud.yml", 2);
        this.addValue("serverName", config.get("name", "PROXY_SERVER_NAME"));
        return JSONValue.toJSONString(this.data);
    }

    public void pushPacket(final DataPacket cloudPacket) {
        try (DatagramSocket s = new DatagramSocket(32324)) {
            if (s.isClosed()) {
                return;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayOutputStream.write(cloudPacket.encode().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] data = byteArrayOutputStream.toByteArray();
            InetAddress address = null;
            try {
                address = InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException ignored) {
            }
            int port = 32323;
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, port);
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket = new DatagramSocket();
            } catch (SocketException ex) {
                ProxyServer.getInstance().getLogger().logException(ex);
            }
            try {
                datagramSocket.send(datagramPacket);
            } catch (IOException ex) {
                ProxyServer.getInstance().getLogger().logException(ex);
            }
        } catch (Exception exception){
            ProxyServer.getInstance().getLogger().logException(exception);
        }
    }
}