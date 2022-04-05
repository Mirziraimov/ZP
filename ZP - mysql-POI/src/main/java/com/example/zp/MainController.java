package com.example.zp;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.DB.Const;
import com.example.Modeles.TblDatas;
import com.example.Modeles.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;

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
    private TableColumn<TblDatas, String> r_tabid;
    @FXML
    private TableView<TblDatas> tbl_view;
    @FXML
    private Button zp_insrt;
    @FXML
    private TextArea zp_note;

    File selectedFilev;
    private final ObservableList<TblDatas> datas = FXCollections.observableArrayList();
    private final Model model = new Model();
    private TblDatas tblItems;
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
            if(selectedFilev != null){
                try {
                    model.fileParse(selectedFilev.toString());
                    datas.clear();
                    displayRecords();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
        displayRecords();

        delete.setOnAction(ActionEvent->{
            tblItems = tbl_view.getSelectionModel().getSelectedItem();
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

        switchtoadd.setOnAction(event -> {
            try {
                model.switchScene(event, new AddController(), "ADDFORM.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void displayRecords(){
        tblItems = tbl_view.getSelectionModel().getSelectedItem();
        r_tabid.setCellValueFactory(new PropertyValueFactory<>("m_PP"));
        r_PP.setCellValueFactory(new PropertyValueFactory<>("m_counter"));
        r_PP.setMaxWidth(40);
        r_FIO.setCellValueFactory(new PropertyValueFactory<>("m_FIO"));
        r_FIO.setMinWidth(200);
        r_FIO.setMaxWidth(300);
        model.editCell(r_FIO,"FIO");
        r_ACC.setCellValueFactory(new PropertyValueFactory<>("m_ACC"));
        r_ACC.setMinWidth(100);
        r_ACC.setCellFactory(TextFieldTableCell.forTableColumn());
        model.editCell(r_ACC,"ACC");
        r_SUMM.setCellValueFactory(new PropertyValueFactory<>("m_SUMM"));
        model.editCell(r_SUMM,"SUM");

        try {
            ResultSet getItems = model.getTableData();
            int i = 1;
            while(getItems.next()){
                datas.add(new TblDatas(i, getItems.getString(Const.COL_DB_FIO), getItems.getString(Const.COL_DB_ACC), getItems.getString(Const.COL_DB_SUMM), getItems.getInt(Const.COL_DB_PP)));
                i++;
            }
            tbl_view.setItems(datas);
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



}
