package io.g740.d1.poi;

import java.util.List;

/**
 * @author: zhangxw
 * @date: 2018/2/26 14:41
 */
public class RowUnit {

    /**
     * 一行数据的索引,从0计算
     */
    private Integer rowIndex;

    /**
     * 一行数据的值, 注意顺序
     */
    private List<String> cellValues;

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public List<String> getCellValues() {
        return cellValues;
    }

    public void setCellValues(List<String> cellValues) {
        this.cellValues = cellValues;
    }
}
