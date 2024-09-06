package com.gaj.GKook;

import com.gaj.GKook.framework.Handler.ApiHandler;
import com.gaj.GKook.framework.Handler.WebSocketHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;


public class Main {

    private static WebSocketHandler wsHandler;
    private static ApiHandler apiHandler;

    public static void main(String[] args) throws Exception {
        apiHandler = new ApiHandler();
        wsHandler = new WebSocketHandler();
        wsHandler.connect();

        for (; ; ) {

        }
    }

    public static String getGateway() {
        return apiHandler.getGateway();
    }

}
