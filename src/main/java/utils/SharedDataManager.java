package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class SharedDataManager {
    private static final String BASE_PATH = "target/test-data/";

    static {
        new File(BASE_PATH).mkdirs(); // Create folder if it doesn't exist
    }

    // Save data to file
    public static void save(String fileName, SharedData data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(Paths.get(BASE_PATH + fileName + ".json").toFile(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load data from file
    public static SharedData load(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Paths.get(BASE_PATH + fileName + ".json").toFile(), SharedData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

