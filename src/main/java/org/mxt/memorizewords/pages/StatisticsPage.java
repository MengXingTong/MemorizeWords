package org.mxt.memorizewords.pages;

import javafx.css.converter.StringConverter;
import javafx.geometry.Insets;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import org.mxt.memorizewords.managers.DataManager;

import java.util.List;
import java.util.stream.IntStream;

public class StatisticsPage extends FlowPane {
    public StatisticsPage() {
        super();
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10));
        for(String WordList:DataManager.WrongWordsMap.keySet()) {
            CategoryAxis x = new CategoryAxis();
            x.setLabel("时间");

            NumberAxis y = new NumberAxis();
            y.setLabel("错词数");

            var line = new XYChart.Series<String, Number>();
            int max = 3;
            for(String key:DataManager.WrongWordsMap.get(WordList).keySet()) {
                List<String> list = DataManager.WrongWordsMap.get(WordList).get(key);
                String shortDate = key.substring(5);
                XYChart.Data<String, Number> data = new XYChart.Data<>(shortDate, list.size());
                if(list.size() > max) {
                    max = list.size();
                }

                line.getData().add(data);
            }
            y.setAutoRanging(false);
            y.setLowerBound(0);
            y.setUpperBound(max + 1);
            y.setTickUnit(1);
            y.setMinorTickCount(0);

            AreaChart<String, Number> chart = new AreaChart<>(x, y);
            chart.setTitle(WordList);
            chart.setMaxWidth(280);
            chart.setMaxHeight(300);
            chart.setLegendVisible(false);
            chart.getData().addAll(line);

            getChildren().addAll(chart);
        }
    }
}
