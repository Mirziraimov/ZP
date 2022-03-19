package com.example.zp;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.Modeles.Model;
import com.example.Modeles.TblDatas;
import eu.hansolo.tilesfx.skins.BarChartTileSkin;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private Scene scene;
    private Stage stage;
    private Parent root;
    @FXML
    void initialize() {
        add_save.setOnAction(actionEvent->{
            subjects.setM_FIO(add_FIO.getText());
            subjects.setM_ACC(add_ACC.getText());
            subjects.setM_SUMM(add_SUMM.getText());
            try {
                model.addSubject(subjects);
                root = FXMLLoader.load(getClass().getResource("ZP.fxml"));
                stage = (Stage)add_save.getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(true);
                stage.show();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void SwitchPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("ZP.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

}

