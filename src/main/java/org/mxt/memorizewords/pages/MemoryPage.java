package org.mxt.memorizewords.pages;

import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.mxt.memorizewords.MainApplication;
import org.mxt.memorizewords.managers.DataManager;
import org.mxt.memorizewords.managers.FileManager;
import org.mxt.memorizewords.managers.SettingManager;
import org.mxt.memorizewords.pojo.Word;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class MemoryPage extends VBox {
    private static final Random random = new Random();
    private final String fileName;
    // 单词
    private final List<Word> words;
    // true-英译中 false-中译英
    private boolean state = true;
    // 序列中的单词
    private List<Word> preWords;
    // 计数系统
    private int correctNum = 0;
    private final Set<String> wrongWords = new HashSet<>();
    private final Label countLabel;
    private final Label passRateLabel;
    // 滚动框
    private final VBox container;
    private WordItem cacheWordItem = null;
    // 输入框
    private final TextField inputField;
    public MemoryPage(String fileName) {
        super();
        this.fileName = fileName;
        words = FileManager.getAllWords(fileName);
        preWords = new ArrayList<>(words);
        // 顶部计数
        {
            countLabel = new Label();
            passRateLabel = new Label();
            updateCalculate();
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox hbox = new HBox(countLabel, spacer, passRateLabel);
            hbox.setPadding(new Insets(5));
            this.getChildren().add(hbox);
        }
        // 滚动框
        {
            // 填充VBox
            container = new VBox(10);
            container.setFillWidth(true);

            ScrollPane scrollPane = new ScrollPane(container);
            scrollPane.setFitToWidth(true); // 滚动内容宽度随窗口变化
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.vvalueProperty().bind(container.heightProperty());
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            this.getChildren().add(scrollPane);
        }
        // 底边栏
        {
            inputField = new TextField();
            inputField.setPromptText("输入新内容...");
            inputField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    try {
                        send(0);
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            Button button = new Button("确认");
            button.setOnAction(e -> {
                try {
                    send(0);
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });

            HBox inputBar = new HBox(10, inputField, button);
            inputBar.setPadding(new Insets(10));
            HBox.setHgrow(inputField, Priority.ALWAYS);
            this.getChildren().add(inputBar);
        }
        nextWord();
    }
    // 行组件
    class WordItem extends HBox {
        private final String word;
        private final String meaning;
        private boolean correct = true;
        private final Label meaningLabel;
        private final Button correctButton;
        private final Label answerLabel;

        private final Button collectButton;
        private final Button changeButton1;
        private final Button changeButton2;

        public WordItem(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
            // 左侧
            {
                Label wordLabel = new Label(correct ?word:meaning);
                meaningLabel = new Label(correct ?meaning:word);
                meaningLabel.setVisible(false);
                // 是否正确
                correctButton = new Button();
                correctButton.setPadding(Insets.EMPTY);
                correctButton.getStyleClass().addAll(Styles.FLAT,Styles.WARNING);
                correctButton.setVisible(false);

                answerLabel = new Label();

                getChildren().addAll(wordLabel, meaningLabel, correctButton, answerLabel);
            }
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            getChildren().add(spacer);
            // 右侧按钮
            {
                collectButton = new Button(null,new FontIcon(FontAwesomeSolid.STAR));
                collectButton.setVisible(false);
                collectButton.getStyleClass().addAll(Styles.WARNING);
                collectButton.setPadding(new Insets(0,2,0,2));

                changeButton1 = new Button(null,new FontIcon(Feather.CHECK));
                changeButton1.setVisible(false);
                changeButton1.getStyleClass().addAll(Styles.SUCCESS);
                changeButton1.setPadding(new Insets(0,2,0,2));
                changeButton1.setOnAction(e -> {
                    setCorrect(true,true);
                    if(cacheWordItem.word.equals(word)) {
                        nextWord();
                        updateCalculate();
                    }
                });

                changeButton2 = new Button(null,new FontIcon(Feather.X));
                changeButton2.setVisible(false);
                changeButton2.getStyleClass().addAll(Styles.DANGER);
                changeButton2.setPadding(new Insets(0,2,0,2));
                changeButton2.setOnAction(e->{
                    setCorrect(false,true);
                });
//                Button wrongButton = new Button("");
                getChildren().addAll(collectButton, changeButton1, changeButton2);
            }
            setSpacing(10);
            setPadding(new Insets(0, 10, 0, 10));
        }
        // 设置回答
        public void answer(String answer) {
            answerLabel.setText("My Answer: " + answer);
        }
        // 设置状态
        void setCorrect(boolean correct, boolean change) {
            // 没有任何变化
            if(meaningLabel.isVisible() && correct == this.correct) {
                return;
            }
            meaningLabel.setVisible(true);
            correctButton.setVisible(true);

            collectButton.setVisible(true);
            changeButton1.setVisible(true);
            changeButton2.setVisible(true);
            // 正确
            if(correct) {
                // 修改为正确，判断是否有意义
                this.correct = true;
                correctButton.setGraphic(new FontIcon(Feather.CHECK));
                correctButton.getStyleClass().addAll(Styles.SUCCESS);
                // 遍历查找更近时间是否有该词汇错误
                boolean check = false;
                for(Node node:container.getChildren()){
                    WordItem wordItem = (WordItem)node;
                    if(wordItem == this){
                        check = true;
                        continue;
                    }
                    if(!check){
                        continue;
                    }
                    // 存在更近时间的历史错误，无意义的修改
                    if(wordItem.word.equals(word) && wordItem.correctButton.isVisible() && !wordItem.correct){
                        return;
                    }
                }
                // 正确+1
                if(preWords.removeIf(theWord -> theWord.word.equals(state?word:meaning))) {
                    correctNum++;
                }
                // 从错词表删除
                if(change) {
                    wrongWords.remove(state?word:meaning);
                }
            }else{
                // 修改为错误,加进错词表
                this.correct = false;
                correctButton.setGraphic(new FontIcon(Feather.X));
                correctButton.getStyleClass().addAll(Styles.DANGER);
                wrongWords.add(state?word:meaning);
                if(change) {
                    correctNum--;
                }
            }
            updateCalculate();
        }
    }
    // 更新计数
    void updateCalculate(){
        countLabel.setText("当前: " + correctNum + "/" + words.size());
        passRateLabel.setText("完全正确: " + correctNum + "   错误: " + wrongWords.size());
    }
    // 下一个单词
    void nextWord(){
        // 结束
        if(preWords.isEmpty()){
            // 是否完成中译英
            if(state) {
                state = false;
                correctNum = 0;
                preWords = new ArrayList<>(words);
            }else{
                // 结束
                DataManager.completeList(fileName,wrongWords);
                MainApplication.panes.put("刷单词",new VBox());
                MainApplication.changePane("首页");
                MainApplication.addNotification("你完成了" + fileName + "!");
                return;
            }
        }
        Word word = preWords.get(random.nextInt(preWords.size()));
        WordItem item = state?new WordItem(word.word, word.meanings):new WordItem(word.meanings, word.word);
        cacheWordItem = item;
        container.getChildren().add(item);
    }
    void send(int num) throws IOException, InterruptedException {
        // 构建 HTTP 客户端
        HttpClient client = HttpClient.newHttpClient();

        Map<String, Object> requestBody = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
//        requestBody.put("model", "qwen3:0.6b");
        requestBody.put("model", SettingManager.config.getModel());
//        requestBody.put("model", "llama3.2:1b");
        requestBody.put("think", false);
        requestBody.put("stream", false);
        List<Map<String, String>> messages = new ArrayList<>();

        requestBody.put("messages", messages);
        messages.add(Map.of("role", "system", "content", "判断用户翻译(中译英/英译中)内容是否完全正确且全面，词性是否正确，仅输出一个整数，范围为1-3。\n用户给出的答案语义完全正确且全面，包含答案中‘;’分隔的所有义项，无遗漏或偏差，词性一致，输出1。\n用户翻译错误、含义不同、词性不同，与答案主要意思不符，输出2。\n用户翻译只包含部分意思或表达不全面，即“部分正确但不完整”，或你认为不好判断，输出3。\n请只输出一个数字，不要输出任何其他文字或符号。\n当前考察内容为'" + cacheWordItem.word + "',答案为'" + cacheWordItem.meaning +"']."));
        messages.add(Map.of("role", "user", "content","用户认为翻译答案应该是'" + inputField.getText() + "',审批用户答案."));
        String json = mapper.writeValueAsString(requestBody);
//        System.out.println(json);
        // 构建请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SettingManager.config.getUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // 发送请求并获取响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());
//        System.out.println(response.body());
        String content = root.path("message").path("content").asText().replace("\n","");
//        System.out.println(content);
        switch (content) {
            case "1" -> cacheWordItem.setCorrect(true, false);
            case "2" -> cacheWordItem.setCorrect(false, false);
            case "3" -> cacheWordItem.setCorrect(true, false);
            default -> {
                if (num >= 3) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("LLM错误");
                    ScrollPane scrollPane = new ScrollPane(new TextArea(response.body()) {{
                        setEditable(false);
                        setWrapText(true);
                    }});
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPrefHeight(250);

                    alert.getDialogPane().setContent(scrollPane);
                    alert.show();
                    return;
                }
                send(num + 1);
                return;
            }
        }
        cacheWordItem.answer(inputField.getText());
        inputField.clear();
        nextWord();
        updateCalculate();
    }
}
