package org.mxt.memorizewords.pages;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.mxt.memorizewords.MainApplication;
import org.mxt.memorizewords.managers.DataManager;
import org.mxt.memorizewords.managers.FileManager;
import org.mxt.memorizewords.pojo.data.WordListData;

public class ListSelectPage extends HBox {
    public ListSelectPage() {
        super();
        ListSelectPage thisPage = this;
        // 左侧滚动框，放默认WordList
        VBox leftBox = new VBox(){{
            prefWidthProperty().bind(thisPage.widthProperty().subtract(20).divide(2));
        }};
        leftBox.setSpacing(10);
        leftBox.setPadding(new Insets(10,0,0,10));
        ScrollPane leftPane = new ScrollPane(leftBox){{
            for(String fileName: FileManager.getAllWordLists()){
                WordListData data = DataManager.WordListDataMap.getOrDefault(fileName, new WordListData());
                leftBox.getChildren().add(getComponent(fileName,data.num,data.date));
            }
        }};
        // 右侧滚动框，放自定义WordList
        VBox rightBox = new VBox();
        rightBox.setSpacing(10);
        rightBox.setPadding(new Insets(10,0,0,0));
        ScrollPane rightPane = new ScrollPane(rightBox){{
            WordListData data = DataManager.WordListDataMap.getOrDefault("test", new WordListData());
            rightBox.getChildren().add(getComponent("test",data.num,data.date));

        }};

        getChildren().addAll(leftPane, new Separator(Orientation.VERTICAL), rightPane);
    }
    private HBox getComponent(String text, int num, String date) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        Button button = new Button(text);
        button.setOnMouseClicked(event -> {
           MemoryPage memoryPage = new MemoryPage(text);
           MainApplication.panes.put("刷单词",memoryPage);
           MainApplication.changePane("刷单词");
        });
        Label numLabel = new Label(num + "次");
        numLabel.setPadding(new Insets(0,0,0,10));
        Label dateLabel = new Label(date);
        dateLabel.setPadding(new Insets(0,0,0,10));
        box.getChildren().addAll(button,numLabel,dateLabel);
        return box;
    }
}
