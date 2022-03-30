package com.example.zp;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.Modeles.Model;
import com.example.Modeles.TblDatas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField add_ACC;

    @FXML
    private TextField add_FIO;

    @FXML
    private TextField add_SUMM;

    @FXML
    private Button add_save;
    @FXML
    private Button cencel;

    Model model = new Model();
    TblDatas subjects = new TblDatas();
    @FXML
    void initialize() {
        add_save.setOnAction(actionEvent->{
            subjects.setM_FIO(add_FIO.getText());
            subjects.setM_ACC(add_ACC.getText());
            subjects.setM_SUMM(add_SUMM.getText());
            try {
                model.addSubject(subjects);
                model.switchScene(actionEvent, new MainController(), "Zp.fxml");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cencel.setOnAction(event -> {
            try {
                model.switchScene(event, new MainController(), "ZP.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}

