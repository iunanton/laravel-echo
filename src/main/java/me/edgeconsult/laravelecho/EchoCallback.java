package me.edgeconsult.laravelecho;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;

public interface EchoCallback extends Emitter.Listener, Ack {
}
