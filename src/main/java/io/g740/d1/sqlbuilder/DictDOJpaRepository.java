package io.g740.d1.sqlbuilder;

import io.g740.d1.dict.entity.DictDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 19:02
 * @description :
 */
public interface DictDOJpaRepository extends JpaRepository<DictDO>{

    List<DictDO> findByFieldId(String fieldId);

}
