package io.g740.d1.service.impl;

import io.g740.d1.component.DfFormTableSettingComponent;
import io.g740.d1.constant.DsConstants;
import io.g740.d1.dao.DbBasicConfigDao;
import io.g740.d1.dao.DfFormTableSettingDao;
import io.g740.d1.dao.DfKeyBasicConfigDao;
import io.g740.d1.dao.DsQueryDao;
import io.g740.d1.datasource.DataSourceFactory;
import io.g740.d1.dict.dto.DictOptionCascadeQueryDTO;
import io.g740.d1.dict.service.DictService;
import io.g740.d1.dto.*;
import io.g740.d1.entity.DbBasicConfigDO;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.entity.DfKeyBasicConfigDO;
import io.g740.d1.exception.ServiceException;
import io.g740.d1.exception.custom.ResourceNotFoundException;
import io.g740.d1.generator.SQLGeneratorFactory;
import io.g740.d1.service.DsBasicDictionaryService;
import io.g740.d1.service.QueryFormTableService;
import io.g740.d1.util.ApiUtils;
import io.g740.d1.util.D1SQLUtils;
import io.g740.d1.util.SqlConditions;
import io.g740.d1.util.StringUtils;
import io.g740.d1.vo.DfKeyQueryFormSettingVO;
import io.g740.d1.vo.DfKeyQueryTableSettingVO;
import io.g740.d1.vo.DfKeyQueryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
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


    @Resource(name = "DfFormTableSettingDao")
    private DfFormTableSettingDao dfFormTableSettingDao;

    @Autowired
    private DsBasicDictionaryService dsBasicDictionaryService;
    
    @Autowired
    private DictService dictService;

    @Autowired
    private DfFormTableSettingComponent dfFormTableSettingComponent;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    @Qualifier("MysqlDsQueryDaoImpl")
    private DsQueryDao dsQueryDao;

    @Autowired
    private SQLGeneratorFactory sqlGeneratorFactory;

    @Override
    public List<DfKeyQueryTableSettingVO> getDfKeyQueryTableSetting(String dataFacetKey) throws Exception {
        List<DfFormTableSettingDO> dfFormTableSettingDOList = getAllDfFormTableSettingByDfKey(dataFacetKey);
        if(dfFormTableSettingDOList == null || dfFormTableSettingDOList.isEmpty()){
           throw new ResourceNotFoundException("Cannot find TABLE resource from data facet key :" + dataFacetKey);
        }
        return realGetDfKeyQueryTableSetting(dfFormTableSettingDOList);
    }

    @Override
    public  List<DfKeyQueryFormSettingVO> getDfKeyQueryFormSetting(String dataFacetKey) throws Exception {
        List<DfFormTableSettingDO> dfFormTableSettingDOList = getAllDfFormTableSettingByDfKey(dataFacetKey);
        if(dfFormTableSettingDOList == null || dfFormTableSettingDOList.isEmpty()){
            return null;
        }
        return realGetDfKeyQueryFormSetting(dfFormTableSettingDOList);
    }

    @Override
    public DfKeyQueryVO getDfKeyQuerySetting(String dataFacetKey) throws Exception {
        List<DfFormTableSettingDO> dfFormTableSettingDOList = getAllDfFormTableSettingByDfKey(dataFacetKey);
        if(dfFormTableSettingDOList == null || dfFormTableSettingDOList.isEmpty()){
            throw new ResourceNotFoundException(String.format(" not found:%s ", dataFacetKey));
        }

        List<DfKeyQueryFormSettingVO> dfKeyQueryFormSettingVOList = realGetDfKeyQueryFormSetting(dfFormTableSettingDOList);
        if(dfKeyQueryFormSettingVOList == null || dfKeyQueryFormSettingVOList.isEmpty()){
            throw new ResourceNotFoundException(String.format(" not found:%s ", dataFacetKey));
        }

        List<DfKeyQueryTableSettingVO> dfKeyQueryTableSettingVOList = realGetDfKeyQueryTableSetting(dfFormTableSettingDOList);
        if(dfKeyQueryTableSettingVOList == null || dfKeyQueryTableSettingVOList.isEmpty()){
            throw new ResourceNotFoundException(String.format(" not found:%s ", dataFacetKey));
        }
        return new DfKeyQueryVO(dfKeyQueryFormSettingVOList, dfKeyQueryTableSettingVOList);

    }

    @Override
    public AssemblyResultDTO generalQuery(String dataFacetKey, Map<String, String[]> simpleParameters, Pageable pageable, String moreWhereClause, boolean returnDatasource) throws Exception {
        List<DfFormTableSettingDO> dfFormTableSettingDOList = getAllDfFormTableSettingByDfKey(dataFacetKey);
        if (dfFormTableSettingDOList == null || dfFormTableSettingDOList.isEmpty()) {
            throw new ResourceNotFoundException(String.format(" setting not found:%s", dataFacetKey));
        }

        DfKeyBasicConfigDO dfKeyBasicConfigDO = this.dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dataFacetKey);
        if(dfKeyBasicConfigDO == null){
            throw new ResourceNotFoundException(String.format(" not found:%s", dataFacetKey));
        }

        QueryParameterGroupDTO queryParameterGroup = null;
        try {
            queryParameterGroup = this.dfFormTableSettingComponent.transformQueryParameterMap(dataFacetKey,
                    simpleParameters, dfFormTableSettingDOList);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to transfrom query parameter map", dataFacetKey, e);
            throw new ServiceException(dataFacetKey + " Failed to transfrom query parameter map");
        }

        String tableName = dfKeyBasicConfigDO.getTableName();
        String schemaName = dfKeyBasicConfigDO.getSchemaName();
        if(StringUtils.isNotNullNorEmpty(schemaName)) {
            tableName = schemaName + "." + tableName;
        }

        AssemblyResultDTO assemblyResultDTO = new AssemblyResultDTO();
        if(returnDatasource){
            Long dbId = dfKeyBasicConfigDO.getFkDbId();
            DbBasicConfigDO dbBasicConfigDO = this.dbBasicConfigDao.findById(dbId);
            DataSource dataSource = dataSourceFactory.builder(dbBasicConfigDO.getDbType(), dbBasicConfigDO.getId());
            assemblyResultDTO.setDataSource(dataSource);
        }

        SqlConditions sqlConditions = generateSqlConditions(queryParameterGroup, dfFormTableSettingDOList);
        List<Object> paramList = sqlConditions.getParameters();
        String wholeWhereClause = (StringUtils.isNotNullNorEmpty(sqlConditions.getWhereClause()) ? " AND " : "") + sqlConditions.getWhereClause() + (moreWhereClause == null ? "" : moreWhereClause);

        String countSql = generateCountSql(tableName,wholeWhereClause);
        LOGGER.info("count sql: {}", countSql);
        String querySql = generateQuerySql(tableName, wholeWhereClause, pageable);
        LOGGER.info("query sql: {}", querySql);

        assemblyResultDTO.setCountSql(countSql);
        assemblyResultDTO.setQuerySql(querySql);
        assemblyResultDTO.setDfFormTableSettingDOS(dfFormTableSettingDOList);
        assemblyResultDTO.setParamList(paramList);
        return assemblyResultDTO;
    }

    @Override
    public PageResultDTO executeQuery(String dataFacetKey, Map<String, String[]> simpleParameters) throws Exception {
        DfKeyBasicConfigDO dfKeyBasicConfigDO = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dataFacetKey);
        if(dfKeyBasicConfigDO ==null){
            throw new ResourceNotFoundException("data facet key is not found!");
        }
        //获取生成sql文件
        long start =System.currentTimeMillis();
        SQLGenerResultDTO sqlGenerResultDTO = generalSQL(dataFacetKey, simpleParameters);
        LOGGER.info("生成sql耗时：{}",System.currentTimeMillis()-start);
        start=System.currentTimeMillis();
        PageResultDTO pageResultDTO = dsQueryDao.excuteQuery(sqlGenerResultDTO, dfKeyBasicConfigDO.getFkDbId());
        LOGGER.info("执行sql耗时：{}",System.currentTimeMillis()-start);
        return pageResultDTO;
    }

    @Override
    public SQLGenerResultDTO generalSQL(String dataFacetKey, Map<String, String[]> requestParams) throws Exception {
        List<DfFormTableSettingDO> dfFormTableSettingDOList = getAllDfFormTableSettingByDfKey(dataFacetKey);
        if (dfFormTableSettingDOList == null || dfFormTableSettingDOList.isEmpty()) {
            throw new ResourceNotFoundException(String.format(" setting not found:%s", dataFacetKey));
        }

        DfKeyBasicConfigDO dfKeyBasicConfigDO = this.dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dataFacetKey);
        if(dfKeyBasicConfigDO == null){
            throw new ResourceNotFoundException(String.format(" not found:%s", dataFacetKey));
        }
        String tableName = dfKeyBasicConfigDO.getTableName();
        String schemaName = dfKeyBasicConfigDO.getSchemaName();
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dfKeyBasicConfigDO.getFkDbId());
        if(dbBasicConfigDO==null|| org.apache.commons.lang3.StringUtils.isBlank(dbBasicConfigDO.getDbType())){
            throw new ServiceException("db config is not found or db type is null");
        }

        QueryParameterGroupDTO queryParameterGroup = null;
        try {
            Map<String, String[]> simpleParameters = ApiUtils.removeReservedParameters(requestParams);
            queryParameterGroup = this.dfFormTableSettingComponent.transformQueryParameterMap("",
                    simpleParameters, dfFormTableSettingDOList);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to transfrom query parameter map", e);
            throw new ServiceException(" Failed to transfrom query parameter map");
        }


        SQLGenerResultDTO sqlGenerResultDTO = sqlGeneratorFactory.builder(dbBasicConfigDO.getDbType())
                .buildSQL(null, schemaName, tableName, requestParams, queryParameterGroup, dfFormTableSettingDOList);
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
            if(pageable.getPageSize() != DsConstants.SIZE_WITHOUT_PAGEABLE  || pageable.getPageNumber() != DsConstants.SIZE_WITHOUT_PAGEABLE){
                long offset = pageable.getPageSize()*pageable.getPageNumber();
                int limit = pageable.getPageSize();
                pageParam.append(" LIMIT " + offset + "," +limit);
            }


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


    private SqlConditions generateSqlConditions(QueryParameterGroupDTO queryParameterGroup, List<DfFormTableSettingDO> dfFormTableSettingDOList) throws Exception {
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
                    D1SQLUtils.buildFuzzyLikeQueryParameterString(fuzzyLike, sqlConditions, dfFormTableSettingDOList);
                }
                if(accurateEqualsString != null && !accurateEqualsString.isEmpty()){
                    D1SQLUtils.buildAccurateEqualsStringQueryParameterString(accurateEqualsString, sqlConditions, dfFormTableSettingDOList);
                }
                if(accurateInString != null && !accurateInString.isEmpty()){
                    D1SQLUtils.buildAccurateInStringQueryParameterString(accurateInString, sqlConditions, dfFormTableSettingDOList);
                }
                if(accurateDateRange != null && !accurateDateRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateRangeQueryParameterString(accurateDateRange, sqlConditions, dfFormTableSettingDOList);
                }
                if(accurateDateTimeRange != null && !accurateDateTimeRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateTimeRangeQueryParameterString(accurateDateTimeRange, sqlConditions, dfFormTableSettingDOList);
                }
                if(accurateNumberRange != null && !accurateNumberRange.isEmpty()){
                    D1SQLUtils.buildAccurateNumberRangeQueryParameterString(accurateNumberRange, sqlConditions, dfFormTableSettingDOList);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to build sql", e);
                throw new ServiceException(String.format("build sql Failed :%s",e.getMessage()));
            }
        }
        return sqlConditions;
    }

    private List<DfKeyQueryFormSettingVO> realGetDfKeyQueryFormSetting(List<DfFormTableSettingDO> dfFormTableSettingDOList) throws Exception {
        List<DfKeyQueryFormSettingVO> rootDfKeyQueryFormSettingVOList = new LinkedList<>();
        DfKeyQueryFormSettingVO dfKeyQueryFormSettingVO = null;
        for (DfFormTableSettingDO dfFormTableSettingDO : dfFormTableSettingDOList) {
            if(dfFormTableSettingDO.getFormFieldVisible()){
                dfKeyQueryFormSettingVO = new DfKeyQueryFormSettingVO();
                dfKeyQueryFormSettingVO.setDbFieldName(dfFormTableSettingDO.getDbFieldName());
                dfKeyQueryFormSettingVO.setViewFieldLabel(dfFormTableSettingDO.getViewFieldLabel());
                dfKeyQueryFormSettingVO.setFormFieldSequence(dfFormTableSettingDO.getFormFieldSequence());

                String formQueryType = dfFormTableSettingDO.getFormFieldQueryType();
                if(DsConstants.FormFieldQueryTypeEnum.getChoiceList().contains(formQueryType)){
                    // 查找optionList,以及默认值
                    String domainName = dfFormTableSettingDO.getFormFieldDictDomainName();
                    String item=dfFormTableSettingDO.getFormFieldDictItem();
                    String formFieldChildFieldName = dfFormTableSettingDO.getFormFieldChildFieldName();
                    //级联
                    if(StringUtils.isNotNullNorEmpty(formFieldChildFieldName)) {
                    	List<DictOptionCascadeQueryDTO> cascadeQueryByDomainAndItem = this.dictService.cascadeQueryByDomainAndItem(domainName, item);
                    	if(cascadeQueryByDomainAndItem != null) {
                    		 dfKeyQueryFormSettingVO.setFieldCascadeOptionalValueList(cascadeQueryByDomainAndItem);
                    		 dfKeyQueryFormSettingVO.setFieldCascadeChildFieldName(formFieldChildFieldName);
                    	}
                    }else {
                    	  OptionListAndDefaultValDTO optionListAndDefaultValDTO = this.dsBasicDictionaryService.getOptionListAndDefaultValDTOByDomainName(domainName,item);
                          if(optionListAndDefaultValDTO != null){
                              dfKeyQueryFormSettingVO.setFieldOptionalValueList(optionListAndDefaultValDTO.getOptionDTOList());
                          }
                    }
                }

                if(dfFormTableSettingDO.getFormFieldUseDefaultVal()){
                    dfKeyQueryFormSettingVO.setFieldValue(dfFormTableSettingDO.getFormFieldDefaultVal());
                }
                dfKeyQueryFormSettingVO.setFormFieldQueryType(formQueryType);
                rootDfKeyQueryFormSettingVOList.add(dfKeyQueryFormSettingVO);
            }
        }
        // Order Form Fields
        rootDfKeyQueryFormSettingVOList.sort(new Comparator<DfKeyQueryFormSettingVO>() {
            @Override
            public int compare(DfKeyQueryFormSettingVO o1, DfKeyQueryFormSettingVO o2) {
                if (o1.getFormFieldSequence() < o2.getFormFieldSequence()) {
                    return -1;
                } else if (o1.getFormFieldSequence() > o2.getFormFieldSequence()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return rootDfKeyQueryFormSettingVOList;
    }

    private List<DfKeyQueryTableSettingVO> realGetDfKeyQueryTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOList) {
        // step 1 ,分解数据为一二级表头（跟前端配合）
        List<DfKeyQueryTableSettingVO> rootDfKeyQueryTableSettingVOList = new LinkedList<>();
        Map<String, List<DfKeyQueryTableSettingVO>> groupTableFieldVOMap = new HashMap<>();
        for (DfFormTableSettingDO dfFormTableSettingDO : dfFormTableSettingDOList) {
            if(dfFormTableSettingDO.getTableFieldVisible()){
                DfKeyQueryTableSettingVO dfKeyQueryTableSettingVO = new DfKeyQueryTableSettingVO();
                String tableParentLable = dfFormTableSettingDO.getTableParentLabel();

                dfKeyQueryTableSettingVO.setDbFieldName(dfFormTableSettingDO.getDbFieldName());
                dfKeyQueryTableSettingVO.setTableFieldColumnWidth(dfFormTableSettingDO.getTableFieldColumnWidth());
                dfKeyQueryTableSettingVO.setViewFieldLabel(dfFormTableSettingDO.getViewFieldLabel());
                dfKeyQueryTableSettingVO.setTableFieldOrderBy(dfFormTableSettingDO.getTableFieldOrderBy());
                dfKeyQueryTableSettingVO.setTableFieldSequence(dfFormTableSettingDO.getTableFieldSequence());

                if(tableParentLable != null){
                    List<DfKeyQueryTableSettingVO> dfKeyQueryTableSettingVOS = groupTableFieldVOMap.get(tableParentLable);
                    if(dfKeyQueryTableSettingVOS == null){
                        dfKeyQueryTableSettingVOS = new LinkedList<>();
                        groupTableFieldVOMap.put(tableParentLable, dfKeyQueryTableSettingVOS);

                        DfKeyQueryTableSettingVO rootDfKeyQueryTableSettingVO = new DfKeyQueryTableSettingVO();
                        BeanUtils.copyProperties(dfFormTableSettingDO, rootDfKeyQueryTableSettingVO);
                        rootDfKeyQueryTableSettingVO.setViewFieldLabel(tableParentLable);
                        rootDfKeyQueryTableSettingVO.setDbFieldName(tableParentLable);
                        // 遍历出来的第一个添加到root里边
                        rootDfKeyQueryTableSettingVOList.add(rootDfKeyQueryTableSettingVO);
                    }
                    dfKeyQueryTableSettingVOS.add(dfKeyQueryTableSettingVO);
                }else{
                    rootDfKeyQueryTableSettingVOList.add(dfKeyQueryTableSettingVO);
                }
            }
        }

        //step2 对二级进行排序
        for (DfKeyQueryTableSettingVO tableFieldAO : rootDfKeyQueryTableSettingVOList) {
            // 对二级表头的一些字段做了特殊处理，它的排序用的第一个子的
            if (tableFieldAO.getDbFieldName().equals(tableFieldAO.getViewFieldLabel()) && groupTableFieldVOMap.containsKey(tableFieldAO.getDbFieldName())) {
                tableFieldAO.setChildren(groupTableFieldVOMap.get(tableFieldAO.getDbFieldName()));
                if (tableFieldAO.getChildren() != null && tableFieldAO.getChildren().size() != 0) {
                    tableFieldAO.getChildren().sort(new Comparator<DfKeyQueryTableSettingVO>() {
                        @Override
                        public int compare(DfKeyQueryTableSettingVO o1, DfKeyQueryTableSettingVO o2) {
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
        rootDfKeyQueryTableSettingVOList.sort(new Comparator<DfKeyQueryTableSettingVO>() {
            @Override
            public int compare(DfKeyQueryTableSettingVO o1, DfKeyQueryTableSettingVO o2) {
                if (o1.getTableFieldSequence() < o2.getTableFieldSequence()) {
                    return -1;
                } else if (o1.getTableFieldSequence() > o2.getTableFieldSequence()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return rootDfKeyQueryTableSettingVOList;
    }


    private List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKey(String dataFacetKey) throws SQLException, IOException {
        return this.dfFormTableSettingDao.getAllDfFormTableSettingByDfKey(dataFacetKey);
    }
}
