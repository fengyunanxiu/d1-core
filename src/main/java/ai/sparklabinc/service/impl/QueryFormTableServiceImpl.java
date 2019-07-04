package ai.sparklabinc.service.impl;

import ai.sparklabinc.component.DsFormTableSettingComponent;
import ai.sparklabinc.constant.DsConstants;
import ai.sparklabinc.dao.DsFormTableSettingDao;
import ai.sparklabinc.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.dto.AssemblyResultDTO;
import ai.sparklabinc.dto.OptionListAndDefaultValDTO;
import ai.sparklabinc.dto.QueryParameterGroupDTO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.service.DsBasicDictionaryService;
import ai.sparklabinc.service.QueryFormTableService;
import ai.sparklabinc.vo.DsKeyQueryFormSettingVO;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import ai.sparklabinc.vo.DsKeyQueryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author : Kingzer
 * @date : 2019-07-02 21:49
 * @description :
 */
@Service
public class QueryFormTableServiceImpl implements QueryFormTableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFormTableServiceImpl.class);

    @Autowired
    private DsFormTableSettingDao dsFormTableSettingDao;

    @Autowired
    private DsBasicDictionaryService dsBasicDictionaryService;

    @Autowired
    private DsFormTableSettingComponent dsFormTableSettingComponent;

    @Autowired
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Override
    public List<DsKeyQueryTableSettingVO> getDsKeyQueryTableSetting(String dataSourceKey) throws SQLException, IOException {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if(dsFormTableSettingDOList == null && dsFormTableSettingDOList.isEmpty()){
            return null;
        }
        return realGetDsKeyQueryTableSetting(dsFormTableSettingDOList);
    }

    @Override
    public  List<DsKeyQueryFormSettingVO> getDsKeyQueryFormSetting(String dataSourceKey) throws SQLException, IOException {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if(dsFormTableSettingDOList == null && dsFormTableSettingDOList.isEmpty()){
            return null;
        }
        return realGetDsKeyQueryFormSetting(dsFormTableSettingDOList);
    }

    @Override
    public DsKeyQueryVO getDsKeyQuerySetting(String dataSourceKey) throws SQLException, IOException {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if(dsFormTableSettingDOList == null && dsFormTableSettingDOList.isEmpty()){
            return null;
        }

        List<DsKeyQueryFormSettingVO> dsKeyQueryFormSettingVOList = realGetDsKeyQueryFormSetting(dsFormTableSettingDOList);
        if(dsKeyQueryFormSettingVOList == null && dsKeyQueryFormSettingVOList.isEmpty()){
            return null;
        }

        List<DsKeyQueryTableSettingVO> dsKeyQueryTableSettingVOList = realGetDsKeyQueryTableSetting(dsFormTableSettingDOList);
        if(dsKeyQueryTableSettingVOList == null && dsKeyQueryTableSettingVOList.isEmpty()){
            return null;
        }
        return new DsKeyQueryVO(dsKeyQueryFormSettingVOList,dsKeyQueryTableSettingVOList);

    }

    @Override
    public AssemblyResultDTO generalQuery(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause) throws Exception {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if (dsFormTableSettingDOList == null && dsFormTableSettingDOList.isEmpty()) {
            throw new Exception(String.format("DataSourceKey not found:%s", dataSourceKey));
        }

        QueryParameterGroupDTO queryParameterGroup = null;
        try {
            queryParameterGroup = this.dsFormTableSettingComponent.transformQueryParameterMap(dataSourceKey,
                    simpleParameters, dsFormTableSettingDOList);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to transfrom query parameter map", dataSourceKey, e);
            throw new Exception(dataSourceKey + "Failed to transfrom query parameter map");
        }

        DsKeyBasicConfigDO dsKeyBasicConfigDO = this.dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dataSourceKey);





        return null;
    }

    private List<DsKeyQueryFormSettingVO> realGetDsKeyQueryFormSetting(List<DsFormTableSettingDO> dsFormTableSettingDOList) throws SQLException, IOException {
        List<DsKeyQueryFormSettingVO> rootDsKeyQueryFormSettingVOList = new LinkedList<>();
        DsKeyQueryFormSettingVO dsKeyQueryFormSettingVO = null;
        for (DsFormTableSettingDO dsFormTableSettingDO : dsFormTableSettingDOList) {
            if(dsFormTableSettingDO.getFormFieldVisible()){
                dsKeyQueryFormSettingVO = new DsKeyQueryFormSettingVO();
                dsKeyQueryFormSettingVO.setDbFieldName(dsFormTableSettingDO.getDbFieldName());
                dsKeyQueryFormSettingVO.setViewFieldLable(dsFormTableSettingDO.getViewFieldLable());
                dsKeyQueryFormSettingVO.setFormFieldSequence(dsFormTableSettingDO.getFormFieldSequence());


                String formQueryType = dsFormTableSettingDO.getFormFieldQueryType();
                if(DsConstants.FormFieldQueryTypeEnum.getChoiceList().contains(formQueryType)){
                    // 查找optionList,以及默认值
                    String domainName = dsFormTableSettingDO.getFormFieldDicDomainName();
                    OptionListAndDefaultValDTO optionListAndDefaultValDTO = this.dsBasicDictionaryService.getOptionListAndDefaultValDTOByDomainName(domainName);
                    if(optionListAndDefaultValDTO != null){
                        dsKeyQueryFormSettingVO.setFieldValue(optionListAndDefaultValDTO.getDefaultVal());
                        dsKeyQueryFormSettingVO.setFieldOptionalValueList(optionListAndDefaultValDTO.getOptionDTOList());
                    }
                }
                dsKeyQueryFormSettingVO.setFormFieldQueryType(formQueryType);
                rootDsKeyQueryFormSettingVOList.add(dsKeyQueryFormSettingVO);
            }
        }
        // Order Form Fields
        rootDsKeyQueryFormSettingVOList.sort(new Comparator<DsKeyQueryFormSettingVO>() {
            @Override
            public int compare(DsKeyQueryFormSettingVO o1, DsKeyQueryFormSettingVO o2) {
                if (o1.getFormFieldSequence() < o2.getFormFieldSequence()) {
                    return -1;
                } else if (o1.getFormFieldSequence() > o2.getFormFieldSequence()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return rootDsKeyQueryFormSettingVOList;
    }

    private List<DsKeyQueryTableSettingVO> realGetDsKeyQueryTableSetting(List<DsFormTableSettingDO> dsFormTableSettingDOList) {
        // step 1 ,分解数据为一二级表头（跟前端配合）
        List<DsKeyQueryTableSettingVO> rootDsKeyQueryTableSettingVOList = new LinkedList<>();
        Map<String, List<DsKeyQueryTableSettingVO>> groupTableFieldVOMap = new HashMap<>();
        for (DsFormTableSettingDO dsFormTableSettingDO : dsFormTableSettingDOList) {
            if(dsFormTableSettingDO.getTableFieldVisible()){
                DsKeyQueryTableSettingVO dsKeyQueryTableSettingVO = new DsKeyQueryTableSettingVO();
                String tableParentLable = dsFormTableSettingDO.getTableParentLable();

                dsKeyQueryTableSettingVO.setDbFieldName(dsFormTableSettingDO.getDbFieldName());
                dsKeyQueryTableSettingVO.setTableFieldColumnWidth(dsFormTableSettingDO.getTableFieldColumnWidth());
                dsKeyQueryTableSettingVO.setViewFieldLable(dsFormTableSettingDO.getViewFieldLable());
                dsKeyQueryTableSettingVO.setTableFieldOrderBy(dsFormTableSettingDO.getTableFieldOrderBy());
                dsKeyQueryTableSettingVO.setTableFieldSequence(dsFormTableSettingDO.getTableFieldSequence());

                if(tableParentLable != null){
                    List<DsKeyQueryTableSettingVO> dsKeyQueryTableSettingVOS = groupTableFieldVOMap.get(tableParentLable);
                    if(dsKeyQueryTableSettingVOS == null){
                        dsKeyQueryTableSettingVOS = new LinkedList<>();
                        groupTableFieldVOMap.put(tableParentLable, dsKeyQueryTableSettingVOS);

                        DsKeyQueryTableSettingVO rootDsKeyQueryTableSettingVO = new DsKeyQueryTableSettingVO();
                        BeanUtils.copyProperties(dsFormTableSettingDO,rootDsKeyQueryTableSettingVO);
                        rootDsKeyQueryTableSettingVO.setViewFieldLable(tableParentLable);
                        rootDsKeyQueryTableSettingVO.setDbFieldName(tableParentLable);
                        // 遍历出来的第一个添加到root里边
                        rootDsKeyQueryTableSettingVOList.add(rootDsKeyQueryTableSettingVO);
                    }
                    dsKeyQueryTableSettingVOS.add(dsKeyQueryTableSettingVO);


                }else{
                    rootDsKeyQueryTableSettingVOList.add(dsKeyQueryTableSettingVO);
                }
            }
        }

        //step2 对二级进行排序
        for (DsKeyQueryTableSettingVO tableFieldAO : rootDsKeyQueryTableSettingVOList) {
            // 对二级表头的一些字段做了特殊处理，它的排序用的第一个子的
            if (tableFieldAO.getDbFieldName().equals(tableFieldAO.getViewFieldLable()) && groupTableFieldVOMap.containsKey(tableFieldAO.getDbFieldName())) {
                tableFieldAO.setChildren(groupTableFieldVOMap.get(tableFieldAO.getDbFieldName()));
                if (tableFieldAO.getChildren() != null && tableFieldAO.getChildren().size() != 0) {
                    tableFieldAO.getChildren().sort(new Comparator<DsKeyQueryTableSettingVO>() {

                        @Override
                        public int compare(DsKeyQueryTableSettingVO o1, DsKeyQueryTableSettingVO o2) {
                            if (o1.getTableFieldSequence() < o2.getTableFieldSequence()) {
                                return -1;
                            } else if (o1.getTableFieldSequence() > o2.getTableFieldSequence()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }

                    });
                }
            }
        }

        // step3 对外层进行排序
        rootDsKeyQueryTableSettingVOList.sort(new Comparator<DsKeyQueryTableSettingVO>() {
            @Override
            public int compare(DsKeyQueryTableSettingVO o1, DsKeyQueryTableSettingVO o2) {
                if (o1.getTableFieldSequence() < o2.getTableFieldSequence()) {
                    return -1;
                } else if (o1.getTableFieldSequence() > o2.getTableFieldSequence()) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });
        return rootDsKeyQueryTableSettingVOList;
    }


    private List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException, IOException {
        return this.dsFormTableSettingDao.getAllDsFormTableSettingByDsKey(dataSourceKey);
    }
}
