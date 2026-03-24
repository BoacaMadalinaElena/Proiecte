package org.example.web_socket_management;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.httpClient.AutoDiscoveryHttp;
import org.example.httpClient.HTTPClientAuthorization;
import org.example.httpClient.dto.TokenDto;
import org.example.httpClient.dto.UserAuthorizationDto;
import org.example.httpClient.exceptions.ServiceUnavailable;
import org.example.other.CustomPrinter;
import org.example.tcp_management.TCPManagement;
import org.example.tcp_management.model.MessageToFront;
import org.example.web_socket_management.dto.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(
        value = "/cluster/{token}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class CommunicationTCPEndpoint {
    private Session session;
    private String id;

    private static final HashMap<Session, String> users = new HashMap<>();
    private static final HashMap<Session, Long> usersTimes = new HashMap<>();
    private  static HTTPClientAuthorization httpClientAuthorization;
    private static final Map<String, ConcurrentLinkedQueue<MessageToFront>> messageFrontBackend = Collections.synchronizedMap(new HashMap<>());
    private final Gson gson;
    private static final HashMap<String,Thread> threadReadFromServer;
    private String token;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isStopWithOutClose = true;


    static {
        threadReadFromServer = new HashMap<>();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for(Map.Entry<Session,Long> entry : usersTimes.entrySet()){
                    if(entry.getValue()+ TimeUnit.MINUTES.toMillis(7) < (System.currentTimeMillis())){
                        CustomPrinter.printErr("Timpul pentru: " + entry.getKey().getId() + " a depasit 10 minute! Acesta va fi deconectat! " + (entry.getValue()+ TimeUnit.MINUTES.toMillis(5)));
                        usersTimes.remove(entry.getKey());
                        httpClientAuthorization.removeToken(users.get(entry.getKey()));
                        users.remove(entry.getKey());
                        if (threadReadFromServer.get(entry.getKey().getId()) != null)
                            threadReadFromServer.get(entry.getKey().getId()).stop();
                        try {
                            entry.getKey().close();
                        }catch (Exception ex){
                            CustomPrinter.printErr(ex.getMessage());
                        }
                        CustomPrinter.printInfo("The user with ID " + entry.getKey().getId() + " has disconnected. " );
                    }
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
    }

    public CommunicationTCPEndpoint() {
        CustomPrinter.printSuccess("CommunicationTCPEndpoint started!");
        httpClientAuthorization = new HTTPClientAuthorization();
        this.gson = new Gson();
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException, EncodeException {
        CustomPrinter.printWarning("______________OPEN___________");
        this.session = session;
        this.token = token;
        if(!usersTimes.containsKey(session))
        {
            usersTimes.put(session,System.currentTimeMillis());
        }else{
            usersTimes.replace(session,System.currentTimeMillis());
        }

        // authorization
        UserAuthorizationDto userAuthorizationDto = null;
        try {
            IpPublicAddressDto ipPublicAddressDto = AutoDiscoveryHttp.getIpAndPort();
            userAuthorizationDto = httpClientAuthorization.validateAndDeserializeJWT(new TokenDto(token, ipPublicAddressDto.getIp() + ":" + ipPublicAddressDto.getPort()));

            users.put(session, token);

            CustomPrinter.printInfo("The user with ID " + session.getId() + " has connected.");
        } catch (ServiceUnavailable unavailable) {
            Message message = new Message("server", unavailable.getMessage(), false, null, false, 503, 0);
            this.session.getBasicRemote().sendObject(new Message(new MessageFrame(this.gson.toJson(message), 0, 1)));
            try {
                isStopWithOutClose = false;
                this.session.close();
                isStopWithOutClose = true;
            } catch (java.io.IOException ignored) {
                // e de la faptul ca nu am utilizator real salvat
            }
            return;
        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
        }
        if (userAuthorizationDto != null) {
            this.id = userAuthorizationDto.getId();
            messageFrontBackend.put(id, new ConcurrentLinkedQueue<>());
        } else {
            Message message = new Message("server", "Token invalid!", false, null, false, 401, 0);
            this.session.getBasicRemote().sendObject(new Message(new MessageFrame(this.gson.toJson(message), 0, 1)));
            try {
                this.onClose(session,null);
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
            }
        }

        Message message = new Message("server", "Connected!", false, null, false, 200, 0);
        this.session.getBasicRemote().sendObject(new Message(new MessageFrame(this.gson.toJson(message), 0, 1)));

        // thread send to frontend
        Runnable myRunnable = () -> {
            while (true) {
                if (!messageFrontBackend.get(id).isEmpty()) {
                    MessageToFront messageToFromFront = messageFrontBackend.get(id).peek();
                    if (messageToFromFront != null) {

                        if (Objects.equals(messageToFromFront.getIdUser(), this.id)) {
                            messageFrontBackend.get(id).remove(messageToFromFront);
                            Message message1 = new Message("server", messageToFromFront.getMessage(), false, null, messageToFromFront.isRead(), messageToFromFront.getStatusCode(), messageToFromFront.getBytes() != null, messageToFromFront.getMessage(), messageToFromFront.getBytes(), null, null, 0);
                            try {
                                int frameSize = 4194304; // ~8MB
                                String msg = this.gson.toJson(message1);
                                int totalSize = msg.length();
                                int frames = (int) Math.ceil((double) totalSize / frameSize);

                                for (int i = 0; i < totalSize; i += frameSize) {
                                    int endIndex = Math.min((i + frameSize), totalSize);
                                    String substring = msg.substring(i, endIndex);

                                    this.session.getBasicRemote().sendObject(new Message(new MessageFrame(substring, i / frameSize, frames)));
                                }
                            } catch (Throwable throwable) {
                                String stacktrace = ExceptionUtils.getStackTrace(throwable);
                                CustomPrinter.printErr(stacktrace);
                            }
                        }
                    }
                }
            }
        };
        threadReadFromServer.put(session.getId(),new Thread(myRunnable));
        threadReadFromServer.get(session.getId()).start();
    }


    @OnMessage
    public void onMessage(Session session, Message message) {
        if(!usersTimes.containsKey(session))
        {
            usersTimes.put(session,System.currentTimeMillis());
        }else{
            usersTimes.replace(session,System.currentTimeMillis());
        }
        try {
            IpPublicAddressDto ipPublicAddressDto = AutoDiscoveryHttp.getIpAndPort();
            UserAuthorizationDto userAuthorizationDto = httpClientAuthorization.validateAndDeserializeJWT2(new TokenDto(token, ipPublicAddressDto.getIp() + ":" + ipPublicAddressDto.getPort()));
        } catch (Exception unavailable) {
            try {
                Message message2 = new Message("server", unavailable.getMessage(), false, null, false, 503, 0);
                this.session.getBasicRemote().sendObject(new Message(new MessageFrame(this.gson.toJson(message2), 0, 1)));
                this.session.close();
            } catch (Exception ignored) {
                // e de la faptul ca nu am utilizator real salvat
            }
        }

        if (users.containsKey(session)) {
            message.setFrom(this.id);
            if (message.getIdCode() == null) {
                TCPManagement.addMessageInQueue(message);
            } else {
                String id = message.getIdCode();

                String url = "http://100.24.146.38:8081/api/cluster/code/intern/" + id;

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz")
                        .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        CodeRecordRequest codeRecordRequest = this.gson.fromJson(response.body(), CodeRecordRequest.class);
                        for (CodeMessage codeMessage : codeRecordRequest.getCodeMessageList()) {
                            codeMessage.setType(codeRecordRequest.getTypeRun());
                        }
                        codeRecordRequest.getCodeMessageList().addAll(message.getListOfCodes());
                        Message message1 = new Message(this.id, null, false, codeRecordRequest.getCodeMessageList(), false, 0, codeRecordRequest.getTypeRun());
                        TCPManagement.addMessageInQueue(message1);
                    } else if (response.statusCode() == 404) {
                        CustomPrinter.printErr("Endpoint-ul nu a fost gasit.");
                    } else {
                        CustomPrinter.printErr("Eroare: " + response.statusCode());
                    }
                } catch (Exception e) {
                    String stacktrace = ExceptionUtils.getStackTrace(e);
                    CustomPrinter.printErr(stacktrace);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        CustomPrinter.printWarning("______________CLOSE___________");
        usersTimes.remove(session);
        if(isStopWithOutClose) {
            CustomPrinter.printWarning("Remove token!");
            CustomPrinter.printNormal(users.get(session));
            httpClientAuthorization.removeToken(users.get(session));
        }
        users.remove(session);

        if (threadReadFromServer.get(session.getId()) != null)
            threadReadFromServer.get(session.getId()).stop();
        TCPManagement.interruptExecutionForClient(this.id);

        int closeCode = closeReason.getCloseCode().getCode();
        String reasonPhrase = closeReason.getReasonPhrase();
        CustomPrinter.printInfo("The user with ID " + session.getId() + " has disconnected. " +
                "Close code: " + closeCode + ", Reason phrase: " + reasonPhrase);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        CustomPrinter.printErr(throwable.getMessage());
    }


    public static void addMessageToSendFront(MessageToFront message) {
        messageFrontBackend.get(message.getIdUser()).add(message);
    }

    public static  void closeById(String id){

    }
}