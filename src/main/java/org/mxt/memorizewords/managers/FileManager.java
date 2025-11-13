package org.mxt.memorizewords.managers;

import org.mxt.memorizewords.pojo.Word;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileManager {
    private static List<String> fileList;
    public static void init(){
        fileList = new ArrayList<>();

        File dir;
        String prop = System.getProperty("java.class.path");
        // IDE/编译器环境运行（classes 目录）
        if (prop.contains("target/classes")) {
            dir = new File("Wordlist");;
        }else{
            dir = new File("src/main/resources/WordList");
        }
        // 不存在需要从jar导出文件
        if(!dir.exists()){
            dir.mkdir();
            String jarPath = FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            try (JarFile jar = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    // 只处理 WordList 目录下的文件
                    if (name.startsWith("WordList/") && !entry.isDirectory()) {
                        InputStream is = jar.getInputStream(entry);
                        Path outPath = Paths.get(dir.getAbsolutePath(), name.substring("WordList/".length()));
                        Files.createDirectories(outPath.getParent());
                        Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
                        is.close();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
        File dir;
        String prop = System.getProperty("java.class.path");
        // IDE/编译器环境运行（classes 目录）
        if (prop.contains("target/classes")) {
            dir = new File("Wordlist");;
        }else{
            dir = new File("src/main/resources/WordList");
        }
        File file = new File(dir,fileName + ".txt");
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
