package com.bedrockcloud.cloudbridge.network.handler;

import com.bedrockcloud.cloudbridge.network.DataPacket;
import com.bedrockcloud.cloudbridge.network.client.ClientRequest;
import dev.waterdog.waterdogpe.ProxyServer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler
{

    private static final String PACKET_NAME_KEY = "packetName";
    private Map<String, Class<? extends DataPacket>> registeredPackets;



    public PacketHandler() {
        this.registeredPackets = new HashMap<>();
    }

    public void registerPacket(final String name, final Class<? extends DataPacket> packet) {
        this.registeredPackets.put(name, packet);
    }

    public void unregisterPacket(final String name) {
        this.registeredPackets.remove(name);
    }

    public boolean isPacketRegistered(final String name) {
        return this.registeredPackets.containsKey(name);
    }

    public Map<String, Class<? extends DataPacket>> getRegisteredPackets() {
        return Collections.unmodifiableMap(this.registeredPackets);
    }

    public Class<? extends DataPacket> getPacketByName(final String packetName) {
        return this.registeredPackets.get(packetName);
    }

    public String getPacketNameByRequest(final String request) {
        final JSONObject jsonObject = (JSONObject) JSONValue.parse(request);
        if (jsonObject != null && jsonObject.containsKey(PACKET_NAME_KEY)) {
            return jsonObject.get(PACKET_NAME_KEY).toString();
        }
        ProxyServer.getInstance().getLogger().warning("Handling of packet cancelled because the packet is unknown!");
        return "Unknown Packet";
    }

    public JSONObject handleJsonObject(final String packetName, final String input) {
        if (this.isPacketRegistered(packetName)) {
            final JSONObject jsonObject = (JSONObject) JSONValue.parse(input);
            if (jsonObject != null) {
                return jsonObject;
            }
        }
        ProxyServer.getInstance().getLogger().warning("Denied unknown packet.");
        return new JSONObject();
    }

    public void handleCloudPacket(final JSONObject jsonObject, final ClientRequest clientRequest) {
        if (clientRequest.getSocket().getInetAddress().toString().equals("/127.0.0.1")) {
            if (jsonObject.containsKey(PACKET_NAME_KEY)) {
                final String packetName = jsonObject.get(PACKET_NAME_KEY).toString();
                final Class<? extends DataPacket> packetClass = this.getPacketByName(packetName);
                if (packetClass != null) {
                    try {
                        final DataPacket packet = packetClass.newInstance();
                        packet.handle(jsonObject, clientRequest);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ProxyServer.getInstance().getLogger().warning("Denied unauthorized server.");
                }
            }
        }
    }
}
