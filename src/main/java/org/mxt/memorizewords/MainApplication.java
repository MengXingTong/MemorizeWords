package org.mxt.memorizewords;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.mxt.memorizewords.managers.DataManager;
import org.mxt.memorizewords.managers.FileManager;
import org.mxt.memorizewords.managers.SettingManager;
import org.mxt.memorizewords.pages.ListSelectPage;
import org.mxt.memorizewords.pages.SettingPage;
import org.mxt.memorizewords.pages.StatisticsPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {
    public static TabPane tabs;
    static StackPane contentArea;
    public static final Map<String, Pane> panes = new HashMap<>();
    @Override
    public void start(Stage stage) throws IOException {
        // 初始化
        {
            SettingManager.init();
            FileManager.init();
            DataManager.init();
        }

        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());

        BorderPane root = new BorderPane();

        // Tab栏
        tabs = new TabPane();
        Tab tab1 = new Tab("首页");
        Tab tab2 = new Tab("刷单词");
        Tab tab3 = new Tab("统计");
        Tab tab4 = new Tab("设置");
        tabs.getTabs().addAll(tab1, tab2, tab3, tab4);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // 内容界面
        contentArea = new StackPane();
        ListSelectPage page1 = new ListSelectPage();
        VBox page2 = new VBox();
        panes.put("刷单词",page2);
        StatisticsPage page3 = new StatisticsPage();
        SettingPage page4 = new SettingPage();
        panes.put("设置",page3);
        contentArea.getChildren().setAll(page1);

        // 切换界面
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tab1) {
                changePane("首页");
            } else if (newTab == tab2) {
                changePane("刷单词");
            } else if (newTab == tab3) {
                changePane("统计");
            } else if (newTab == tab4) {
                contentArea.getChildren().setAll(page4);
            }
        });
        // 加入布局
        root.setTop(tabs);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("幻梦之界[BDRealm]-背单词 Demo");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changePane(String key){
        if(key.equals("首页")) {
            tabs.getSelectionModel().select(0);
            changePane(new ListSelectPage());
            return;
        }else if(key.equals("刷单词")){
            tabs.getSelectionModel().select(1);
        }else if(key.equals("统计")){
            tabs.getSelectionModel().select(2);
            changePane(new StatisticsPage());
            return;
        }else if(key.equals("设置")){
            tabs.getSelectionModel().select(3);
        }
        contentArea.getChildren().setAll(panes.get(key));
    }
    public static void changePane(Pane pane){
        contentArea.getChildren().setAll(pane);
    }
    public static void addNotification(String text){
        Notification info = new Notification(
                text,
                new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        info.getStyleClass().add(Styles.ELEVATED_1);
        info.getStyleClass().add(Styles.SUCCESS);
        info.setOnClose(e -> {
            Timeline timeline = Animations.fadeOut(info, Duration.millis(500));
            timeline.play();
            timeline.setOnFinished(finish ->{
                contentArea.getChildren().remove(info);
            });
        });
        // 自适应大小
        info.setMaxWidth(250);
        info.setMaxHeight(50);

        StackPane.setAlignment(info, Pos.TOP_RIGHT);

        contentArea.getChildren().add(info);
        // 3s后自动关闭
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            if (contentArea.getChildren().contains(info)) {
                Timeline fade = Animations.fadeOut(info, Duration.millis(500));
                fade.setOnFinished(finish -> contentArea.getChildren().remove(info));
                fade.play();
            }
        });
        delay.play();
    }
}