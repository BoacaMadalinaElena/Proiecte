package org.example.execution_node.conexion_udp.udp_multicast.system_info;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.conexion_tcp.ConnectionTCPManager;
import org.example.execution_node.other.CustomPrinter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class SystemInfoManager {
    private final String osName;

    public SystemInfoManager() {
        osName = System.getProperty("os.name");
        CustomPrinter.printInfo("OsName: " + osName);
    }

    public InfoToSend getSystemInfo() {
        InfoToSend result = null;
        switch (osName) {
            case "Linux":
                result = getSystemInfoLinux();
                break;
            case "Windows":
                result = getSystemInfoWindows();
                break;
            default:
                CustomPrinter.printErr("There is no method for this system!");
                System.exit(-1);
        }
        return result;
    }

    private InfoToSend getSystemInfoLinux() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            ConnectionTCPManager connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            long totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
            long freePhysicalMemorySize = osBean.getFreePhysicalMemorySize();
            int availableProcessors = osBean.getAvailableProcessors();
            double systemLoadAverage = osBean.getSystemLoadAverage();

                    return new InfoToSend(totalPhysicalMemorySize,
                            freePhysicalMemorySize,
                            connectionTCPManager.getIpAddress(),
                            connectionTCPManager.getPort(),
                            availableProcessors,
                            systemLoadAverage
                    );
        } catch (IOException ioException) {
            String stacktrace = ExceptionUtils.getStackTrace(ioException);
            CustomPrinter.printErr(stacktrace);
        }
        return null;
    }

    private InfoToSend getSystemInfoWindows() {
        return null;
    }
}
