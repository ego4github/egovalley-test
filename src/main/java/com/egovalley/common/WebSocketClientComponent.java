package com.egovalley.common;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class WebSocketClientComponent {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientComponent.class);

    public static WebSocketClient createWebSocketClient(String sessionId) {
        try {
            String url = "wss://cxyyb.zgpajf.com.cn/aliOnlineAsr/websocket/" + sessionId;
            WebSocketClient webSocketClient = new WebSocketClient(
                    new URI(url),
                    new Draft_6455()
            ) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    logger.info(">>> [webSocket] 连接成功");
                }

                @Override
                public void onMessage(String s) {
                    logger.info(">>> [webSocket] 收到消息 = {}", s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    logger.info(">>> [webSocket] 退出连接");
                }

                @Override
                public void onError(Exception e) {
                    logger.error(">>> [webSocket] 连接错误", e);
                }
            };
            webSocketClient.setConnectionLostTimeout(7200);// 设置client超时时长, 单位秒
            webSocketClient.connect();
            return webSocketClient;
        } catch (Exception e) {
            logger.error(">>> webSocketClient连接异常");
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            WebSocketClient webSocketClient = createWebSocketClient("9998");
            System.out.println("webSocketClient = " + webSocketClient);
            while (true) {
                System.out.println(webSocketClient.getReadyState());
                if (webSocketClient != null && WebSocket.READYSTATE.OPEN.equals(webSocketClient.getReadyState())) {
                    webSocketClient.send("9998");
                    break;
                } else {
                    System.out.println("mode");
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
