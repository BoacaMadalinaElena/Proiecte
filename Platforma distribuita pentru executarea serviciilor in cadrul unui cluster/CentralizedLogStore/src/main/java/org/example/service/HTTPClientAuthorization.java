package org.example.service;


import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.example.dto.TokenDto;
import org.example.dto.UserAuthorizationDto;
import org.springframework.stereotype.Service;

@Service
public class HTTPClientAuthorization {

    public UserAuthorizationDto validateAndDeserializeJWT(TokenDto token) {
        try {
            Gson gson = new Gson();
            HttpURLConnection conn = getHttpURLConnection();

            try (OutputStream outputStream = conn.getOutputStream()) {
                byte[] input = gson.toJson(token).getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                System.err.println(stacktrace);
                return null;
            }

            UserAuthorizationDto userAuthorizationDto;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response);
                userAuthorizationDto = gson.fromJson(response.toString(), UserAuthorizationDto.class);

            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                System.err.println(stacktrace);
                return null;
            }
            conn.disconnect();
            return userAuthorizationDto;
        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            System.err.println(stacktrace);
            return null;
        }
    }

    private static HttpURLConnection getHttpURLConnection() throws IOException {
        String urlCampus = "100.24.146.38:8081";
        URL url = new URL("http://" + urlCampus + "/api/cluster/user/validateJWSLogs");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String pass = "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";
        conn.setRequestProperty("Authorization", pass);
        return conn;
    }
}
