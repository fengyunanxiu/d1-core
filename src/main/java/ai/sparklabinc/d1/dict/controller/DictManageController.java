package ai.sparklabinc.d1.dict.controller;

import ai.sparklabinc.d1.dict.dto.DictDTO;
import ai.sparklabinc.d1.dict.dto.DictQueryVO;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.dict.service.DictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/6 11:13
 * @description :
 */
@RestController
@RequestMapping("/d1/dict/manage")
public class DictManageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictManageController.class);

    @Autowired
    private DictService dictService;

    @GetMapping("")
    @ResponseBody
    public Collection<DictQueryVO> query(@RequestParam(required = false) String domain,
                                         @RequestParam(required = false) String item,
                                         @RequestParam(required = false) String value,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

        DictDTO dictDTO = new DictDTO();
        dictDTO.setDomain(domain);
        dictDTO.setItem(item);
        dictDTO.setValue(value);
        PageRequest pageable = PageRequest.of(page, size);
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();
        return this.dictService.query(dictDTO, offset, pageSize);
    }

    @PostMapping("")
    @ResponseBody
    public List<DictDO> add(@RequestBody List<DictDO> dictDOList) throws Exception {
        return this.dictService.batchInsert(dictDOList);
    }

    @DeleteMapping("")
    @ResponseBody
    public void delete(@RequestBody List<String> idList) throws Exception {
        this.dictService.batchDelete(idList);
    }

    @PutMapping("")
    @ResponseBody
    public void update(@RequestBody List<DictDO> dictDOList) throws Exception {
        this.dictService.batchUpdate(dictDOList);
    }

}
