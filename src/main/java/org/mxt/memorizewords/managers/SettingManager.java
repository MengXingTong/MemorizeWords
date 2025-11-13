package org.mxt.memorizewords.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.mxt.memorizewords.pojo.Config;

import java.io.InputStream;

public class SettingManager {
    public static Config config;
    public static void init(){
        InputStream in = SettingManager.class.getClassLoader().getResourceAsStream("config.yml");
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(in, Config.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
