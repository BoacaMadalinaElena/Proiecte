package org.example;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.httpClient.AutoDiscoveryHttp;
import org.example.other.CustomPrinter;
import org.example.sendLogs.SendLogs;
import org.example.tcp_management.TCPManagement;
import org.example.udp_receiver.HeartbeatReceiver;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class ConnectionManagerNodeInitializer implements ServletContextListener  {
    private AutoDiscoveryHttp autoDiscoveryHttp;
    Thread threadAutoDiscovery;
    HeartbeatReceiver heartbeatReceiver;
    Thread thread;

    Thread tcpManagementThread;
    ScheduledExecutorService executor;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            AutoDiscoveryHttp.getPublicIP();

            this.autoDiscoveryHttp = new AutoDiscoveryHttp();
            autoDiscoveryHttp.reset(AutoDiscoveryHttp.getPublicIP() + ":8080");

            this.threadAutoDiscovery = new Thread(this.autoDiscoveryHttp);
            threadAutoDiscovery.start();

            this.heartbeatReceiver = HeartbeatReceiver.getHeartbeatReceiver();
            this.thread = new Thread(heartbeatReceiver);
            thread.start();

            CustomPrinter.printSuccess("Start server UDP!");
            TCPManagement tcpManagement = TCPManagement.getInstance(true);  // true static, false dinamic
            this.tcpManagementThread = new Thread(tcpManagement);
            tcpManagementThread.start();

            this.executor = Executors.newScheduledThreadPool(1);
            SendLogs sendLogs = new SendLogs();
            executor.scheduleAtFixedRate(sendLogs, 0, 2, TimeUnit.MINUTES);
        }catch (Exception ex){
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
            System.exit(-1);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        CustomPrinter.printErr("Stop server UDP!");
        CustomPrinter.printErr("Stop server TCP!");
        try {
            this.autoDiscoveryHttp.unRegister();
        }catch (Exception ignored){
        }
    }
}
