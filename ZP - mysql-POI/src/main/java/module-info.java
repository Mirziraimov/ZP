//в данном случае у нас пакетный модуль com.example.
//но можно пользоваться не пакатным модулем
open module com.example.zp { // открыть для всего проекта
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    exports com.example.zp;
//    opens com.example.zp to javafx.fxml;

}