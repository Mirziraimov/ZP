package com.example.zp;

import com.example.Modeles.TblDatas;
import javafx.scene.control.TableCell;

public class TableCellFormat extends TableCell<TblDatas, String> {

    public void updateItem(String item, boolean empty){
        super.updateItem(item, empty);
        this.setText(item);

    }
}
