package me.edgeconsult.laravelecho.connector;

import me.edgeconsult.laravelecho.EchoCallback;
import me.edgeconsult.laravelecho.EchoOptions;
import me.edgeconsult.laravelecho.channel.AbstractChannel;

public abstract class AbstractConnector {
    protected EchoOptions options;

    public AbstractConnector(EchoOptions options) {
        this.options = options;
    }

    public abstract void connect(EchoCallback success, EchoCallback error);

    public abstract AbstractChannel channel(String channel);

    public abstract void leave(String channel);

    public abstract boolean isConnected();

    public abstract void disconnect();
}
