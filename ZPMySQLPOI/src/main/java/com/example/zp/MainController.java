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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;


public class MainController{

    @FXML
    private Button download;

    @FXML
    private Button create_file;

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

    @FXML
    void initialize(){

        tbl_view.setPlaceholder(new Label("Для того чтобы заполнить таблицу, загрузите файл!"));

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

            /*try {
                ResultSet result = model.exportData(new TblDatas());
                while(result.next()){

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
        });

        displayRecords();
    }

    private void displayRecords(){
        r_PP.setCellValueFactory(new PropertyValueFactory<TblDatas, Integer>("m_PP"));
        r_PP.setMaxWidth(40);
        r_FIO.setCellValueFactory(new PropertyValueFactory<TblDatas, String>("m_FIO"));
        r_FIO.setMinWidth(200);
        r_FIO.setMaxWidth(300);
        r_ACC.setCellValueFactory(new PropertyValueFactory<TblDatas, String>("m_ACC"));
        r_ACC.setMinWidth(100);
        r_SUMM.setCellValueFactory(new PropertyValueFactory<TblDatas, String>("m_SUMM"));
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
