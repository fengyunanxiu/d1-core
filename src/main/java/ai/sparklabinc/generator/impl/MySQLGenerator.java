package ai.sparklabinc.generator.impl;

import ai.sparklabinc.dto.QueryParameterGroupDTO;
import ai.sparklabinc.dto.SQLGenerResultDTO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.exception.ServiceException;
import ai.sparklabinc.generator.SQLGenerator;
import ai.sparklabinc.util.D1SQLUtils;
import ai.sparklabinc.util.ParameterHandlerUtils;
import ai.sparklabinc.util.SqlConditions;
import ai.sparklabinc.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @function:
 * @author: DAM
 * @date: 2019/7/29 16:01
 * @description:
 * @version: V1.0
 */
public class MySQLGenerator implements SQLGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLGenerator.class);




    @Override
    public SQLGenerResultDTO buildSQL(String database, String schema, String table, Map<String, String[]> requestParams, QueryParameterGroupDTO queryParameterGroup, List<DsFormTableSettingDO> dsFormTableSettingDOS) throws Exception {
        //参数处理
        Pageable pageable = ParameterHandlerUtils.extractPageable(requestParams);
        String moreWhereClause = ParameterHandlerUtils.extractMoreClause(requestParams);

        String tableName=schema+"."+table;
        SQLGenerResultDTO sqlGenerResultDTO = new SQLGenerResultDTO();

        SqlConditions sqlConditions = generateSqlConditions(queryParameterGroup,dsFormTableSettingDOS);
        List<Object> paramsValueList = sqlConditions.getParameters();
        List<String> paramTypes = sqlConditions.getParamTypes();
        String wholeWhereClause = (StringUtils.isNotNullNorEmpty(sqlConditions.getWhereClause()) ? " AND " : "") + sqlConditions.getWhereClause() + (moreWhereClause == null ? "" : moreWhereClause);
        String countSql = generateCountSql(tableName,wholeWhereClause);

        LOGGER.info("count sql: {}", countSql);
        String querySql = generateQuerySql(tableName, wholeWhereClause, pageable);
        LOGGER.info("query sql: {}", querySql);

        sqlGenerResultDTO.setCountSql(countSql);
        sqlGenerResultDTO.setQuerySql(querySql);
        sqlGenerResultDTO.setParamsValue(paramsValueList);
        sqlGenerResultDTO.setParamsType(paramTypes);
        return sqlGenerResultDTO ;
    }



    private String generateCountSql(String tableName, String wholeWhereClause) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT COUNT(*) FROM " + tableName + " WHERE 1 = 1 ");
        stringBuilder.append(wholeWhereClause);
        return  stringBuilder.toString();
    }


    private SqlConditions generateSqlConditions(QueryParameterGroupDTO queryParameterGroup, List<DsFormTableSettingDO> dsFormTableSettingDOS) throws Exception {
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
                    D1SQLUtils.buildFuzzyLikeQueryParameterString(fuzzyLike, sqlConditions,dsFormTableSettingDOS);
                }
                if(accurateEqualsString != null && !accurateEqualsString.isEmpty()){
                    D1SQLUtils.buildAccurateEqualsStringQueryParameterString(accurateEqualsString, sqlConditions,dsFormTableSettingDOS);
                }
                if(accurateInString != null && !accurateInString.isEmpty()){
                    D1SQLUtils.buildAccurateInStringQueryParameterString(accurateInString, sqlConditions,dsFormTableSettingDOS);
                }
                if(accurateDateRange != null && !accurateDateRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateRangeQueryParameterString(accurateDateRange, sqlConditions,dsFormTableSettingDOS);
                }
                if(accurateDateTimeRange != null && !accurateDateTimeRange.isEmpty()){
                    D1SQLUtils.buildAccurateDateTimeRangeQueryParameterString(accurateDateTimeRange, sqlConditions,dsFormTableSettingDOS);
                }
                if(accurateNumberRange != null && !accurateNumberRange.isEmpty()){
                    D1SQLUtils.buildAccurateNumberRangeQueryParameterString(accurateNumberRange, sqlConditions,dsFormTableSettingDOS);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to build sql", e);
                throw new ServiceException(String.format("build sql Failed :%s",e.getMessage()));
            }
        }
        return sqlConditions;
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


}
