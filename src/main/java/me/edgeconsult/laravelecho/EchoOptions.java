package me.edgeconsult.laravelecho;

import io.socket.client.IO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class EchoOptions {
    private Map<String, String> headers;
    private URI uri;
    private IO.Options socketOpts;
    private String eventNamespace;
    //public String authEndpoint;

    private EchoOptions() throws URISyntaxException {
        headers = new HashMap<>();
        //authEndpoint = "/broadcasting/auth";
        eventNamespace = "App.Events";
    }

    public EchoOptions(String uri)  throws URISyntaxException {
        this();
        this.uri = new URI(uri);
        this.socketOpts = null;
    }

    public EchoOptions(String uri, IO.Options socketOpts)  throws URISyntaxException {
        this();
        this.uri = new URI(uri);
        this.socketOpts = socketOpts;
    }

    public URI getUri() {
        return uri;
    }

    public IO.Options getSocketOpts() {
        return socketOpts;
    }

    public void setEventNamespace(String eventNamespace) {
        this.eventNamespace = eventNamespace;
    }

    public String getEventNamespace() {
        return eventNamespace;
    }

/*    public JSONObject getAuth() throws Exception {
        JSONObject auth = new JSONObject();
        JSONObject headers = new JSONObject();

        for (String header : this.headers.keySet()) {
            headers.put(header, this.headers.get(header));
        }

        auth.put("headers", headers);

        return auth;
    }*/

}
