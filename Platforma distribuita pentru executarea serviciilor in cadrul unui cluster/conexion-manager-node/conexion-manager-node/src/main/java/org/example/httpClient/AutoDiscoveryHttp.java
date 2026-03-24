package org.example.httpClient;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.other.CustomPrintStreamError;
import org.example.other.CustomPrinter;
import org.example.web_socket_management.dto.IpPublicAddressDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;


public class AutoDiscoveryHttp implements Runnable {
    @Override
    public void run(){
        while (true){
            try {
                this.register();
                try{
                    Thread.sleep(1*60*1000);
                }catch (InterruptedException interruptedException){
                  CustomPrinter.printErr(interruptedException.getMessage());
                }
            }catch (Exception ex){
                CustomPrinter.printErr(ex.getMessage());
                try{
                    Thread.sleep(1*60*1000);
                }catch (InterruptedException interruptedException){
                    CustomPrinter.printErr(interruptedException.getMessage());
                }
            }
        }
    }

    public static IpPublicAddressDto getIpAndPort(){
        try {
            int port = 8080;
            return new IpPublicAddressDto(null,getPublicIP(),port);
        }catch (Exception ex){
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
            System.exit(-1);
        }
        return null;
    }

public static String getPublicIP(){
    StringBuilder out = new StringBuilder();
    try {
        Process process = Runtime.getRuntime().exec("uname -a");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
    } catch (IOException e) {
        e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
        CustomPrinter.printErr("Cerere esuata localAddress is null!");
        System.exit(-1);
    }
    if(out.toString().contains("Debian") || out.toString().contains("Ubuntu")  ){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress localAddress = null;

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                if ("wlo1".equals(networkInterface.getName())) {
                    Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();

                    while (interfaceAddresses.hasMoreElements()) {
                        InetAddress address = interfaceAddresses.nextElement();

                        if (address instanceof Inet4Address) {
                            localAddress = address;
                            break;
                        }
                    }
                    break;
                }
            }
            if (localAddress != null) {
                return  localAddress.getHostName();
            } else {
                CustomPrinter.printErr("Cerere esuata localAddress is null!");
                System.exit(-1);
            }
        } catch (Exception ex) {
            CustomPrinter.printErr(ex.getMessage());
            System.exit(-1);
        }
        CustomPrinter.printErr("Cerere esuata localAddress is null!");
        System.exit(-1);
        return null;
    }else{

        try {
            URL url = new URL("https://api.ipify.org");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String publicIP = response.toString();
                CustomPrinter.printSuccess("Adresa IP publica este: " + publicIP);
                return publicIP;
            } else {
                CustomPrinter.printErr("Cerere esuata: " + responseCode);
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            System.exit(-1);
        }
        CustomPrinter.printErr("Cerere esuata localAddress is null!");
        System.exit(-1);
        return null;
    }
}

    public void register() throws IOException {
        Gson gson = new Gson();
        String urlAddressAndPort = "100.24.146.38:8081";

        URL url = new URL("http://" + urlAddressAndPort + "/api/cluster/ipAddress");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String token = "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";
        conn.setRequestProperty("Authorization", token);

        try (OutputStream outputStream = conn.getOutputStream()) {
            byte[] input = gson.toJson(getIpAndPort()).getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // read response
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            CustomPrinter.printNormal("Response from server (register): " + response);
        }catch (Exception ex){
           CustomPrinter.printErr(ex.getMessage());
        }
        conn.disconnect();
    }

    public boolean unRegister() throws IOException {
        Gson gson = new Gson();

        HttpURLConnection conn = getHttpURLConnection();

        try (OutputStream outputStream = conn.getOutputStream()) {
            byte[] input = gson.toJson(new IpPublicAddressDto(null,getIpAndPort().getIp(), getIpAndPort().getPort())).getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // read response
        IpPublicAddressDto ipPublicAddressDtoResult ;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            CustomPrinter.printNormal("Response from server: " + response);
            ipPublicAddressDtoResult = gson.fromJson(response.toString(), IpPublicAddressDto.class);
        }
        conn.disconnect();

        return ipPublicAddressDtoResult != null;
    }

    private static HttpURLConnection getHttpURLConnection() throws IOException {
        String urlAddressAndPort =   getPublicIP() + ":8081";
        URL url = new URL("http://" + urlAddressAndPort + "/api/cluster/ipAddress");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json");

        String token = "Bearer GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";
        conn.setRequestProperty("Authorization", token);
        return conn;
    }

    public void reset(String ip){
        try {

            URL url = new URL("http://100.24.146.38:8081/api/cluster/user/restart");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz");

            connection.setDoOutput(true);


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = ip.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                CustomPrinter.printSuccess("Request was successful (reset)");
            } else {
                CustomPrinter.printErr("Request failed." + responseCode);
            }
        }catch (Exception ex){
            CustomPrinter.printErr(ex.getMessage());
        }
    }
}
