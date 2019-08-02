package ai.sparklabinc.d1.vo;

import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 16:03
 * @description :
 */
public class DsKeyQueryVO {

    private List<DsKeyQueryFormSettingVO> form;
    private List<DsKeyQueryTableSettingVO> table;

    public List<DsKeyQueryFormSettingVO> getForm() {
        return form;
    }

    public void setForm(List<DsKeyQueryFormSettingVO> form) {
        this.form = form;
    }

    public List<DsKeyQueryTableSettingVO> getTable() {
        return table;
    }

    public void setTable(List<DsKeyQueryTableSettingVO> table) {
        this.table = table;
    }

    public DsKeyQueryVO() {
    }

    public DsKeyQueryVO(List<DsKeyQueryFormSettingVO> form, List<DsKeyQueryTableSettingVO> table) {
        this.form = form;
        this.table = table;
    }
}
