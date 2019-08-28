package io.g740.d1.dict.controller;

import io.g740.d1.dict.dto.DictDTO;
import io.g740.d1.dict.vo.DictQueryVO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dict.service.DictService;
import io.g740.d1.dto.PageResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
@Api("dict manage controller")
public class DictManageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictManageController.class);

    @Autowired
    private DictService dictService;

    @GetMapping("/domain")
    @ResponseBody
    @ApiOperation("query")
    public PageResultDTO<DictQueryVO> query(@RequestParam(required = false, value = "field_domain") String domain,
                                            @RequestParam(required = false, value = "field_item") String item,
                                            @RequestParam(required = false, value = "field_value") String value,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {


        DictDTO dictDTO = new DictDTO();
        dictDTO.setFieldDomain(domain);
        dictDTO.setFieldItem(item);
        dictDTO.setFieldValue(value);
        PageRequest pageable =new PageRequest(page, size);
        long offset = pageable.getPageSize()*pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        return this.dictService.query(dictDTO, offset, pageSize);
    }


    @PostMapping("/domain")
    @ResponseBody
    @ApiOperation("add dict List ")
    public void addDictList(@RequestBody  List<DictDTO> dictDTOS) throws Exception {
        // 前端只会传fieldDomain 、fieldItem 、 fieldValue 、 fieldLabel 四个字段 ;前端判断了value的重复值问题
        this.dictService.addDictList(dictDTOS);

    }

    @PostMapping("/domain-update")
    @ResponseBody
    @ApiOperation("batch update")
    public void batchUpdate(@RequestBody List<DictDO> dictDOList) throws Exception {
        this.dictService.batchUpdate(dictDOList);
    }


    @DeleteMapping("/domain")
    @ResponseBody
    @ApiOperation("delete ")
    public void deleteDomain(@RequestBody DictDTO dictDTO) throws Exception {
        // 前端只会传fieldDomain 、fieldItem
        this.dictService.deleteDomain(dictDTO);
    }



    @PostMapping("/value")
    @ResponseBody
    @ApiOperation("add base value config ")
    public void addBaseValue(@RequestBody  DictDTO dictDTO) throws Exception {
        // 前端只会传fieldDomain 、fieldItem 、 fieldValue 、 fieldLabel 四个字段 ;前端判断了value的重复值问题
        this.dictService.addBaseDict(dictDTO);

    }


    @PutMapping("/value")
    @ResponseBody
    @ApiOperation("update base value config")
    public void updateBaseValue(@RequestBody  DictDTO dictDTO) throws Exception {
        // 前端只会传fieldDomain 、fieldItem 、 fieldValue 、 fieldLabel 四个字段 ;前端判断了value的重复值问题
        this.dictService.updateBaseDict(dictDTO);

    }

    @DeleteMapping("/value")
    @ResponseBody
    @ApiOperation("delete")
    public void delete(@RequestBody List<String> idList) throws Exception {
        this.dictService.batchDelete(idList);
    }

}
