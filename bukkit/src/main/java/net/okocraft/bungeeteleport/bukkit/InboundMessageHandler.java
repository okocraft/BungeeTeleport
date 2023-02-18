package net.okocraft.bungeeteleport.bukkit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

public class InboundMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        System.out.println(msg.getClass().getName());

        if (!(msg instanceof SocketChannel channel)) {
            ctx.fireChannelRead(msg);
            return;
        }

        System.out.println("pass no socket channel if");

        try {
            // ↓ should write code to remove on unload
            channel.pipeline().addLast("btp_inner", new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
                    System.out.println("reach inner handler");
                    ByteBuf buf = ((ByteBuf) msg).copy();
                    // int len = buf.getInt(0);
                    // System.out.println("got len: " + len);
                    // if (len <= 0 || len > 1000000) {
                    //     ctx.fireChannelRead(msg);
                    //     return;
                    // }
                    // String message = new String(buf.getBytes(0, new byte[len]).array(), StandardCharsets.ISO_8859_1);
                    // System.out.println("got message " + message);
                    ctx.fireChannelRead(msg);
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.out.println("valid end?");
        ctx.fireChannelRead(msg);
    }
}
