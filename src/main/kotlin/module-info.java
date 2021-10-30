module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.swing;
    requires kotlin.stdlib.jdk7;
    requires org.apache.commons.io;

    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}