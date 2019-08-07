package ai.sparklabinc.d1.vo;

import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 16:03
 * @description :
 */
public class DfKeyQueryVO {

    private List<DfKeyQueryFormSettingVO> form;
    private List<DfKeyQueryTableSettingVO> table;

    public List<DfKeyQueryFormSettingVO> getForm() {
        return form;
    }

    public void setForm(List<DfKeyQueryFormSettingVO> form) {
        this.form = form;
    }

    public List<DfKeyQueryTableSettingVO> getTable() {
        return table;
    }

    public void setTable(List<DfKeyQueryTableSettingVO> table) {
        this.table = table;
    }

    public DfKeyQueryVO() {
    }

    public DfKeyQueryVO(List<DfKeyQueryFormSettingVO> form, List<DfKeyQueryTableSettingVO> table) {
        this.form = form;
        this.table = table;
    }
}
