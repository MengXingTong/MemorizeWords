module org.mxt.memorizewords {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.management;
    requires java.net.http;
    requires org.kordamp.ikonli.material2;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;

    opens org.mxt.memorizewords to javafx.fxml;
    exports org.mxt.memorizewords;
    exports org.mxt.memorizewords.pages;
    opens org.mxt.memorizewords.pages to javafx.fxml;
    opens org.mxt.memorizewords.pojo to com.fasterxml.jackson.databind;
}