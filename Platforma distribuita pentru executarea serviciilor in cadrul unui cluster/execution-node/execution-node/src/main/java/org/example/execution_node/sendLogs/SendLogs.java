package org.example.execution_node.sendLogs;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.other.CustomPrinter;
import org.example.execution_node.sendLogs.model.LogsMessage;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

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
        String csvFile = "logs_execution_" + newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")) + ".csv";

        boolean ok = true;
        for (String s : readFile) {
            if (s.equals(newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")))) {
                ok = false;
            }
        }
        if (ok) {
            CustomPrinter.printWarning("Read file: "+ "logs_execution_"+ newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")));
            readFile.add(newDateTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")));
            CustomPrinter.setCurrentIdFile(CustomPrinter.getCurrentIdFile() + 1);

            File file = new File(csvFile);

            if (file.exists()) {
                try {
                    CSVReader csvReader = new CSVReader(new FileReader(csvFile));
                    String[] nextRecord;
                    List<LogsMessage> logsMessageList = new ArrayList<>();
                    while ((nextRecord = csvReader.readNext()) != null) {
                        LogsMessage logsMessage = new LogsMessage(null, nextRecord[0], nextRecord[1], nextRecord[2], nextRecord[3], nextRecord[4]);
                        logsMessageList.add(logsMessage);
                    }

                    String url = "http://100.24.146.38:8070/api/cluster/logsExecutionNode";
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz");

                    HttpEntity<List<LogsMessage>> entity = new HttpEntity<>(logsMessageList, headers);
                    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(url, entity, Void.class);
                    CustomPrinter.printInfo("Status send logs: " + responseEntity.getStatusCode().toString());

                    if(responseEntity.getStatusCode() == HttpStatus.OK){
                        Files.delete(Path.of(csvFile));
                    }
                    csvReader.close();
                } catch (Exception ex) {
                    CustomPrinter.printErr(ex.getMessage());
                }
            } else {
                CustomPrinter.printInfo("Fisierul: " + csvFile + " nu exista!");
            }
        }
    }
}
