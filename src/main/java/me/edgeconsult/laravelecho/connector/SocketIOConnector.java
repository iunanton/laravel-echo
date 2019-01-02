package me.edgeconsult.laravelecho.connector;

import io.socket.client.IO;
import io.socket.client.Socket;
import me.edgeconsult.laravelecho.EchoCallback;
import me.edgeconsult.laravelecho.EchoOptions;
import me.edgeconsult.laravelecho.channel.AbstractChannel;
import me.edgeconsult.laravelecho.channel.SocketIOChannel;

import java.util.HashMap;
import java.util.Map;

public class SocketIOConnector extends AbstractConnector {
    private Socket socket;
    private Map<String, SocketIOChannel> channels;

    public SocketIOConnector(EchoOptions options) {
        super(options);
        channels = new HashMap<>();
    }

    @Override
    public void connect(EchoCallback success, EchoCallback error) {
        socket = IO.socket(options.getUri(), options.getSocketOpts());
        if (success != null) {
            socket.on(Socket.EVENT_CONNECT, success);
        }
        if (error != null) {
            socket.on(Socket.EVENT_ERROR, error);
        }
        socket.connect();
    }

    @Override
    public AbstractChannel channel(String channel) {
        if (!channels.containsKey(channel)) {
            channels.put(channel, new SocketIOChannel(socket, channel, options));
        }
        return channels.get(channel);
    }

    @Override
    public void leave(String channel) {
        for (String subscribed : channels.keySet()) {
            if (subscribed.equals(channel)) {
                channels.get(subscribed).unsubscribe(null);
                channels.remove(subscribed);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return socket.connected();
    }

    @Override
    public void disconnect() {
        for (String subscribed : channels.keySet()) {
            channels.get(subscribed).unsubscribe(null);
        }
        channels.clear();
        socket.disconnect();

    }

    public String socketID() {
        return socket.id();
    }
}
