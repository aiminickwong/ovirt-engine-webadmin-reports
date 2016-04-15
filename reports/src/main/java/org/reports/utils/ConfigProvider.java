package org.reports.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;

public class ConfigProvider {
    private static Logger log = org.slf4j.LoggerFactory.getLogger(ConfigProvider.class);
    private static Properties config = null;

    public static final String SDK_BASE_URL = "engineSdkBaseUrl";
    public static final String SDK_USER = "engineSdkUser";
    public static final String SDK_PASSWORD = "engineSdkPassword";
    public static final String QUERY_INTERVAL_M = "queryIntervalM";

    public static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            try {
                config.load(new FileReader("/etc/ovirt-engine-webadmin-reports/ovirt-engine-webadmin-reports.properties"));
            } catch (IOException ex) {
                log.error("error reading config file.", ex);
            }
        }
        return config;
    }
}