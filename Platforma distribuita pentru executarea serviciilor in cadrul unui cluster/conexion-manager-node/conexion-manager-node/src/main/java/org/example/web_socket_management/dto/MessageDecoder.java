package org.example.web_socket_management.dto;


import com.google.gson.Gson;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {
    private final Gson gson;
    public MessageDecoder(){
        this.gson = new Gson();
    }

    @Override
    public Message decode(String s) {
        return this.gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    // nu sunt folosite dar trebuie declarate
    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}