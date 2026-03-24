package com.example.httpnode.service;

import com.example.httpnode.dto.IpPublicAddressDto;
import com.example.httpnode.exception.ConflictException;
import com.example.httpnode.exception.InternalServerError;
import com.example.httpnode.exception.InvalidFieldException;
import com.example.httpnode.other.CustomPrintStreamError;
import com.example.httpnode.other.CustomPrinter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class IPAddressManagementService {
    @Autowired
    private ParameterValidateService parameterValidateService;

    public boolean findByIpAndPort(String ip, int port)  {
        String endpointUrl = "http://address-component-microservice:9030/api/cluster/ipAddress/ipAndPort";
        String authorizationHeader = "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz";

        try {
            String fullUrl = String.format("%s/%s/%d", endpointUrl, ip, port);
            CustomPrinter.printWarning(fullUrl);
            URL url = new URL(fullUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Authorization", authorizationHeader);

            int responseCode = connection.getResponseCode();
            CustomPrinter.printInfo("Raspuns existenta adresa IP:  " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                CustomPrinter.printSuccess(response.toString());
                connection.disconnect();
                return true;
            } else {
                CustomPrinter.printErr("Cererea a esuat cu codul de eroare: " + responseCode);
                connection.disconnect();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            return false;
        }
    }
}
