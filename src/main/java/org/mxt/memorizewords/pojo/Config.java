package org.mxt.memorizewords.pojo;

public class Config {
    private String url = "http://localhost:11434/api/chat";
    private String model = "gemma3:4b";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
