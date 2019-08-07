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

    private String fDomain;

    private String fItem;

    private List<DictDO> dictList;


    public String getFDomain() {
        return fDomain;
    }

    public void setFDomain(String fDomain) {
        this.fDomain = fDomain;
    }

    public String getFItem() {
        return fItem;
    }

    public void setFItem(String fItem) {
        this.fItem = fItem;
    }

    public List<DictDO> getDictList() {
        return dictList;
    }

    public void setDictList(List<DictDO> dictList) {
        this.dictList = dictList;
    }
}
