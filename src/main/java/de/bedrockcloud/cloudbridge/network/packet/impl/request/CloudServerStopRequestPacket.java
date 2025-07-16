package de.bedrockcloud.cloudbridge.network.packet.impl.request;

import de.bedrockcloud.cloudbridge.network.packet.RequestPacket;
import de.bedrockcloud.cloudbridge.network.packet.utils.PacketData;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CloudServerStopRequestPacket extends RequestPacket {

    private String server;

    public CloudServerStopRequestPacket(String server) {
        this.server = server;
    }

    @Override
    protected void encodePayload(PacketData packetData) {
        super.encodePayload(packetData);
        packetData.write(server);
    }

    @Override
    protected void decodePayload(PacketData packetData) {
        super.decodePayload(packetData);
        server = packetData.readString();
    }

}