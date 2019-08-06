package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.component.DsFormTableSettingComponent;
import ai.sparklabinc.d1.constant.DsConstants;
import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DsFormTableSettingDao;
import ai.sparklabinc.d1.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.d1.dao.DsQueryDao;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.dto.*;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.d1.generator.SQLGeneratorFactory;
import ai.sparklabinc.d1.service.DsBasicDictionaryService;
import ai.sparklabinc.d1.service.QueryFormTableService;
import ai.sparklabinc.d1.util.ApiUtils;
import ai.sparklabinc.d1.util.D1SQLUtils;
import ai.sparklabinc.d1.util.SqlConditions;
import ai.sparklabinc.d1.util.StringUtils;
import ai.sparklabinc.d1.vo.DsKeyQueryFormSettingVO;
import ai.sparklabinc.d1.vo.DsKeyQueryTableSettingVO;
import ai.sparklabinc.d1.vo.DsKeyQueryVO;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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


    @Resource(name = "DsFormTableSettingDao")
    private DsFormTableSettingDao dsFormTableSettingDao;

    @Autowired
    private DsBasicDictionaryService dsBasicDictionaryService;

    @Autowired
    private DsFormTableSettingComponent dsFormTableSettingComponent;

    @Resource(name = "DsKeyBasicConfigDao")
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DsQueryDao")
    private DsQueryDao dsQueryDao;

    @Autowired
    private SQLGeneratorFactory sqlGeneratorFactory;

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
            throw new ResourceNotFoundException(String.format("DataSourceKey setting not found:%s", dataSourceKey));
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
            throw new ServiceException(dataSourceKey + " Failed to transfrom query parameter map");
        }

        String tableName = dsKeyBasicConfigDO.getTableName();
        String schemaName = dsKeyBasicConfigDO.getSchemaName();
        if(StringUtils.isNotNullNorEmpty(schemaName)) {
            tableName = schemaName + "." + tableName;
        }

        AssemblyResultDTO assemblyResultDTO = new AssemblyResultDTO();
        if(returnDatasource){
            Long dbId = dsKeyBasicConfigDO.getFkDbId();
            DbBasicConfigDO dbBasicConfigDO = this.dbBasicConfigDao.findById(dbId);
            DataSource dataSource = dataSourceFactory.builder(dbBasicConfigDO.getDbType(), dbBasicConfigDO.getId());
            assemblyResultDTO.setDataSource(dataSource);
        }

        SqlConditions sqlConditions = generateSqlConditions(queryParameterGroup,dsFormTableSettingDOList);
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
        AssemblyResultDTO assemblyResultDTO = generalQuery(dataSourceKey, simpleParameters, pageable, moreWhereClause,false);
        PageResultDTO pageResultDTO = dsQueryDao.excuteQuery(assemblyResultDTO, dsKeyBasicConfigDO.getFkDbId());
        return pageResultDTO;
    }


    @Override
    public SQLGenerResultDTO generalSQL(String dataSourceKey, Map<String, String[]> requestParams) throws Exception {
        List<DsFormTableSettingDO> dsFormTableSettingDOList = getAllDsFormTableSettingByDsKey(dataSourceKey);
        if (dsFormTableSettingDOList == null || dsFormTableSettingDOList.isEmpty()) {
            throw new ResourceNotFoundException(String.format("DataSourceKey setting not found:%s", dataSourceKey));
        }

        DsKeyBasicConfigDO dsKeyBasicConfigDO = this.dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dataSourceKey);
        if(dsKeyBasicConfigDO == null){
            throw new ResourceNotFoundException(String.format("DataSourceKey not found:%s", dataSourceKey));
        }
        String tableName = dsKeyBasicConfigDO.getTableName();
        String schemaName = dsKeyBasicConfigDO.getSchemaName();
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dsKeyBasicConfigDO.getFkDbId());
        if(dbBasicConfigDO==null|| org.apache.commons.lang3.StringUtils.isBlank(dbBasicConfigDO.getDbType())){
            throw new ServiceException("db config is not found or db type is null");
        }

        QueryParameterGroupDTO queryParameterGroup = null;
        try {
            Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(requestParams);
            queryParameterGroup = this.dsFormTableSettingComponent.transformQueryParameterMap("",
                    simpleParameters, dsFormTableSettingDOList);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to transfrom query parameter map", e);
            throw new ServiceException(" Failed to transfrom query parameter map");
        }


        SQLGenerResultDTO sqlGenerResultDTO = sqlGeneratorFactory.builder(dbBasicConfigDO.getDbType())
                .buildSQL(null, schemaName, tableName, requestParams, queryParameterGroup, dsFormTableSettingDOList);
        sqlGenerResultDTO.setSqlType(dbBasicConfigDO.getDbType());
        return sqlGenerResultDTO;
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


    private SqlConditions generateSqlConditions(QueryParameterGroupDTO queryParameterGroup, List<DsFormTableSettingDO> dsFormTableSettingDOList) throws Exception {
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
                    D1SQLUtils.buildFuzzyLikeQueryParameterString(fuzzyLike, sqlConditions,dsFormTableSettingDOList);
                }
                if(accurateEqualsString != null && !accurateEqualsString.isEmpty()){
                    D1SQLUtils.buildAccurateEqualsStringQueryParameterString(accurateEqualsString, sqlConditions,dsFormTableSettingDOList);
                }
                if(accurateInString != null && !accurateInString.isEmpty()){
                    D1SQLUtils.buildAccurateInStringQueryParameterString(accurateInString, sqlConditions,dsFormTableSettingDOList);
                }
                if(accurateDateRange != null && !accurateDateRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateRangeQueryParameterString(accurateDateRange, sqlConditions,dsFormTableSettingDOList);
                }
                if(accurateDateTimeRange != null && !accurateDateTimeRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateTimeRangeQueryParameterString(accurateDateTimeRange, sqlConditions,dsFormTableSettingDOList);
                }
                if(accurateNumberRange != null && !accurateNumberRange.isEmpty()){
                    D1SQLUtils.buildAccurateNumberRangeQueryParameterString(accurateNumberRange, sqlConditions,dsFormTableSettingDOList);
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
                dsKeyQueryFormSettingVO.setViewFieldLabel(dsFormTableSettingDO.getViewFieldLabel());
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
                dsKeyQueryTableSettingVO.setViewFieldLabel(dsFormTableSettingDO.getViewFieldLabel());
                dsKeyQueryTableSettingVO.setTableFieldOrderBy(dsFormTableSettingDO.getTableFieldOrderBy());
                dsKeyQueryTableSettingVO.setTableFieldSequence(dsFormTableSettingDO.getTableFieldSequence());

                if(tableParentLable != null){
                    List<DsKeyQueryTableSettingVO> dsKeyQueryTableSettingVOS = groupTableFieldVOMap.get(tableParentLable);
                    if(dsKeyQueryTableSettingVOS == null){
                        dsKeyQueryTableSettingVOS = new LinkedList<>();
                        groupTableFieldVOMap.put(tableParentLable, dsKeyQueryTableSettingVOS);

                        DsKeyQueryTableSettingVO rootDsKeyQueryTableSettingVO = new DsKeyQueryTableSettingVO();
                        BeanUtils.copyProperties(dsFormTableSettingDO,rootDsKeyQueryTableSettingVO);
                        rootDsKeyQueryTableSettingVO.setViewFieldLabel(tableParentLable);
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
            if (tableFieldAO.getDbFieldName().equals(tableFieldAO.getViewFieldLabel()) && groupTableFieldVOMap.containsKey(tableFieldAO.getDbFieldName())) {
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
