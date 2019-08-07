package ai.sparklabinc.d1.dict.dto;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(DictQueryVO.class);

    private String domain;

    private String item;

    private List<DictDO> dictList;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public List<DictDO> getDictList() {
        return dictList;
    }

    public void setDictList(List<DictDO> dictList) {
        this.dictList = dictList;
    }
}
