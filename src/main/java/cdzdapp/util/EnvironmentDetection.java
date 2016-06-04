package cdzdapp.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EnvironmentDetection {
    private EnvironmentDetection() {}

    public static String detectEnvironment() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();

            if (classExists("com.gargoylesoftware.htmlunit.WebClient")) {
                if ("cdzd-ci".equals(hostname)) {
                    return "test-ci";
                }
                return "test-local";
            }

            if (hostname.startsWith("cdzd-app")) {
                return "prod";
            }

            return "local";
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Unable to detect environment", e);
        }
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
