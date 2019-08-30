package io.g740.d1.sqlbuilder;

import io.g740.d1.dict.entity.DictDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 19:02
 * @description :
 */
public interface DictDOJpaRepository extends JpaRepository<DictDO, String>{

    List<DictDO> findByFieldId(String fieldId);

    List<DictDO> findByGmtCreate(Date gmtCreate);

    List<DictDO> findByGmtCreateAndFieldDomain(Date gmtCreate, String fieldDomain);

    List<DictDO> findByFieldDomainAndFieldItemOrFieldDomainAndFieldItem(String fieldDomain1, String fieldItem1, String fieldDomain2, String fieldItem2);

}
