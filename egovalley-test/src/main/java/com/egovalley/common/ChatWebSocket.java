package com.egovalley.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/chatWebSocket/{nickname}")
public class ChatWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocket.class);

    // 用来存放每个客户端对应的ChatWebSocket对象
    private static CopyOnWriteArraySet<ChatWebSocket> webSocketSet = new CopyOnWriteArraySet<ChatWebSocket>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("nickname") String nickname) {
        this.session = session;
        for (ChatWebSocket chatWebSocket : webSocketSet) {// 保留同一个用户的最新session
            String chatSocketUser = (String) chatWebSocket.session.getUserProperties().get("chatSocketUser");
            if (chatSocketUser.equals(nickname)) {
                webSocketSet.remove(chatWebSocket);
            }
        }
        webSocketSet.add(this);// 加入set中
        session.getUserProperties().put("chatSocketUser", nickname);
        logger.info(">>> " + nickname + "加入了聊天室, 当前在线人数为: " +webSocketSet.size());
//        this.session.getAsyncRemote().sendText("欢迎进入聊天室, 当前在线人数为: " + webSocketSet.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {// Session session
        // 这里调用onClose时, session貌似已经被框架内部清除了, 所以再调出错, 消息去前端发吧
//        this.session.getAsyncRemote().sendText(session.getUserProperties().get("chatSocketUser") + "离开了");
        webSocketSet.remove(this);// 从set中删除
        logger.info(">>> 有一连接关闭, 当前在线人数为: " + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("nickname") String nickname) {
        System.out.println("来自客户端的消息 => " + nickname + ": " + message);
        // 群发消息
        broadcast(nickname + ": " + message);
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
        for (ChatWebSocket item : webSocketSet) {
            // 同步异步说明参考：http://blog.csdn.net/who_is_xiaoming/article/details/53287691
            // this.session.getBasicRemote().sendText(message);
            item.session.getAsyncRemote().sendText(message);// 异步发送消息.
        }
    }
    public void broadcast(int flag) {
        if (flag == 1) {
            for (ChatWebSocket chatWebSocket : webSocketSet) {
                String nickname = (String) chatWebSocket.session.getUserProperties().get("chatSocketUser");
//                chatWebSocket.session.getAsyncRemote().sendText("欢迎【" + nickname + "】进入聊天室! 当前在线人数为: " +  + webSocketSet.size());// 异步发送消息.
                chatWebSocket.session.getAsyncRemote().sendText("欢迎进入聊天室! 当前在线人数为: " +  + webSocketSet.size());// 异步发送消息.
            }
        } else if (flag == 2) {
            for (ChatWebSocket chatWebSocket : webSocketSet) {
                String nickname = (String) chatWebSocket.session.getUserProperties().get("chatSocketUser");
                chatWebSocket.session.getAsyncRemote().sendText("【" + nickname + "】离开了聊天室! 当前在线人数为: " +  + webSocketSet.size());// 异步发送消息.
            }
        }
    }

    /**
     * 向指定用户发送信息
     */
    public void assignMessageToUser(String message, String assignUser) {
        for (ChatWebSocket chatWebSocket : webSocketSet) {
            String sessionUser = (String) chatWebSocket.session.getUserProperties().get("chatSocketUser");
            logger.info(">>> 当前webSocketSet中的用户有: " + sessionUser);
            try {
                if (!(StringUtils.isEmpty(assignUser)) && assignUser.equals(sessionUser)) {
                    logger.info(">>> 向指定用户[" + assignUser + "]发送信息[" + message + "]");
                    chatWebSocket.sendMessage(message);
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
        for (ChatWebSocket chatWebSocket : webSocketSet) {
            String nickname = (String) chatWebSocket.session.getUserProperties().get("chatSocketUser");
            System.out.println("在线用户: " + nickname);
            userList.add(nickname);
        }
        return userList;
    }

}
