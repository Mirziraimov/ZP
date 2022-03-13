package com.example.Modeles;
import com.example.DB.DB_Connection;
import com.example.DB.Const;
import javafx.scene.Node;

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

    public void createFile(String dirName, String textField) throws SQLException, ClassNotFoundException, IOException {
        ResultSet resultSet = this.exportFileSettings();
        String titleFile = null;
        String format = null;
        while (resultSet.next()) {
            titleFile = resultSet.getString(Const.COL_DB_TITLE);
            format = resultSet.getString(Const.COL_DB_FORMAT );
        }
        File file = new File(dirName + "/" + titleFile + ".csv");
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