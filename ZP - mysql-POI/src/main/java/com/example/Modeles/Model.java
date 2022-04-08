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

    public ResultSet getTableData() throws SQLException, ClassNotFoundException {
        String select = "SELECT * FROM " + Const.TBL_RECORD_LIST;
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(select);
        return prSt.executeQuery();
    }

    public void switchScene(ActionEvent event, Object className, String FXMLname) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(className.getClass().getResource(FXMLname));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
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
        StringBuilder zero = new StringBuilder();
        if(acc.length()<12){
            zero = new StringBuilder("0");
            zero.append("0".repeat(12 - acc.length() - 1));
        }
        return zero.toString();
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
                                if (currentCell.getCellType() == CellType.STRING) {
                                    tblRcds.setM_FIO(currentCell.getStringCellValue().toUpperCase());
                                }
                                break;
                            case 1:
                                switch (currentCell.getCellType()) {
                                    case STRING -> tblRcds.setM_ACC(currentCell.getStringCellValue());
                                    case NUMERIC -> tblRcds.setM_ACC(fillByZero(String.valueOf(currentCell.getNumericCellValue()).replace(".0", "")) + String.valueOf(currentCell.getNumericCellValue()).replace(".0", ""));
                                    default -> {
                                    }
                                }
                                break;
                            case 2:
                                if (currentCell.getCellType() == CellType.NUMERIC) {
                                    tblRcds.setM_SUMM(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    this.insertByParseFile(tblRcds);
                }
                wb.close();
            }
            case "XLS" -> {
                HSSFWorkbook wb2 = new HSSFWorkbook(new FileInputStream(filePath));
                Sheet sheet2 = wb2.getSheetAt(0);
                for (Row currentRow : sheet2) {
                    for (Cell currentCell : currentRow) {
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    tblRcds.setM_FIO(currentCell.getStringCellValue().toUpperCase());
                                }
                                break;
                            case 1:
                                switch (currentCell.getCellType()) {
                                    case STRING -> tblRcds.setM_ACC(currentCell.getStringCellValue());
                                    case NUMERIC -> tblRcds.setM_ACC(fillByZero(String.valueOf(currentCell.getNumericCellValue()).replace(".0", "")) + String.valueOf(currentCell.getNumericCellValue()).replace(".0", ""));
                                    default -> {
                                    }
                                }
                                break;
                            case 2:
                                if (currentCell.getCellType() == CellType.NUMERIC) {
                                    tblRcds.setM_SUMM(String.valueOf(currentCell.getNumericCellValue()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    this.insertByParseFile(tblRcds);
                }
                wb2.close();
            }
        }
    }

    public String printFile(){
        File file = new File(Const.DIR_NAME);
        Date date = new Date();
        String newDirName = new SimpleDateFormat("ddMM yyyy HHmmss").format(date);
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
        return prSt.executeQuery();
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
    private String totalCount() throws  SQLException, ClassNotFoundException{
        String select = "SELECT count(*) totalcount FROM " +Const.TBL_RECORD_LIST;
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(select);
        ResultSet resultSet = prSt.executeQuery();
        String res = null;
        while(resultSet.next()) {
            res = resultSet.getString("totalcount");
        }
        return res;
    }

    private void updateCounterFile(int i) throws SQLException, ClassNotFoundException {
        String update = "UPDATE " +Const.TBL_SETTING +" SET " + Const.COL_DB_COUNTER + "=?";
        this.prSt = connectionDataBase.getDBConnection().prepareStatement(update);
        this.prSt.setString(1, String.valueOf(i));
        this.prSt.executeUpdate();
    }

    private String md5(String filePath){
        String md5 = "";
        try(InputStream is = Files.newInputStream(Paths.get(filePath+".CSV.gpg"))){
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    private void addSheet(CellRangeAddress cellRangeAddress, Sheet sheet, int rowNumber, int cellNumber, String textCell, XSSFCellStyle cellStyle, String alignment){
        Row row;Cell cell;
        sheet.addMergedRegion(cellRangeAddress);
        row = sheet.createRow(rowNumber);
        cell = row.createCell(cellNumber);
        cell.setCellValue(textCell);
        switch (alignment) {
            case "CENTER" -> {
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(cellStyle);
            }
            case "RIGHT" -> {
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                cell.setCellStyle(cellStyle);
            }
            case "LEFT" -> {
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellStyle(cellStyle);
            }
        }
    }

    private void decodeFile(String dirName, String cmdLine) throws IOException {
        String CMDFilePath = dirName+"\\dep.bat";
        File CMDFile = new File(CMDFilePath);
        CMDFile.createNewFile();

        PrintWriter writeBATFile = new PrintWriter(CMDFile);
        writeBATFile.println("cd .\\"+dirName);
        writeBATFile.println(cmdLine);
        writeBATFile.println("del/q *.CSV");
        writeBATFile.close();

        Runtime.getRuntime().exec("cmd /c "+".\\\""+CMDFilePath+"\"");
    }

    public void createFile(String dirName, String textField) throws SQLException, ClassNotFoundException, IOException, NoSuchAlgorithmException {
        ResultSet resultSet = this.exportFileSettings();
        String titleFile = null;
        String format = null;
        String key = null;
        int counterFile = 1;
        while (resultSet.next()) {
            titleFile = resultSet.getString(Const.COL_DB_TITLE);
            format = resultSet.getString(Const.COL_DB_FORMAT );
            counterFile = resultSet.getInt(Const.COL_DB_COUNTER);
            key = resultSet.getString(Const.COL_DB_KEY);
            counterFile++;
        }
        this.updateCounterFile(counterFile);

        String filename = dirName+"/"+format+counterFile+"_UPD."+ format;
        File file = new File(filename +".CSV");
        file.createNewFile();

        PrintWriter pw = new PrintWriter(file);
        if(textField.isEmpty()){
            textField = "Зарплата";
        }
        pw.println("FILE_COMMENT = "+textField+"\n" +
                "DATA_DELIMITER = \";\"\n" +
                "DATA_DECIMAL_SYMBOL = \".\"\n" +
                "FACTORY_NAME = "+titleFile+"\n"+
                "FACTORY_ID = 12345\n" +
                "FACTORY_ACCOUNT = 47422810500000012345\n" +
                "TOTAL_COUNT = "+this.totalCount()+"\n"+
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

        if(file.exists()) {
            this.decodeFile(dirName, "gpg -r " + key + " -e " + format + counterFile + "_UPD." + format + ".CSV");
        }

        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Ведомость");
        XSSFCellStyle cellStyle = wb.createCellStyle();

        HashMap<String, Object> properties = new HashMap<>();
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

        this.addSheet(new CellRangeAddress(0,1,0,3), sheet, 0, 0, titleFile, cellStyle, "CENTER");
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        this.addSheet(new CellRangeAddress(2,2,0,3), sheet, 2, 0, textField, cellStyle, "CENTER");

        this.addSheet(new CellRangeAddress(j,j,0,3), sheet, j, 0, "Итог:", cellStyle, "LEFT");
        RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(j,j,0,3), sheet);

        this.addSheet(new CellRangeAddress(j+2,j+2,0,3), sheet, j+2, 0, "Руководитель", cellStyle, "LEFT");
        RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(j+2,j+2,0,3), sheet);

        this.addSheet(new CellRangeAddress(j+4,j+4,0,3), sheet, j+4, 0, "Глав. бухгалтер", cellStyle, "LEFT");
        RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(j+4,j+4,0,3), sheet);

        this.addSheet(new CellRangeAddress(j+6,j+6,0,3), sheet, j+6, 0, "Контрольная сумма", cellStyle, "CENTER");

        this.addSheet(new CellRangeAddress(j+7,j+7,0,3), sheet, j+7, 0, this.md5(filename), cellStyle, "CENTER");

        FileOutputStream fos = new FileOutputStream(dirName + "/" + format+counterFile+"_UPD."+ format +".xlsx");
        wb.write(fos);
        fos.close();
    }
}
