package me.edgeconsult.laravelecho;

import io.socket.client.IO;

import java.net.URI;
import java.net.URISyntaxException;

public class Echo {
    public Echo(String uri) {
        this(uri, null);
    }

    public Echo(String uri, IO.Options opts) {
        //this(new URI(uri), null);
    }

    public Echo(URI uri) {
        this(uri, null);
    }

    public Echo(URI uri, IO.Options opts) {

    }

    public static void main(String args[]) {
        System.out.println("Echo.main()");
        //Echo echo = new Echo("");
    }
}
