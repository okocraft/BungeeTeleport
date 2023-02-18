package net.okocraft.bungeeteleport.bukkit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadedValue;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    private Channel modifiedChannel;

    @Override
    public void onEnable() {
        this.modifiedChannel = ReflectionUtil.getServerChannels().get(0).channel();
        this.modifiedChannel.pipeline().addFirst("btp_inbound", new InboundMessageHandler());
    }

    @Override
    public void onDisable() {
        try {
            // for force plugin reloading
            this.modifiedChannel.pipeline().remove(InboundMessageHandler.class);
        } catch (NoSuchElementException ignored) {
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        Channel channel = connectToServer(new InetSocketAddress("localhost", 25541));

        channel.close();

        // ByteBuf buf = channel.alloc().buffer();
        // String message = args[0];
        // byte[] messageBytes = message.getBytes(StandardCharsets.ISO_8859_1);
        // buf.setInt(0, messageBytes.length);
        // buf.setBytes(0, messageBytes);
//
        // channel.writeAndFlush(buf).addListener(f -> {
        //     if (f.isSuccess() && f.isDone()) {
        //         Main.getPlugin(Main.class).getLogger().info("Success sending message.");
        //     } else {
        //         Main.getPlugin(Main.class).getLogger().warning("!!Error on sending message.");
        //     }
        //     channel.close();
        // });

        return true;
    }

    private static Channel connectToServer(InetSocketAddress address) {
        Class<? extends SocketChannel> socketChannelClazz;
        @SuppressWarnings("deprecation")
        LazyLoadedValue<? extends MultithreadEventLoopGroup> lazyinitvar;

        if (Epoll.isAvailable() && MinecraftServer.getServer().isEpollEnabled()) {
            socketChannelClazz = EpollSocketChannel.class;
            lazyinitvar = Connection.NETWORK_EPOLL_WORKER_GROUP;
        } else {
            socketChannelClazz = NioSocketChannel.class;
            lazyinitvar = Connection.NETWORK_WORKER_GROUP;
        }

        return new Bootstrap()
                .group(lazyinitvar.get())
                .handler(new ChannelInitializer<>() {
                    protected void initChannel(@NotNull Channel channel) {
                        try {
                            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException ignored) {
                        }

                        // channel.pipeline().addLast("message_modifier", modifier);
                    }
                })
                .channel(socketChannelClazz)
                .connect(address.getAddress(), address.getPort())
                .addListener(future -> {
                    if (future.isSuccess()) {
                        JavaPlugin.getPlugin(Main.class).getLogger().info("Success connecting to " + address);
                    } else {
                        JavaPlugin.getPlugin(Main.class).getLogger().warning("!!Error on connecting to " + address);
                    }
                })
                .syncUninterruptibly()
                .channel();
    }
}
