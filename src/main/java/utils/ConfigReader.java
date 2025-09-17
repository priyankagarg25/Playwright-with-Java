package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;

    //This is used to raed from properties files and returns properties object
    public Properties initProp() {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("./src/test/resources/config/config.properties");
            properties.load(fileInputStream);
        } catch (Exception e) {
            System.out.println("Unable to read Properties file.");
        }
        return properties;
    }
    public static String getProperty(String key) {
        ConfigReader configReader = new ConfigReader();
        Properties properties = configReader.initProp();    // Reading from config properties file
        return properties.getProperty(key);
    }


}