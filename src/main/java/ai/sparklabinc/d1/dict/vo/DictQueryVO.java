package ai.sparklabinc.d1.dict.vo;

import ai.sparklabinc.d1.dict.entity.DictDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/6 9:45
 * @description :
 */
public class DictQueryVO {

    private String fieldDomain;

    private String fieldItem;

    private List<DictDO> dictList;

    public String getFieldDomain() {
        return fieldDomain;
    }

    public void setFieldDomain(String fieldDomain) {
        this.fieldDomain = fieldDomain;
    }

    public String getFieldItem() {
        return fieldItem;
    }

    public void setFieldItem(String fieldItem) {
        this.fieldItem = fieldItem;
    }

    public List<DictDO> getDictList() {
        return dictList;
    }

    public void setDictList(List<DictDO> dictList) {
        this.dictList = dictList;
    }
}
