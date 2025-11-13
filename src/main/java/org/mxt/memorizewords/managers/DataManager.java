package org.mxt.memorizewords.managers;

import org.mxt.memorizewords.pojo.Word;
import org.mxt.memorizewords.pojo.data.WordListData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {
    public static Map<String, WordListData> WordListDataMap = new HashMap<>();
    public static Map<String, Map<String,List<String>>> WrongWordsMap = new HashMap<>();
    public static void init() throws IOException {
        File DataDir = new File("Data");
        if(!DataDir.exists()){
            DataDir.mkdir();
        }
        WordListDataMap = new HashMap<>();
        File wordListDataFile = new File(DataDir, "WordListData.txt");
        if(wordListDataFile.exists()) {
            List<String> lines = Files.readAllLines(wordListDataFile.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.contains(",")) {
                    String[] str = line.split(",");
                    WordListDataMap.put(str[0], new WordListData(Integer.parseInt(str[1]), str[2]));
                }
            }
        }

        WrongWordsMap = new HashMap<>();
        File wrongWordsFile = new File(DataDir, "WrongWords.txt");
        if(wrongWordsFile.exists()) {
            List<String> lines = Files.readAllLines(wrongWordsFile.toPath(), StandardCharsets.UTF_8);
            for(String line : lines){
                if(line.contains(";")) {
                    String[] str = line.split(";");
                    Map<String,List<String>> map = WrongWordsMap.getOrDefault(str[0],new TreeMap<>());
                    if(str.length == 3){
                        map.put(str[1],new ArrayList<>(Arrays.asList(str[2].split(","))));
                    }else{
                        map.put(str[1],new ArrayList<>());
                    }
                    WrongWordsMap.put(str[0],map);
                }
            }
        }
    }
    public static void completeList(String name,Set<String> wrongList){
        WordListData data = WordListDataMap.getOrDefault(name,new WordListData());
        data.num++;
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        data.date = date.format(formatter);
        WordListDataMap.put(name, data);
        saveWordListData();

        Map<String,List<String>> map = WrongWordsMap.getOrDefault(name,new TreeMap<>());
        map.put(data.date,new ArrayList<>(wrongList));
        WrongWordsMap.put(name,map);
        saveWrongWords();
    }
    private static void saveWordListData(){
        File DataDir = new File("Data");
        if(!DataDir.exists()){
            DataDir.mkdir();
        }
        File wordListDataFile = new File(DataDir, "WordListData.txt");
        StringBuilder data = new StringBuilder();
        for(String name: WordListDataMap.keySet()){
            WordListData wordListData = WordListDataMap.get(name);
            data.append(name).append(",").append(wordListData.num).append(",").append(wordListData.date).append("\n");
        }
        try (FileWriter writer = new FileWriter(wordListDataFile, false)) {
            writer.write(data.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void saveWrongWords(){
        File DataDir = new File("Data");
        if(!DataDir.exists()){
            DataDir.mkdir();
        }
        File wrongWordsFile = new File(DataDir, "WrongWords.txt");
        StringBuilder data = new StringBuilder();
        for(String list: WrongWordsMap.keySet()){
            Map<String,List<String>> map = WrongWordsMap.get(list);
            map.forEach((k,v)->{
                StringBuilder words = new StringBuilder();
                if(!v.isEmpty()) {
                    for (String word : v) {
                        words.append(word).append(",");
                    }
                    words.deleteCharAt(words.length() - 1);
                }
                data.append(list).append(";").append(k).append(";").append(words).append("\n");
            });
        }
        try (FileWriter writer = new FileWriter(wrongWordsFile, false)) {
            writer.write(data.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
