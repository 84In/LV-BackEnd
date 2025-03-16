package com.luanvan.notificationservice.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OrderWebSocketHandler extends TextWebSocketHandler {
    // Map lưu trữ danh sách client theo trạng thái quan tâm
    private static final Map<WebSocketSession, String> sessionStatusMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client kết nối: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Client gửi tin nhắn để đăng ký trạng thái cần lắng nghe (VD: "cancelled")
        String status = message.getPayload();
        sessionStatusMap.put(session, status);
        System.out.println("Client " + session.getId() + " đăng ký trạng thái: " + status);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionStatusMap.remove(session);
        System.out.println("Client ngắt kết nối: " + session.getId());
    }

    // Gửi order đến client theo trạng thái mà họ đăng ký
    public void sendOrderToClients(String orderJson) throws IOException {
        for (WebSocketSession session : sessionStatusMap.keySet()) {
            String clientStatus = sessionStatusMap.get(session);

            // Kiểm tra nếu client quan tâm tới trạng thái này thì mới gửi
            if (orderJson.contains(clientStatus)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(orderJson));
                }
            }
        }
    }
}
