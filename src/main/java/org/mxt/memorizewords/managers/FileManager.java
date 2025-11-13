package org.mxt.memorizewords.managers;

import org.mxt.memorizewords.pojo.Word;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static List<String> fileList;
    public static void init(){
        fileList = new ArrayList<>();

        File dir = new File("Wordlist");
        File[] files = dir.listFiles();
        if (files != null) {
            for(File file : files){
                fileList.add(file.getName().replace(".txt", ""));
            }
        }
    }
    // 获取全部单词表
    public static List<String> getAllWordLists(){
        return fileList;
    }
    // 获取单词表的全部单词
    public static List<Word> getAllWords(String fileName){
        File file = new File("WordList" + File.separator + fileName + ".txt");
        List<Word> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            for (String line : lines) {
                list.add(new Word(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
