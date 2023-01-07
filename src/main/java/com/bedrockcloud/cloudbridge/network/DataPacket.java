package com.bedrockcloud.cloudbridge.network;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.json.simple.JSONValue;
import com.bedrockcloud.cloudbridge.config.Config;
import com.bedrockcloud.cloudbridge.network.client.ClientRequest;
import org.json.simple.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class DataPacket {
    private static final String PACKET_NAME_KEY = "packetName";
    private static final String SERVER_NAME_KEY = "serverName";
    private static final String DEFAULT_SERVER_NAME = "PROXY_SERVER_NAME";
    private Map<String, Object> data;
    private String packetName;
    private Config config;

    public DataPacket() {
        this.data = new HashMap<>();
        this.config = new Config("./cloud.yml", 2);
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

    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
    }

    public String encode() {
        this.data.put(PACKET_NAME_KEY, this.getPacketName());
        this.data.put(SERVER_NAME_KEY, this.config.get("name", DEFAULT_SERVER_NAME));
        return JSONValue.toJSONString(this.data);
    }

    public void pushPacket() {
        try (Socket s = new Socket("127.0.0.1", 32323)) {
            if (s.isClosed()) {
                return;
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
                bufferedWriter.write(this.encode());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}