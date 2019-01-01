package me.edgeconsult.laravelecho;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Echo {
    private URI uri;
    private Socket socket;

    public Echo(String uri) throws URISyntaxException {
        this(uri, null);
    }

    public Echo(String uri, IO.Options opts) throws URISyntaxException {
        this(new URI(uri), null);
    }

    public Echo(URI uri) {
        this(uri, null);
    }

    public Echo(URI uri, IO.Options opts) {
        //main method
        this.uri = uri;
    }

    public void connect(Emitter.Listener success, Emitter.Listener error) {
        socket = IO.socket(uri);
        if (success != null) {
            socket.on(Socket.EVENT_CONNECT, success);
        }
        if (error != null) {
            socket.on(Socket.EVENT_ERROR, error);
        }
        socket.connect();
    }

    public static void main(String args[]) {
        System.out.println("Run Echo.main()");

        SSLContext sslContext;
        TrustManager[] trustManagers;
        IO.Options opts;

        Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(Level.ALL);

        Logger logger = Logger.getLogger(Socket.class.getName());
        logger.addHandler(handlerObj);
        logger.setLevel(Level.ALL);

        // set self-signed certificate for SSL
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            InputStream certInputStream = Echo.class.getResourceAsStream("server.pem");
            BufferedInputStream bis = new BufferedInputStream(certInputStream);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry("edgeconsult.me", cert);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                .build();

        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
        IO.setDefaultOkHttpCallFactory(okHttpClient);

        opts = new IO.Options();
        opts.callFactory = okHttpClient;
        opts.webSocketFactory = okHttpClient;



        try {
            Echo echo = new Echo("http://localhost:6001", opts);
            echo.connect(
                    (Object... objects) -> System.out.println("Connected"),
                    (Object... objects) -> System.out.println("Error"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
