package com.example.interfaces.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/websocket/{terminalId}")
public class WebSocketService {

    private final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    /**
     * 保存連接信息
     */
    private static final Map<String, Session> CLIENTS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam("terminalId") String terminalId, Session session) throws Exception {
        logger.info(session.getRequestURI().getPath() + "，打開連接開始：" + session.getId());
        // 當前連接已存在，關閉
        if (CLIENTS.containsKey(terminalId)) {
            onClose(CLIENTS.get(terminalId));
        }
        CLIENTS.put(terminalId, session);
        logger.info(session.getRequestURI().getPath() + "，打開連接完成：" + session.getId());
    }

    @OnClose
    public void onClose(@PathParam("terminalId") String terminalId, Session session) throws Exception {
        logger.info(session.getRequestURI().getPath() + "，關閉連接開始：" + session.getId());
        CLIENTS.remove(terminalId);
        logger.info(session.getRequestURI().getPath() + "，關閉連接完成：" + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("前台發送消息：" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error(error.toString());
    }

    public void onClose(Session session) {
        // 判斷當前連接是否在線
        // if (!session.isOpen()) {
        // return;
        // }
        try {
            session.close();
        } catch (IOException e) {
            logger.error("金鬥雲關閉連接異常：" + e);
        }
    }

    public void sendMessage(String message, Session session) {
        try {
            session.getAsyncRemote().sendText(message);
            logger.info("推送成功：" + message);
        } catch (Exception e) {
            logger.error("推送異常：" + e);
        }
    }

    public boolean sendMessage(String terminalId, String message) {
        try {
            Session session = CLIENTS.get(terminalId);
            session.getAsyncRemote().sendText(message);
            logger.info("推送成功：" + message);
            return true;
        } catch (Exception e) {
            logger.error("推送異常：" + e);
            return false;
        }
    }

    public void broadcast(String message) {
        for (String terminalId : CLIENTS.keySet()) {
            sendMessage(terminalId, message);
        }
    }

}
