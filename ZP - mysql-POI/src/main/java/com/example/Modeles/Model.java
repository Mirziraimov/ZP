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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class Model {
    private final DB_Connection connectionDataBase = new DB_Connection();
    private PreparedStatement prSt = null;
    private Parent root = null;
    private Stage stage = null;
    private Scene scene = null;
    private FXMLLoader fxmlLoader = null;
    private SXSSFWorkbook wb = null;

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
    private void editTable(TblDatas trblrcds, String COL_NAME, String dataType) throws SQLException, ClassNotFoundException {
        String update = "UPDATE "+Const.TBL_RECORD_LIST+" SET "+COL_NAME+"=?"+" WHERE "+Const.COL_DB_PP+"=?";
        prSt = connectionDataBase.getDBConnection().prepareStatement(update);
        switch (dataType) {
            case "FIO":
                prSt.setString(1, trblrcds.getM_FIO().toUpperCase(Locale.ROOT));
                break;
            case "ACC":
                prSt.setString(1, trblrcds.getM_ACC());
                break;
            case "SUMM":
                prSt.setString(1, trblrcds.getM_SUMM());
                break;
        }
        prSt.setString(2, trblrcds.getM_PP().toString());
        prSt.executeUpdate();
    }

    public void editCell(TableColumn<TblDatas, String> objc, String typeRows){
        objc.setCellFactory(TextFieldTableCell.forTableColumn());
        objc.setOnEditCommit((TableColumn.CellEditEvent<TblDatas, String>event)->{
            TablePosition<TblDatas, String> pos = event.getTablePosition();
            String newFullName = event.getNewValue();
            int row = pos.getRow();
            TblDatas tblRcrds = event.getTableView().getItems().get(row);
            tblRcrds.setM_PP(tblRcrds.getM_PP());
            switch (typeRows){
                case "FIO":
                    tblRcrds.setM_FIO(newFullName);

                    try {
                        editTable(tblRcrds, Const.COL_DB_FIO, "FIO");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case "ACC":
                    tblRcrds.setM_ACC(newFullName);
                    try {
                        editTable(tblRcrds, Const.COL_DB_ACC, "ACC");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    tblRcrds.setM_SUMM(newFullName);
                    try {
                        editTable(tblRcrds, Const.COL_DB_SUMM, "SUMM");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
    }

    public void addSubject(TblDatas records) throws SQLException, ClassNotFoundException {
        if(!records.getM_FIO().isEmpty() && !records.getM_ACC().isEmpty() && !records.getM_SUMM().isEmpty()){
            String insert = "INSERT INTO "+Const.TBL_RECORD_LIST +"("+Const.COL_DB_FIO+", "+
                    Const.COL_DB_ACC+", "+Const.COL_DB_SUMM+") VALUES (UPPER(?), ?, ?)";
            prSt = connectionDataBase.getDBConnection().prepareStatement(insert);
            prSt.setString(1, records.getM_FIO());
            prSt.setString(2, fillByZero(records.getM_ACC())+records.getM_ACC());
            prSt.setString(3, records.getM_SUMM().replace(",", "."));
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
        String trancate = "DELETE FROM " + tblName;
        prSt = connectionDataBase.getDBConnection().prepareStatement(trancate);
        prSt.execute();
    }
    public String fillByZero(String acc){
        String zero = "";
        if(acc.length()<12){
            zero = "0";
            for(int i = 1; i < 12 - acc.length(); i++){
                zero = zero + "0";
            }
        }
        return zero;
    }
    private void insertByParseFile(TblDatas tblDatas) throws SQLException, ClassNotFoundException{
        String insert = "INSERT INTO "+Const.TBL_RECORD_LIST+" ("+Const.COL_DB_FIO+", "+Const.COL_DB_ACC+", "+Const.COL_DB_SUMM+") VALUES (?, ?, ?)";
        prSt = connectionDataBase.getDBConnection().prepareStatement(insert);
        prSt.setString(1, tblDatas.getM_FIO());
        prSt.setString(2, tblDatas.getM_ACC());
        prSt.setString(3, tblDatas.getM_SUMM());
        prSt.executeUpdate();
    }

    public void fileParse(String filePath) throws IOException, SQLException, ClassNotFoundException {
//        String substr = filePath.length() > format ? filePath.substring(0, format) : filePath;
        TblDatas tblRcds = new TblDatas();
        String format = filePath.substring(filePath.lastIndexOf(".")+1);
        switch (format.toUpperCase(Locale.ROOT)){
            case "XLSX":
                XSSFWorkbook wb = new XSSFWorkbook(filePath);
                Sheet sheet = wb.getSheetAt(0);
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()){
                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    while (cellIterator.hasNext()){
                        Cell currentCell = cellIterator.next();
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                switch (currentCell.getCellType()) {
                                    case STRING:
                                        tblRcds.setM_FIO(currentCell.getStringCellValue().toUpperCase());
                                    break;
                                }
                                break;
                            case 1:
                                switch (currentCell.getCellType()) {
                                    case STRING:
                                        tblRcds.setM_ACC(currentCell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        tblRcds.setM_ACC(fillByZero(String.valueOf(currentCell.getNumericCellValue()).replace(".0", ""))+String.valueOf(currentCell.getNumericCellValue()).replace(".0", ""));
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 2:
                                switch (currentCell.getCellType()) {
                                    case NUMERIC:
                                        tblRcds.setM_SUMM(String.valueOf(currentCell.getNumericCellValue()));
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    insertByParseFile(tblRcds);
                }
            break;
            case "XLS":
                System.out.println(format);
            break;
            default:
            break;
        }
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
                "DATA_FORMAT = EXT_ID;NAME;F_TUBNUM;ACCOUNT;SUM" );
        ResultSet records = this.getTableData();
        int i = 1;
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(3);
        Cell cell = row.createCell(3);
        cell.setCellValue("Шдпшя");
        while(records.next()){
            pw.println(i+";"+records.getString(Const.COL_DB_FIO)+";"+records.getString(Const.COL_DB_ACC)+";"+records.getString(Const.COL_DB_ACC)+";"+records.getString(Const.COL_DB_SUMM)+";");
            i++;
        }
        pw.close();
    }
}
