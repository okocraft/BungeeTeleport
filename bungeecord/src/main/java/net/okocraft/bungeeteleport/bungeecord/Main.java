package net.okocraft.bungeeteleport.bungeecord;

import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener {

    public static final String PMC_NAME = "lunachat:info";

    private static final NioSocketChannel c = new NioSocketChannel();

    @Override
    public void onEnable() {

        getProxy().registerChannel(PMC_NAME);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().unregisterChannel(PMC_NAME);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(PMC_NAME)) {
            return;
        }

        try (
                ByteArrayInputStream byteIn = new ByteArrayInputStream(event.getData());
                DataInputStream dataIn = new DataInputStream(byteIn);
        ) {
            String signature = dataIn.readUTF();
            if (dataIn.readUTF().equals("serverbound")) {
                return;
            }
            if (signature.equals("default_channel_get")) {
                String playerName = dataIn.readUTF();
                // LunaChatAPI api = LunaChatBungee.getInstance().getLunaChatAPI();
                // api.setDefaultChannel(playerName, api.getDefaultChannel(playerName).getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
