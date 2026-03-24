package org.example.httpClient;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.httpClient.dto.ErrorDto;
import org.example.httpClient.dto.TokenDto;
import org.example.httpClient.dto.UserAuthorizationDto;
import org.example.httpClient.exceptions.ServiceUnavailable;
import org.example.other.CustomPrinter;
import org.example.web_socket_management.dto.IpPublicAddressDto;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class HTTPClientAuthorization {
    private static final HashSet<String> tokens = new HashSet<>();

    public UserAuthorizationDto validateAndDeserializeJWT(TokenDto token) throws Exception {
        Gson gson = new Gson();
        String urlCampus = "100.24.146.38:8081";
        URL url = new URL("http://" + urlCampus + "/api/cluster/user/validateJWS");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String pass = "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";
        conn.setRequestProperty("Authorization", pass);


        try (OutputStream outputStream = conn.getOutputStream()) {
            byte[] input = gson.toJson(token).getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
            return null;
        }

        // read response
        UserAuthorizationDto userAuthorizationDto;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            CustomPrinter.printNormal("Response from server: " + response);
            userAuthorizationDto = gson.fromJson(response.toString(), UserAuthorizationDto.class);
            tokens.add(token.getToken());
        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);

            if (stacktrace.contains("HTTP response code: 503")) {
                CustomPrinter.printErr("HTTP response code: 503");
                throw new ServiceUnavailable(gson.toJson(new ErrorDto(
                        "Nu poate exista decât o conexiune de execuție deschisă de un utilizator la un moment dat. Vă rugăm să executați codul doar după ce ați terminat de executat codul anterior.",
                        "Only one execution connection can be open per user at a time. Please execute code only after you have finished executing the previous code."
                )));
            }
            if (stacktrace.contains("HTTP response code: 403")) {
                throw new ServiceUnavailable(gson.toJson(new ErrorDto(
                        "Activitatea acestui cont a fost blocată din cauza unor acțiuni suspecte. Vă rugăm să reveniți.",
                        "The activity of this account has been blocked due to suspicious actions. Please try again later."
                )));
            }
            CustomPrinter.printErr(stacktrace);
            return null;
        }

        conn.disconnect();
        return userAuthorizationDto;
    }

    public UserAuthorizationDto validateAndDeserializeJWT2(TokenDto token) throws Exception {
        Gson gson = new Gson();
        String urlCampus = "100.24.146.38:8081";
        URL url = new URL("http://" + urlCampus + "/api/cluster/user/validateJWSMessage");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String pass = "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";
        conn.setRequestProperty("Authorization", pass);

        try (OutputStream outputStream = conn.getOutputStream()) {
            byte[] input = gson.toJson(token).getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
            return null;
        }

        // read response
        UserAuthorizationDto userAuthorizationDto;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            CustomPrinter.printNormal("Response from server: " + response);
            userAuthorizationDto = gson.fromJson(response.toString(), UserAuthorizationDto.class);
            tokens.add(token.getToken());
        } catch (Exception  ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);

            if (stacktrace.contains("HTTP response code: 403")) {
                throw new ServiceUnavailable(gson.toJson(new ErrorDto(
                        "Activitatea acestui cont a fost blocată din cauza unor acțiuni suspecte. Vă rugăm să reveniți.",
                        "The activity of this account has been blocked due to suspicious actions. Please try again later."
                )));
            }
            return null;
        }

        conn.disconnect();
        return userAuthorizationDto;
    }

    public void removeToken(String token) {
        try {
            Gson gson = new Gson();
            String urlCampus = "100.24.146.38:8081";
            URL url = new URL("http://" + urlCampus + "/api/cluster/user/logoutJWS");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String pass = "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";
            conn.setRequestProperty("Authorization", pass);

            try (OutputStream outputStream = conn.getOutputStream()) {
                IpPublicAddressDto ipPublicAddressDto = AutoDiscoveryHttp.getIpAndPort();
                byte[] input = gson.toJson(new TokenDto(token,ipPublicAddressDto.getIp() + ":" + ipPublicAddressDto.getPort())).getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
            }

            // read response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                CustomPrinter.printNormal("Response from server (remove): " + response);
                CustomPrinter.printInfo(token + " disconected!");
                tokens.remove(token);
            } catch (Exception ignored) {
            }
            conn.disconnect();
        } catch (Exception ignored) {

        }
        try{
            Thread.sleep(5);
        }catch (Exception ignored){

        }
    }
}
