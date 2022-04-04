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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Model {
    private final DB_Connection connectionDataBase = new DB_Connection();
    private PreparedStatement prSt = null;
    private Parent root = null;
    private Stage stage = null;
    private Scene scene = null;
    private FXMLLoader fxmlLoader = null;

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
            case "FIO" -> prSt.setString(1, trblrcds.getM_FIO().toUpperCase(Locale.ROOT));
            case "ACC" -> prSt.setString(1, trblrcds.getM_ACC());
            case "SUMM" -> prSt.setString(1, trblrcds.getM_SUMM());
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
            switch (typeRows) {
                case "FIO" -> {
                    tblRcrds.setM_FIO(newFullName);
                    try {
                        this.editTable(tblRcrds, Const.COL_DB_FIO, "FIO");
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                case "ACC" -> {
                    tblRcrds.setM_ACC(newFullName);
                    try {
                        this.editTable(tblRcrds, Const.COL_DB_ACC, "ACC");
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                default -> {
                    tblRcrds.setM_SUMM(newFullName);
                    try {
                        this.editTable(tblRcrds, Const.COL_DB_SUMM, "SUMM");
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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
                Const.COL_DB_ACC+"=? AND "+Const.COL_DB_SUMM+"=? AND "+Const.COL_DB_PP+"=?";
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(deleteSubject);
        this.prSt.setString(1, subject.getM_FIO());
        this.prSt.setString(2, subject.getM_ACC());
        this.prSt.setString(3, subject.getM_SUMM());
        this.prSt.setString(4, subject.getM_PP().toString());
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
                zero += "0";
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
        switch (format.toUpperCase(Locale.ROOT)) {
            case "XLSX" -> {
                XSSFWorkbook wb = new XSSFWorkbook(filePath);
                Sheet sheet = wb.getSheetAt(0);
                for (Row currentRow : sheet) {
                    for (Cell currentCell : currentRow) {
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
                                        tblRcds.setM_ACC(fillByZero(String.valueOf(currentCell.getNumericCellValue()).replace(".0", "")) + String.valueOf(currentCell.getNumericCellValue()).replace(".0", ""));
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
                    this.insertByParseFile(tblRcds);
                }
            }
            case "XLS" -> {
                HSSFWorkbook wb2 = new HSSFWorkbook(new FileInputStream(filePath));
                Sheet sheet2 = wb2.getSheetAt(0);
                for (Row currentRow : sheet2) {
                    for (Cell currentCell : currentRow) {
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
                                        tblRcds.setM_ACC(fillByZero(String.valueOf(currentCell.getNumericCellValue()).replace(".0", "")) + String.valueOf(currentCell.getNumericCellValue()).replace(".0", ""));
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
                    this.insertByParseFile(tblRcds);
                }
            }
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

    private String md5(String filePath){
        String md5 = "";
        try(InputStream is = Files.newInputStream(Paths.get(filePath+".CSV"))){
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public void createFile(String dirName, String textField) throws SQLException, ClassNotFoundException, IOException, NoSuchAlgorithmException {
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

        String filename = dirName+"/"+format+counterFile+"_UPD."+ format;
        File file = new File(filename +".CSV");
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
        while(records.next()){
            pw.println(i+";"+records.getString(Const.COL_DB_FIO)+";"+records.getString(Const.COL_DB_ACC)+";"+records.getString(Const.COL_DB_ACC)+";"+records.getString(Const.COL_DB_SUMM)+";");
            i++;
        }
        pw.close();

        String CMDFilePath = dirName+"/dep.bat";
        File CMDFile = new File(CMDFilePath);
        CMDFile.createNewFile();



        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Ведомость");
        XSSFCellStyle cellStyle = wb.createCellStyle();

        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(CellUtil.BORDER_TOP, BorderStyle.THIN);
        properties.put(CellUtil.BORDER_BOTTOM, BorderStyle.THIN);
        properties.put(CellUtil.BORDER_LEFT, BorderStyle.THIN);
        properties.put(CellUtil.BORDER_RIGHT, BorderStyle.THIN);

        Row row;Cell cell;

        ResultSet records2 = this.getTableData();
        int j = 3, rownum = 1;

        while(records2.next()){
            row = sheet.createRow(j);
            cell = row.createCell(0);
            cell.setCellValue(rownum);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(0, true);
            CellUtil.setCellStyleProperties(cell, properties);

            cell = row.createCell(1);
            cell.setCellValue(records2.getString(Const.COL_DB_FIO));
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(1, true);
            CellUtil.setCellStyleProperties(cell, properties);

            cell = row.createCell(2);
            cell.setCellValue(records2.getString(Const.COL_DB_ACC));
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(2, true);
            CellUtil.setCellStyleProperties(cell, properties);

            cell = row.createCell(3);
            cell.setCellValue(records2.getString(Const.COL_DB_SUMM));
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(3, true);
            CellUtil.setCellStyleProperties(cell, properties);
            j++;
            rownum++;
        }
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,3));
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Название организации!");
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        CellUtil.setCellStyleProperties(cell, properties);
        cell.setCellStyle(cellStyle);

        sheet.addMergedRegion(new CellRangeAddress(2,2,0,3));
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("Назначение");
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        CellUtil.setCellStyleProperties(cell, properties);
        cell.setCellStyle(cellStyle);

        CellRangeAddress cellRangeAddressR = new CellRangeAddress(j+2,j+2,0,3);
        sheet.addMergedRegion(cellRangeAddressR);
        row = sheet.createRow(j+2);
        cell = row.createCell(0);
        cell.setCellValue("Руководитель");
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddressR, sheet);

        CellRangeAddress cellRangeAddressG = new CellRangeAddress(j+4,j+4,0,3);
        sheet.addMergedRegion(cellRangeAddressG);
        row = sheet.createRow(j+4);
        cell = row.createCell(0);
        cell.setCellValue("Глав. бухгалтер");
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddressG, sheet);

        sheet.addMergedRegion(new CellRangeAddress(j+6,j+6,0,3));
        row = sheet.createRow(j+6);
        cell = row.createCell(0);
        cell.setCellValue("Контрольная сумма");
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(cellStyle);

        sheet.addMergedRegion(new CellRangeAddress(j+7,j+7,0,3));
        row = sheet.createRow(j+7);
        cell = row.createCell(0);
        cell.setCellValue(this.md5(filename));
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        CellUtil.setCellStyleProperties(cell, properties);
        cell.setCellStyle(cellStyle);

        FileOutputStream fos = new FileOutputStream(dirName + "/" + format+counterFile+"_UPD."+ format +".xlsx");
        wb.write(fos);
        fos.close();

    }
}
