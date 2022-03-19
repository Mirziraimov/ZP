package com.example.zp;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.DB.Const;
import com.example.Modeles.TblDatas;
import com.example.Modeles.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class MainController{
    @FXML
    private Button download;
    @FXML
    private Button create_file;
    @FXML
    private Button switchtoadd;
    @FXML
    private Button delete;
    @FXML
    private Button clearTbl;
    @FXML
    private Label filename;
    @FXML
    private TableColumn<TblDatas, Integer> r_PP;
    @FXML
    private TableColumn<TblDatas, String> r_FIO;
    @FXML
    private TableColumn<TblDatas, String> r_ACC;
    @FXML
    private TableColumn<TblDatas, String> r_SUMM;
    @FXML
    private TableView<TblDatas> tbl_view;

    @FXML
    private Button zp_insrt;
    @FXML
    private TextArea zp_note;

    File selectedFilev;
    private final ObservableList<TblDatas> datas = FXCollections.observableArrayList();
    private final Model model = new Model();

    private Scene scene;
    private Stage stage;
    private Parent root;

    public void switchAddPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("ADDFORM.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 360, 273);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    void initialize(){
        tbl_view.setPlaceholder(new Label("Для того чтобы заполнить таблицу, загрузите файл!"));
        tbl_view.setEditable(true);
        
        download.setOnAction(actionEvent->{
            FileChooser fc = new FileChooser();
            selectedFilev = fc.showOpenDialog(null);
            if(selectedFilev != null) {
                filename.setText(selectedFilev.getName());
            }
        });

        zp_insrt.setOnAction(actionEvent->{
            filename.setText("..............");
            if(selectedFilev != null) {
                System.out.println(selectedFilev);
                model.fileParse(selectedFilev.toString());
            }
        });

        create_file.setOnAction(actionEvent->{
            String dirName = model.printFile();
            try {
                model.createFile(dirName, zp_note.getText());
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        displayRecords();

        delete.setOnAction(ActionEvent->{
            TblDatas tblItems = tbl_view.getSelectionModel().getSelectedItem();
            if(tblItems != null) {
                try {
                    model.deleteUser(tblItems);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                tbl_view.getItems().removeAll(tbl_view.getSelectionModel().getSelectedItem());
            }
        });

        clearTbl.setOnAction(ActionEvent->{
            try {
                model.trancateTbl(Const.TBL_RECORD_LIST);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            tbl_view.getItems().removeAll(tbl_view.getItems());
        });

    }

    private void displayRecords(){
        r_FIO.setEditable(true);
        r_FIO.setCellFactory(TextFieldTableCell.forTableColumn());
        r_FIO.setOnEditCommit((TableColumn.CellEditEvent<TblDatas, String>event)->{
            TablePosition<TblDatas, String> pos = event.getTablePosition();
            String newFullName = event.getNewValue();
            int row = pos.getRow();
            TblDatas tblRcrds = event.getTableView().getItems().get(row);
            tblRcrds.setM_FIO(newFullName);
            System.out.println("Нажата "+row);
        });
        r_PP.setCellValueFactory(new PropertyValueFactory<>("m_PP"));
        r_PP.setMaxWidth(40);
        r_FIO.setCellValueFactory(new PropertyValueFactory<>("m_FIO"));
        r_FIO.setMinWidth(200);
        r_FIO.setMaxWidth(300);
        r_ACC.setCellValueFactory(new PropertyValueFactory<>("m_ACC"));
        r_ACC.setMinWidth(100);
        r_SUMM.setCellValueFactory(new PropertyValueFactory<>("m_SUMM"));
        try {
            ResultSet getItems = model.getTableData();
            while(getItems.next()){
                datas.add(new TblDatas(getItems.getInt(Const.COL_DB_PP), getItems.getString(Const.COL_DB_FIO), getItems.getString(Const.COL_DB_ACC), getItems.getString(Const.COL_DB_SUMM)));
            }
            tbl_view.setItems(datas);
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



}
