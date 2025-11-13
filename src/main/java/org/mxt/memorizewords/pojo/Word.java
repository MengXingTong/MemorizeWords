package org.mxt.memorizewords.pojo;

public class Word {
    public String word;
    public String meanings;
    public Word(String line){
        String[] str = line.split(":");
        word = str[0];
        meanings = str[1];
    }
}
