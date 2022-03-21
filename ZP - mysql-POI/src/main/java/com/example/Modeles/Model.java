package com.example.Modeles;
import com.example.DB.DB_Connection;
import com.example.DB.Const;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Model {
    private final DB_Connection connectionDataBase = new DB_Connection();
    private PreparedStatement prSt = null;
    private Parent root = null;
    private Stage stage = null;
    private Scene scene = null;
    FXMLLoader fxmlLoader = null;


    public ResultSet getTableData() throws SQLException, ClassNotFoundException {
        String select = "SELECT * FROM " + Const.TBL_RECORD_LIST;
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(select);
        ResultSet resultSet = prSt.executeQuery();
        return resultSet;
    }

    /*public ObservableList getTableData(ObservableList<TblDatas> datas) throws SQLException, ClassNotFoundException {
        String select = "SELECT * FROM " + Const.TBL_RECORD_LIST;
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(select);
        ResultSet resultSet = prSt.executeQuery();
        while(resultSet.next()){
//            System.out.println(resultSet.getString(Const.COL_DB_FIO));
            datas.add(new TblDatas(resultSet.getInt(Const.COL_DB_PP), resultSet.getString(Const.COL_DB_FIO), resultSet.getString(Const.COL_DB_ACC), resultSet.getString(Const.COL_DB_SUMM)));
        }
        return datas;
    }*/
    public void switchScene(ActionEvent event, Object className, String FXMLname) throws IOException {
        fxmlLoader = new FXMLLoader(className.getClass().getResource(FXMLname));
        root = fxmlLoader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void editCell(TableColumn<TblDatas, String> objc){
        objc.setCellFactory(TextFieldTableCell.forTableColumn());
        objc.setOnEditCommit((TableColumn.CellEditEvent<TblDatas, String>event)->{
            TablePosition<TblDatas, String> pos = event.getTablePosition();
            String newFullName = event.getNewValue();
            int row = pos.getRow();
            TblDatas tblRcrds = event.getTableView().getItems().get(row);
            tblRcrds.setM_FIO(newFullName);
            System.out.println("Нажата "+row);
        });
    }

    public void addSubject(TblDatas records) throws SQLException, ClassNotFoundException {
        if(!records.getM_FIO().isEmpty() && !records.getM_ACC().isEmpty() && !records.getM_SUMM().isEmpty()){
            if(records.getM_ACC().length()<12){
                System.out.println();
            }
            String insert = "INSERT INTO "+Const.TBL_RECORD_LIST +"("+Const.COL_DB_FIO+", "+
                    Const.COL_DB_ACC+", "+Const.COL_DB_SUMM+") VALUES (?, ?, ?)";
            prSt = connectionDataBase.getDBConnection().prepareStatement(insert);
            prSt.setString(1, records.getM_FIO());
            prSt.setString(2, records.getM_ACC());
            prSt.setString(3, records.getM_SUMM());
            prSt.executeUpdate();
        }
    }
    public void deleteUser(TblDatas subject) throws SQLException, ClassNotFoundException {
        String deleteSubject = "DElETE FROM "+Const.TBL_RECORD_LIST+" WHERE "+Const.COL_DB_FIO+"=? AND "+
                Const.COL_DB_ACC+"=? AND "+Const.COL_DB_SUMM+"=?";
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(deleteSubject);
        this.prSt.setString(1, subject.getM_FIO());
        this.prSt.setString(2, subject.getM_ACC());
        this.prSt.setString(3, subject.getM_SUMM());
        this.prSt.execute();
    }
    public void trancateTbl(String tblName) throws SQLException, ClassNotFoundException {
        String trancate = "TRUNCATE TABLE " + tblName;
        prSt = connectionDataBase.getDBConnection().prepareStatement(trancate);
        prSt.execute();
    }

    public void fileParse(String filePath){
//        String substr = filePath.length() > format ? filePath.substring(0, format) : filePath;
        String format = filePath.substring(filePath.lastIndexOf(".")+1);
        if(format.equals("xlsx")){
            System.out.println(format);
        }
        System.out.println(format);
    }

    public String printFile(){
        File file = new File(Const.DIR_NAME);
        Date date = new Date();
        String newDirName = new SimpleDateFormat("ddMMYYYY HHmmss").format(date);
        File dir = new File(file+"/"+newDirName);
        if(file.exists()){
            if (!dir.exists()){
                dir.mkdir();
            }
        }else{
            dir.mkdirs();
        }
        return dir.toString();
    }

    private ResultSet exportFileSettings() throws SQLException, ClassNotFoundException {
        String select = "SELECT * FROM " + Const.TBL_SETTING;
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(select);
        ResultSet resultSet = prSt.executeQuery();
        return resultSet;
    }

    private String totalSumm() throws  SQLException, ClassNotFoundException{
        String select = "SELECT sum("+Const.COL_DB_SUMM+") totalsum FROM " +Const.TBL_RECORD_LIST;
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(select);
        ResultSet resultSet = prSt.executeQuery();
        String res = null;
        while(resultSet.next()) {
            res = resultSet.getString("totalsum");
        }
        return res;
    }

    private void updateCounterFile(int i) throws SQLException, ClassNotFoundException {
        String update = "UPDATE " +Const.TBL_SETTING +" SET " + Const.COL_DB_COUNTER + "=?";
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(update);
        this.prSt.setString(1, String.valueOf(i));
        this.prSt.executeUpdate();
    };

    public void createFile(String dirName, String textField) throws SQLException, ClassNotFoundException, IOException {
        ResultSet resultSet = this.exportFileSettings();
        String titleFile = null;
        String format = null;
        int counterFile = 1;
        while (resultSet.next()) {
            titleFile = resultSet.getString(Const.COL_DB_TITLE);
            format = resultSet.getString(Const.COL_DB_FORMAT );
            counterFile = resultSet.getInt(Const.COL_DB_COUNTER);
            counterFile++;
        }
        this.updateCounterFile(counterFile);
        File file = new File(dirName + "/" + format+counterFile+"_UPD."+ format +".CSV");
        file.createNewFile();
        PrintWriter pw = new PrintWriter(file);
        pw.println("FILE COMMENT = "+textField+"\n" +
                "DATA_DELIMIMITER = \";\"\n" +
                "DATA_DECIMAL_SYMBOL = \".\"\n" +
                "FATORY_NAME = "+titleFile+"\n"+
                "FACTORY_ID = 12345\n" +
                "FACTORY_ACCOUNT = 13246578\n" +
                "TOTAL_SUM = "+this.totalSumm()+"\n"+
                "DATA_CODEPAGE = 1251\n" +
                "DATA_FORMAT = EXT_ID;NAME;F_TUBNUM;ACCOUNT;SUM");
        ResultSet records = this.getTableData();
        int i = 1;
        while(records.next()){
            pw.println(i+";"+records.getString(Const.COL_DB_FIO)+";"+records.getString(Const.COL_DB_ACC)+";"+records.getString(Const.COL_DB_ACC)+";"+records.getString(Const.COL_DB_SUMM)+";");
            i++;
        }
        pw.close();
    }
}
