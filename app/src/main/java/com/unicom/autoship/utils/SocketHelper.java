package com.unicom.autoship.utils;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.IO.Options;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
/**
 * Created by dyzhou on 2018/8/10.
 */
public class SocketHelper {

    private static final String RSP_TYPE_CONNECT = "onConnect";
    private static final String RSP_TYPE_DISCONNECT = "onDisConnect";
    private static final String RSP_TYPE_CONNECT_ERROR = "onConnectError";
    public static final String RSP_TYPE_LOCATION = "locationEvent";


    private Listener onConnect;
    private Listener onReconnecting;
    private Listener onReConnect;
    private Listener onDisconnect;
    private Listener onConnectError;
    private Listener locationEvent;


    private static Socket socket;
    private volatile static SocketHelper singleton;

    private List<SocketRspListener> listeners = new ArrayList<>();

    private SocketHelper() {
    }

    public static SocketHelper getHttpsSingleton(String server) throws URISyntaxException {

        if (null == singleton) {
            synchronized (SocketHelper.class) {
                if (null == singleton) {
                    singleton = new SocketHelper();
                    Options options = new Options();
//                    SSLContext sslContext = getSocketSSLContext();
//                    if (null != sslContext) {
//                        options.sslContext = getSocketSSLContext();
//                    }
//                    options.hostnameVerifier = n ew HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    };
                    options.transports = new String[]{WebSocket.NAME, Polling.NAME};
                    socket = IO.socket(server, options);
                }
            }
        }
        return singleton;
    }

    public static SSLContext getSocketSSLContext() {
        SSLContext sc = null;
        TrustManager[] trustCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustCerts, null);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sc;
    }

    public void reconnecte() {
        initWebSocket(socket);
        loginSocket(socket);
    }

    public Socket getSocket() {
        return socket;
    }

    public interface SocketRspListener {
        void onSocketResponse(String rspType, String JSONStr);
    }

    public void registerOnSocketRspListener(SocketRspListener listener) {
        listeners.add(listener);
    }

    public void unRegisterOnSocketRspListener(SocketRspListener listener) {
        if (listeners.contains(listener)) {

            listeners.remove(listener);
        }
    }

    private void loginSocket(Socket socket) {

        JSONObject jsonObject = new JSONObject();

        socket.emit("Login", jsonObject);
    }

    // 初始化webSocket
    public void initWebSocket(final Socket socket) {

        onConnect = new Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
//                    loginSocket(socket);
                    Log.i("websocket","connected---"+args[0].toString());
                }
            }
        };

        onReconnecting = new Listener() {
            @Override
            public void call(Object... args) {
                Log.i("websocket","reConnecting---"+args[0].toString());
            }
        };

        onReConnect = new Listener() {
            @Override
            public void call(Object... args) {
                Log.i("websocket","reConnected---"+args[0].toString());
                loginSocket(socket);
            }
        };

        onDisconnect = new Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    Log.i("websocket","onDisconnect---"+args[0].toString());
                }
            }
        };

        onConnectError = new Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    Log.i("websocket","onConnectError---"+args[0].toString());
                    for (SocketRspListener listener : listeners) {
                        listener.onSocketResponse(RSP_TYPE_CONNECT_ERROR, args[0].toString());
                    }
                }
            }
        };

        locationEvent = new Listener() {
            @Override
            public void call(Object... args) {
                Log.i("websocket"," location Resp---"+args.toString());
                if (args.length > 0) {
                    Log.i("websocket","onLocationResp---"+args[0].toString());

                    for (SocketRspListener listener : listeners) {
                        listener.onSocketResponse(RSP_TYPE_LOCATION, args[0].toString());
                    }
                }
            }
        };


        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_RECONNECT, onReConnect);
        socket.on(Socket.EVENT_RECONNECTING, onReconnecting);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("locationEvent", locationEvent);
        socket.connect();
    }

    public void closeConnection() {

        if (null == socket) return;
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_RECONNECTING, onReconnecting);
        socket.off(Socket.EVENT_RECONNECT, onReConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("locationEvent", locationEvent);

        socket.disconnect();
    }

}
