package me.edgeconsult.laravelecho.channel;

import io.socket.client.Socket;
import me.edgeconsult.laravelecho.EchoCallback;
import me.edgeconsult.laravelecho.EchoOptions;
import me.edgeconsult.laravelecho.util.EventFormatter;
import org.json.JSONObject;

import java.util.*;

public class SocketIOChannel extends AbstractChannel {
    private Socket socket;
    private String name;
    private EchoOptions options;
    private EventFormatter formatter;
    private Map<String, List<EchoCallback>> eventsCallbacks;

    public SocketIOChannel(Socket socket, String name, EchoOptions options) {
        this.socket = socket;
        this.name = name;
        this.options = options;
        this.formatter = new EventFormatter(options.getEventNamespace());
        this.eventsCallbacks = new HashMap<>();

        subscribe(null);

        //configureReconnector(); why we need this??
    }

    public void subscribe(EchoCallback callback) {
        JSONObject object = new JSONObject();
        try {
            object.put("channel", name);
            if (callback == null) {
                socket.emit("subscribe", object);
            } else {
                socket.emit("subscribe", object, callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(EchoCallback callback) {
        unbind();
        JSONObject object = new JSONObject();
        try {
            object.put("channel", name);
            if (callback == null) {
                socket.emit("unsubscribe", object);
            } else {
                socket.emit("unsubscribe", object, callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AbstractChannel listen(String event, EchoCallback callback) {
        on(formatter.format(event), callback);
        return this;
    }

    private void on(String event, final EchoCallback callback) {
        EchoCallback listener = (Object... objects) -> {
            if (objects.length > 0 && objects[0] instanceof String) {
                String channel = (String) objects[0];
                if (channel.equals(name)) {
                    callback.call(objects);
                }
            }
        };
        socket.on(event, listener);
        bind(event, listener);
    }

    private void bind(String event, EchoCallback callback) {
        if (!eventsCallbacks.containsKey(event)) {
            eventsCallbacks.put(event, new ArrayList<>());
        }
        eventsCallbacks.get(event).add(callback);
    }

    private void unbind() {
        Iterator<String> iterator = eventsCallbacks.keySet().iterator();
        while (iterator.hasNext()) {
            socket.off(iterator.next());
            iterator.remove();
        }
    }

}
