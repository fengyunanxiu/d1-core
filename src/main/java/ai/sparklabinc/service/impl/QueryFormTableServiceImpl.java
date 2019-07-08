package ai.sparklabinc.service.impl;

import ai.sparklabinc.component.DsFormTableSettingComponent;
import ai.sparklabinc.constant.DsConstants;
import ai.sparklabinc.dao.DbBasicConfigDao;
import ai.sparklabinc.dao.DsFormTableSettingDao;
import ai.sparklabinc.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.dao.DsQueryDao;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.AssemblyResultDTO;
import ai.sparklabinc.dto.OptionListAndDefaultValDTO;
import ai.sparklabinc.dto.PageResultDTO;
import ai.sparklabinc.dto.QueryParameterGroupDTO;
import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.exception.ServiceException;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.service.DsBasicDictionaryService;
import ai.sparklabinc.service.QueryFormTableService;
import ai.sparklabinc.util.D1SQLUtils;
import ai.sparklabinc.util.SqlConditions;
import ai.sparklabinc.util.StringUtils;
import ai.sparklabinc.vo.DsKeyQueryFormSettingVO;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import ai.sparklabinc.vo.DsKeyQueryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Autowired
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    private DsQueryDao dsQueryDao;

    @Override
    public List<DsKeyQueryTableSettingVO> getDsKeyQueryTableSetting(String dataSourceKey) throws Exception {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if(dsFormTableSettingDOList == null || dsFormTableSettingDOList.isEmpty()){
           throw new ResourceNotFoundException("Cannot find TABLE resource from data source key :" + dataSourceKey);
        }
        return realGetDsKeyQueryTableSetting(dsFormTableSettingDOList);
    }

    @Override
    public  List<DsKeyQueryFormSettingVO> getDsKeyQueryFormSetting(String dataSourceKey) throws Exception {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if(dsFormTableSettingDOList == null || dsFormTableSettingDOList.isEmpty()){
            return null;
        }
        return realGetDsKeyQueryFormSetting(dsFormTableSettingDOList);
    }

    @Override
    public DsKeyQueryVO getDsKeyQuerySetting(String dataSourceKey) throws Exception {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if(dsFormTableSettingDOList == null || dsFormTableSettingDOList.isEmpty()){
            throw new ResourceNotFoundException(String.format("DataSourceKey not found:%s ", dataSourceKey));
        }

        List<DsKeyQueryFormSettingVO> dsKeyQueryFormSettingVOList = realGetDsKeyQueryFormSetting(dsFormTableSettingDOList);
        if(dsKeyQueryFormSettingVOList == null || dsKeyQueryFormSettingVOList.isEmpty()){
            throw new ResourceNotFoundException(String.format("DataSourceKey not found:%s ", dataSourceKey));
        }

        List<DsKeyQueryTableSettingVO> dsKeyQueryTableSettingVOList = realGetDsKeyQueryTableSetting(dsFormTableSettingDOList);
        if(dsKeyQueryTableSettingVOList == null || dsKeyQueryTableSettingVOList.isEmpty()){
            throw new ResourceNotFoundException(String.format("DataSourceKey not found:%s ", dataSourceKey));
        }
        return new DsKeyQueryVO(dsKeyQueryFormSettingVOList,dsKeyQueryTableSettingVOList);

    }

    @Override
    public AssemblyResultDTO generalQuery(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause, boolean returnDatasource) throws Exception {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if (dsFormTableSettingDOList == null || dsFormTableSettingDOList.isEmpty()) {
            throw new ResourceNotFoundException(String.format("DataSourceKey not found:%s", dataSourceKey));
        }

        DsKeyBasicConfigDO dsKeyBasicConfigDO = this.dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dataSourceKey);
        if(dsKeyBasicConfigDO == null){
            throw new ResourceNotFoundException(String.format("DataSourceKey not found:%s", dataSourceKey));
        }

        QueryParameterGroupDTO queryParameterGroup = null;
        try {
            queryParameterGroup = this.dsFormTableSettingComponent.transformQueryParameterMap(dataSourceKey,
                    simpleParameters, dsFormTableSettingDOList);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to transfrom query parameter map", dataSourceKey, e);
            throw new ServiceException(dataSourceKey + "Failed to transfrom query parameter map");
        }


        String tableName = dsKeyBasicConfigDO.getTableName();
        String schemaName = dsKeyBasicConfigDO.getSchema();
        if(StringUtils.isNotNullNorEmpty(schemaName)) {
            tableName = schemaName + "." + tableName;
        }

        AssemblyResultDTO assemblyResultDTO = new AssemblyResultDTO();
        if(returnDatasource){
            Long dbId = dsKeyBasicConfigDO.getFkDbId();
            DbBasicConfigDO dbBasicConfigDO = this.dbBasicConfigDao.findById(dbId);
            DataSource dataSource = dataSourceFactory.builder(dbBasicConfigDO.getType(), dbBasicConfigDO.getId());
            assemblyResultDTO.setDataSource(dataSource);
        }


        SqlConditions sqlConditions = generateSqlConditions(queryParameterGroup);
        List<Object> paramList = sqlConditions.getParameters();
        String wholeWhereClause = (StringUtils.isNotNullNorEmpty(sqlConditions.getWhereClause()) ? " AND " : "") + sqlConditions.getWhereClause() + (moreWhereClause == null ? "" : moreWhereClause);

        String countSql = generateCountSql(tableName,wholeWhereClause);
        LOGGER.info("count sql: {}", countSql);
        String querySql = generateQuerySql(tableName, wholeWhereClause, pageable);
        LOGGER.info("query sql: {}", querySql);

        assemblyResultDTO.setCountSql(countSql);
        assemblyResultDTO.setQuerySql(querySql);
        assemblyResultDTO.setDsFormTableSettingDOS(dsFormTableSettingDOList);
        assemblyResultDTO.setParamList(paramList);
        return assemblyResultDTO;
    }

    @Override
    public PageResultDTO executeQuery(String dataSourceKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dataSourceKey);
        if(dsKeyBasicConfigDO==null){
            throw new ResourceNotFoundException("data source key is not found!");
        }
        //获取生成sql文件
        AssemblyResultDTO assemblyResultDTO = generalQuery(dataSourceKey, simpleParameters, pageable, moreWhereClause);
        PageResultDTO pageResultDTO = dsQueryDao.excuteQuery(assemblyResultDTO, dsKeyBasicConfigDO.getFkDbId());
        return pageResultDTO;
    }









    private String generateQuerySql(String tableName, String wholeWhereClause, Pageable pageable) {
        StringBuilder querySqlStringBuilder = new StringBuilder();
        querySqlStringBuilder.append("SELECT * FROM " + tableName + " WHERE 1 = 1");
        querySqlStringBuilder.append(wholeWhereClause);

        StringBuilder sorParam = new StringBuilder();
        StringBuilder pageParam = new StringBuilder();
        if(pageable != null) {
            long offset = pageable.getOffset();
            int limit = pageable.getPageSize();
            pageParam.append(" LIMIT " + offset + "," +limit);

            Sort sort = pageable.getSort();
            if (sort != null) {
                Iterator<Sort.Order> sortIterator = sort.iterator();
                if (sortIterator.hasNext()) {
                    sorParam.append(" ORDER BY ");

                    do {
                        Sort.Order order = sortIterator.next();
                        if (sortIterator.hasNext()) {
                            if (order.getProperty() != null && !order.getProperty().isEmpty()
                                    && order.getDirection() != null)
                                sorParam.append(order.getProperty() + " " + order.getDirection().toString() + ", ");
                        } else {
                            if (order.getProperty() != null && !order.getProperty().isEmpty()
                                    && order.getDirection() != null)
                                sorParam.append(order.getProperty() + " " + order.getDirection().toString());
                        }
                    } while (sortIterator.hasNext());
                }
            }
        }
        querySqlStringBuilder.append(sorParam);
        querySqlStringBuilder.append(pageParam);
        return querySqlStringBuilder.toString();
    }

    private String generateCountSql(String tableName, String wholeWhereClause) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT COUNT(*) FROM " + tableName + " WHERE 1 = 1 ");
        stringBuilder.append(wholeWhereClause);
        return  stringBuilder.toString();
    }


    private SqlConditions generateSqlConditions(QueryParameterGroupDTO queryParameterGroup) throws Exception {
        SqlConditions sqlConditions = new SqlConditions();
        if (queryParameterGroup != null) {
            try {
                Map<String, String> fuzzyLike = queryParameterGroup.getFuzzyLike();
                Map<String, String> accurateEqualsString = queryParameterGroup.getAccurateEqualsString();
                Map<String, String[]> accurateInString =queryParameterGroup.getAccurateInString();
                Map<String, String[]> accurateDateRange = queryParameterGroup.getAccurateDateRange();
                Map<String, String[]> accurateDateTimeRange = queryParameterGroup.getAccurateDateTimeRange();
                Map<String, String[]> accurateNumberRange = queryParameterGroup.getAccurateNumberRange();

                if(fuzzyLike != null && !fuzzyLike.isEmpty()){
                    D1SQLUtils.buildFuzzyLikeQueryParameterString(fuzzyLike, sqlConditions);
                }
                if(accurateEqualsString != null && !accurateEqualsString.isEmpty()){
                    D1SQLUtils.buildAccurateEqualsStringQueryParameterString(accurateEqualsString, sqlConditions);
                }
                if(accurateInString != null && !accurateInString.isEmpty()){
                    D1SQLUtils.buildAccurateInStringQueryParameterString(accurateInString, sqlConditions);
                }
                if(accurateDateRange != null && !accurateDateRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateRangeQueryParameterString(accurateDateRange, sqlConditions);
                }
                if(accurateDateTimeRange != null && !accurateDateTimeRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateTimeRangeQueryParameterString(accurateDateTimeRange, sqlConditions);
                }
                if(accurateNumberRange != null && !accurateNumberRange.isEmpty()){
                    D1SQLUtils.buildAccurateNumberRangeQueryParameterString(accurateNumberRange, sqlConditions);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to build sql", e);
                throw new ServiceException(String.format("build sql Failed :%s",e.getMessage()));
            }
        }
        return sqlConditions;
    }

    private List<DsKeyQueryFormSettingVO> realGetDsKeyQueryFormSetting(List<DsFormTableSettingDO> dsFormTableSettingDOList) throws SQLException, IOException {
        List<DsKeyQueryFormSettingVO> rootDsKeyQueryFormSettingVOList = new LinkedList<>();
        DsKeyQueryFormSettingVO dsKeyQueryFormSettingVO = null;
        for (DsFormTableSettingDO dsFormTableSettingDO : dsFormTableSettingDOList) {
            if(dsFormTableSettingDO.getFormFieldVisible()){
                dsKeyQueryFormSettingVO = new DsKeyQueryFormSettingVO();
                dsKeyQueryFormSettingVO.setDbFieldName(dsFormTableSettingDO.getDbFieldName());
                dsKeyQueryFormSettingVO.setViewFieldLable(dsFormTableSettingDO.getViewFieldLabel());
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
                String tableParentLable = dsFormTableSettingDO.getTableParentLabel();

                dsKeyQueryTableSettingVO.setDbFieldName(dsFormTableSettingDO.getDbFieldName());
                dsKeyQueryTableSettingVO.setTableFieldColumnWidth(dsFormTableSettingDO.getTableFieldColumnWidth());
                dsKeyQueryTableSettingVO.setViewFieldLable(dsFormTableSettingDO.getViewFieldLabel());
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
