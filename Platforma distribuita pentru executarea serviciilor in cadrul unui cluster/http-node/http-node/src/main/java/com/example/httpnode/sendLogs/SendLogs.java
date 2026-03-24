package com.example.httpnode.sendLogs;

import com.example.httpnode.other.CustomPrinter;
import com.example.httpnode.sendLogs.model.LogsMessage;
import com.opencsv.CSVReader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SendLogs implements Runnable {
    List<String> readFile = new ArrayList<>();

    @Override
    public void run() {
        while (CustomPrinter.getLock().get()) {

        }
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm"));
        String modifiedDateTime = formattedDateTime.substring(0, formattedDateTime.length() - 1);
        modifiedDateTime += "0";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");
        LocalDateTime dateTime = LocalDateTime.parse(modifiedDateTime, formatter);

        LocalDateTime newDateTime = dateTime.minusMinutes(10);

        String csvFile = "logs_http_" + newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")) + ".csv";

        boolean ok = true;
        for (String s : readFile) {
            if (s.equals(newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")))) {
                ok = false;
            }
        }
        if (ok) {
            CustomPrinter.printWarning("Read file: " + newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")));
            readFile.add(newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")));
            CustomPrinter.setCurrentIdFile(CustomPrinter.getCurrentIdFile() + 1);

            File file = new File(csvFile);

            if (file.exists()) {
                try {
                    CSVReader csvReader = new CSVReader(new FileReader(csvFile));
                    String[] nextRecord;
                    List<LogsMessage> logsMessageList = new ArrayList<>();
                    while ((nextRecord = csvReader.readNext()) != null) {
                        LogsMessage logsMessage = new LogsMessage(null, nextRecord[0], nextRecord[1], nextRecord[2], nextRecord[3],"IDM");
                        logsMessageList.add(logsMessage);
                    }

                    String url = "http://100.24.146.38:8070/api/cluster/logsHttpNode";

                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz");

                    HttpEntity<List<LogsMessage>> entity = new HttpEntity<>(logsMessageList, headers);

                    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(url, entity, Void.class);

                    CustomPrinter.printInfo(responseEntity.getStatusCode().toString());

                    csvReader.close();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            } else {
                CustomPrinter.printInfo("Fisierul: " + csvFile + " nu exista!");
            }
        }
    }
}
