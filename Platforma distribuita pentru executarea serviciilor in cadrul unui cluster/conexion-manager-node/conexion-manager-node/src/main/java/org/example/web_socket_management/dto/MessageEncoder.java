package org.example.web_socket_management.dto;


import com.google.gson.Gson;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {
    private final Gson gson;

    public MessageEncoder() {
        this.gson = new Gson();
    }

    @Override
    public String encode(Message message)  {
        return this.gson.toJson(message);
    }

    // din interfata
    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}