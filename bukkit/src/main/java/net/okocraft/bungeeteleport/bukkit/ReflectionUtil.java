package net.okocraft.bungeeteleport.bukkit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPipeline;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;

public final class ReflectionUtil {

    private ReflectionUtil() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    @SuppressWarnings("unchecked")
    public static List<ChannelFuture> getServerChannels() {
        try {
            ServerConnectionListener scl = Objects.requireNonNull(MinecraftServer.getServer().getConnection());
            Field channelsField = getChannelsField(scl.getClass());
            System.out.println("channels field name: " + channelsField.getName());
            channelsField.setAccessible(true);
            List<ChannelFuture> channels = (List<ChannelFuture>) channelsField.get(scl);
            if (isProtocolLibList(channels)) {
                Method getOriginalMethod = channels.getClass().getDeclaredMethod("getOriginal");
                getOriginalMethod.setAccessible(true);
                return (List<ChannelFuture>) getOriginalMethod.invoke(channels);
            } else {
                return channels;
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getChannelsField(Class<? extends ServerConnectionListener> sclClass) throws NoSuchFieldException {
        for (Field f : sclClass.getDeclaredFields()) {
            if (f.getGenericType().getTypeName().contains(ChannelFuture.class.getName())) {
                return f;
            }
        }
        throw new NoSuchFieldException();
    }

    private static boolean isProtocolLibList(List<ChannelFuture> channels) {
        return channels.getClass().getName().equals("com.comphenix.protocol.injector.netty.manager.ListeningList");
    }

    public static void printHandlerNames(ChannelPipeline pipe) {
        System.out.println("print handler names:");
        try {
            Field headField = DefaultChannelPipeline.class.getDeclaredField("head");
            headField.setAccessible(true);
            Object head = headField.get(pipe);

            Field tailField = DefaultChannelPipeline.class.getDeclaredField("tail");
            tailField.setAccessible(true);
            Object tail = tailField.get(pipe);

            Class<?> achcClazz = Class.forName("io.netty.channel.AbstractChannelHandlerContext");

            Field nextField = achcClazz.getDeclaredField("next");
            nextField.setAccessible(true);
            Object context = nextField.get(head);

            Method nameMethod = achcClazz.getDeclaredMethod("name");
            nameMethod.setAccessible(true);

            while (context != tail) {
                System.out.println(nameMethod.invoke(context));
                context = nextField.get(context);
            }
            System.out.println(nameMethod.invoke(context));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
