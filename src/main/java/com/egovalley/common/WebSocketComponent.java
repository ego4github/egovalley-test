package com.egovalley.common;

import com.alibaba.fastjson.JSONObject;
import com.egovalley.service.ASRAudioDataService;
import com.egovalley.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/webSocket/{webSocketUser}")
public class WebSocketComponent {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketComponent.class);

    // 用来存放每个客户端对应的ChatWebSocket对象
    private static CopyOnWriteArraySet<WebSocketComponent> webSocketSet = new CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private static ASRAudioDataService asrAudioDataService;
    @Autowired
    public void setAsrAudioDataService(ASRAudioDataService asrAudioDataService) {
        WebSocketComponent.asrAudioDataService = asrAudioDataService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("webSocketUser") String webSocketUser) {
        this.session = session;
        for (WebSocketComponent webSocketComponent : webSocketSet) {// 保留同一个用户的最新session
            String chatSocketUser = (String) webSocketComponent.session.getUserProperties().get("webSocketUser");
            if (chatSocketUser.equals(webSocketUser)) {
                webSocketSet.remove(webSocketComponent);
            }
        }
        webSocketSet.add(this);// 加入set中
        session.getUserProperties().put("webSocketUser", webSocketUser);
        logger.info(">>> " + webSocketUser + "加入, 当前在线人数为: " +webSocketSet.size());
//        this.session.getAsyncRemote().sendText("欢迎进入聊天室, 当前在线人数为: " + webSocketSet.size());

        if (webSocketUser.contains("EGVASR")) {
            String sessionId = webSocketUser.substring(0, webSocketUser.length() - 6);

        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {// Session session
        // 这里调用onClose时, session貌似已经被框架内部清除了, 所以再调出错, 消息去前端发吧
//        this.session.getAsyncRemote().sendText(session.getUserProperties().get("webSocketUser") + "离开了");
        webSocketSet.remove(this);// 从set中删除
        logger.info(">>> 有一连接关闭, 当前在线人数为: " + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("webSocketUser") String webSocketUser) {
        boolean flag = false;
        try {
            JSONObject.parseObject(message);
            flag = true;
        } catch (Exception e) {
            logger.info(">>> not json type");
        }
        if (flag && asrAudioDataService != null) {// TODO 这里有问题, 聊天室中手动输入json格式字符串, 就会调错
            try {
                Map<String, Object> paramMap = JsonUtils.jsonToMap(message);
                String channel = "" + paramMap.get("channel");
                if (StringUtils.isNotBlank(channel) && !"null".equals(channel) && "asr".equals(channel)) {
                    asrAudioDataService.transcriberService(paramMap);
                }
            } catch (Exception e) {
                logger.error(">>> onMessage接收异常", e);
            }
        } else {
            logger.info("来自客户端的消息 => " + webSocketUser + ": " + message);
            // 群发消息
            broadcast(webSocketUser + ": " + message);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发自定义消息
     */
    public void broadcast(String message) {
        for (WebSocketComponent item : webSocketSet) {
            // 同步异步说明参考：http://blog.csdn.net/who_is_xiaoming/article/details/53287691
            // this.session.getBasicRemote().sendText(message);
            item.session.getAsyncRemote().sendText(message);// 异步发送消息.
        }
    }
    public void broadcast(int flag) {
        if (flag == 1) {
            for (WebSocketComponent webSocketComponent : webSocketSet) {
                String webSocketUser = (String) webSocketComponent.session.getUserProperties().get("webSocketUser");
//                chatWebSocket.session.getAsyncRemote().sendText("欢迎【" + webSocketUser + "】进入聊天室! 当前在线人数为: " +  + webSocketSet.size());// 异步发送消息.
                webSocketComponent.session.getAsyncRemote().sendText("欢迎进入聊天室! 当前在线人数为: " +  + webSocketSet.size());// 异步发送消息.
            }
        } else if (flag == 2) {
            for (WebSocketComponent webSocketComponent : webSocketSet) {
                String webSocketUser = (String) webSocketComponent.session.getUserProperties().get("webSocketUser");
                webSocketComponent.session.getAsyncRemote().sendText("【" + webSocketUser + "】离开了聊天室! 当前在线人数为: " +  + webSocketSet.size());// 异步发送消息.
            }
        }
    }

    /**
     * 向指定用户发送信息
     */
    public void assignMessageToUser(String message, String assignUser) {
        for (WebSocketComponent webSocketComponent : webSocketSet) {
            String sessionUser = (String) webSocketComponent.session.getUserProperties().get("webSocketUser");
            logger.info(">>> 当前webSocketSet中的用户有: " + sessionUser);
            try {
                if (StringUtils.isNotBlank(assignUser) && !"null".equals(assignUser) && assignUser.equals(sessionUser)) {
                    logger.info(">>> 向指定用户[" + assignUser + "]发送信息[" + message + "]");
                    webSocketComponent.sendMessage(message);
                }
            } catch (IOException e) {
                logger.error(">>> 向指定用户[" + assignUser + "]发送信息[" + message + "]异常", e);
                continue;
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        if (this.session.isOpen()) {
            this.session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 获取所有在线用户
     */
    public List<String> getOnlineUsers() {
        List<String> userList = new ArrayList<>();
        for (WebSocketComponent webSocketComponent : webSocketSet) {
            String webSocketUser = (String) webSocketComponent.session.getUserProperties().get("webSocketUser");
            System.out.println("在线用户: " + webSocketUser);
            userList.add(webSocketUser);
        }
        return userList;
    }

}
