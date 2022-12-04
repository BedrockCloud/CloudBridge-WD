package com.bedrockcloud.player;

import com.bedrockcloud.cloudbridge.network.packets.ProxyPlayerQuitPacket;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.session.CompressionAlgorithm;
import dev.waterdog.waterdogpe.network.session.LoginData;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.Locale;

public class CustomPlayer extends ProxiedPlayer {

    public CustomPlayer(ProxyServer proxy, BedrockServerSession session, CompressionAlgorithm compression, LoginData loginData) {
        super(proxy, session, compression, loginData);
    }

    @Override
    public void disconnect() {
        super.disconnect();
        final ProxyPlayerQuitPacket newpacket = new ProxyPlayerQuitPacket();
        newpacket.playerName = this.getName().toLowerCase(Locale.ROOT).replace(" ", "_");
        try {
            newpacket.leftServer = this.getServerInfo().getServerName();
        } catch (NullPointerException e) {
            newpacket.leftServer = "NOT FOUND";
        }
        newpacket.pushPacket();
    }
}
