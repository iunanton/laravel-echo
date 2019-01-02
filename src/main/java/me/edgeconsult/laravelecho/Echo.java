package me.edgeconsult.laravelecho;

import io.socket.client.IO;
import io.socket.client.Socket;
import me.edgeconsult.laravelecho.channel.SocketIOChannel;
import me.edgeconsult.laravelecho.connector.SocketIOConnector;
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
    private SocketIOConnector connector;

    public Echo(EchoOptions options) {
        connector = new SocketIOConnector(options);
    }

    public void connect(EchoCallback success, EchoCallback error) {
        connector.connect(success, error);
    }

    public SocketIOChannel channel(String channel) {
        return (SocketIOChannel) connector.channel(channel);
    }

    public boolean isConnected() {
        return connector.isConnected();
    }

    public void disconnect() {
        connector.disconnect();
    }

    public static void main(String args[]) {
        System.out.println("Run Echo.main()");

        SSLContext sslContext;
        TrustManager[] trustManagers;
        IO.Options socketOpts;

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

        socketOpts = new IO.Options();
        socketOpts.callFactory = okHttpClient;
        socketOpts.webSocketFactory = okHttpClient;

        try {
            EchoOptions options = new EchoOptions("http://localhost:6001", socketOpts);
            Echo echo = new Echo(options);
            echo.connect(
                    (Object... objects) -> System.out.println("Connected: " + echo.connector.socketID()),
                    (Object... objects) -> System.out.println("Error"));
            //SocketIOChannel channel = echo.channel("test-event");
            //channel.listen("MessagePushed", (Object... objects) -> System.out.println("MessagePushed"));
            SocketIOChannel channel = echo.channel("private-user.1");
            channel.listen("PrivateEvent", (Object... objects) -> System.out.println("PrivateEvent\n"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
