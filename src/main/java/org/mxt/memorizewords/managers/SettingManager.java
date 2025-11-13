package org.mxt.memorizewords.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.mxt.memorizewords.pojo.Config;

import java.io.File;

public class SettingManager {
    public static Config config;
    public static void init(){
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(new File("config.yml"), Config.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
