package com.zf.customchat.config;


import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取httpSession对象
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        // 保存httpSession
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);

    }
}
